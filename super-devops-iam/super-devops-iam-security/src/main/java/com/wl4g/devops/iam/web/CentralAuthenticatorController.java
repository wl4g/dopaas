package com.wl4g.devops.iam.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.shiro.util.Assert;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.bean.iam.model.LogoutModel;
import com.wl4g.devops.common.bean.iam.model.SecondAuthcAssertion;
import com.wl4g.devops.common.bean.iam.model.SessionValidationAssertion;
import com.wl4g.devops.common.bean.iam.model.TicketAssertion;
import com.wl4g.devops.common.bean.iam.model.TicketValidationModel;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.exception.iam.InvalidGrantTicketException;
import com.wl4g.devops.common.exception.iam.IllegalApplicationAccessException;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.common.annotation.IamController;
import com.wl4g.devops.iam.common.utils.Sessions;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_LOGOUT_INFO;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_TICKET_ASSERT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SECOND_AUTH_ASSERT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SESSION_VALID_ASSERT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_LOGOUT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_VALIDATE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SECOND_VALIDATE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SESSION_VALIDATE;

/**
 * IAM central authenticator controller
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
@IamController
public class CentralAuthenticatorController extends AbstractAuthenticatorController {

	/**
	 * Verification based on 'cas1' extension protocol.
	 * 
	 * @param param
	 *            TicketValidationRequest parameters
	 * @param bind
	 *            BindingResult
	 * @return TicketAssertion result.
	 */
	@PostMapping(URI_S_VALIDATE)
	@ResponseBody
	public RespBase<TicketAssertion> validate(HttpServletRequest request, @NotNull @RequestBody TicketValidationModel param) {
		if (log.isInfoEnabled()) {
			log.info("Grant ticket validate ... sessionId[{}]", Sessions.getSessionId());
		}

		RespBase<TicketAssertion> resp = new RespBase<>();
		try {
			// Ticket assertion.
			resp.getData().put(KEY_TICKET_ASSERT, authHandler.validate(param));

		} catch (Exception e) {
			log.error("Ticket validate failed. Reason:{}", e.getMessage());
			if (e instanceof InvalidGrantTicketException) {
				/*
				 * Only if the error is not authenticated, can it be redirected
				 * to the IAM server login page, otherwise the client will
				 * display the error page directly (to prevent unlimited
				 * redirection). See:com.wl4g.devops.iam.client.validation.
				 * AbstractBasedTicketValidator#getRemoteValidation()
				 */
				resp.setCode(RetCode.UNAUTHC);
			} else if (e instanceof IllegalApplicationAccessException) {
				resp.setCode(RetCode.UNAUTHZ);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(e.getMessage());
		}

		if (log.isInfoEnabled()) {
			log.info("Ticket validate response: {}", resp);
		}
		return resp;
	}

	/**
	 * Global applications logout all
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(URI_S_LOGOUT)
	@ResponseBody
	public RespBase<LogoutModel> logout(HttpServletRequest request, HttpServletResponse response) {
		if (log.isInfoEnabled()) {
			log.info("Sessions logout ... {}", WebUtils2.getFullRequestURL(request));
		}

		RespBase<LogoutModel> resp = new RespBase<>();
		try {
			// Source application logout processing
			String fromAppName = WebUtils.getCleanParam(request, config.getParam().getApplication());
			Assert.hasText(fromAppName, String.format("'%s' must not be empty", config.getParam().getApplication()));

			// Using coercion ignores remote exit failures
			boolean forced = WebUtils2.isTrue(request, config.getParam().getLogoutForced(), true);
			resp.getData().put(KEY_LOGOUT_INFO, authHandler.logout(forced, fromAppName, request, response));

		} catch (Exception e) {
			if (e instanceof IamException) {
				log.error("Logout server failed. Reason:{}", Exceptions.getRootCauseMessage(e));
			} else {
				log.error("Logout server failed.", e);
			}
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(Exceptions.getRootCauseMessage(e));
		}
		if (log.isInfoEnabled()) {
			log.info("Logout response[{}]", resp);
		}
		return resp;
	}

	/**
	 * Secondary certification validation
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(URI_S_SECOND_VALIDATE)
	@ResponseBody
	public RespBase<SecondAuthcAssertion> seondValidate(HttpServletRequest request) {
		if (log.isInfoEnabled()) {
			log.info("Second authentication validate ... {}", WebUtils2.getFullRequestURL(request));
		}

		RespBase<SecondAuthcAssertion> resp = new RespBase<>();
		try {
			// Required parameters
			String authCode = WebUtils.getCleanParam(request, config.getParam().getSecondAuthCode());
			String fromAppName = WebUtils.getCleanParam(request, config.getParam().getApplication());

			// Secondary authentication assertion.
			resp.getData().put(KEY_SECOND_AUTH_ASSERT, authHandler.secondValidate(authCode, fromAppName));

		} catch (Exception e) {
			log.error("Second authentication validation failed.", e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(e.getMessage());
		}

		if (log.isInfoEnabled()) {
			log.info("Second authentication validate response: {}", resp);
		}
		return resp;
	}

	/**
	 * Sessions expired validation
	 * 
	 * @param param
	 * @return
	 */
	@PostMapping(URI_S_SESSION_VALIDATE)
	@ResponseBody
	public RespBase<SessionValidationAssertion> sessionValidate(@NotNull @RequestBody SessionValidationAssertion param) {
		if (log.isInfoEnabled()) {
			log.info("Sessions expire validate ... {}", param);
		}

		RespBase<SessionValidationAssertion> resp = new RespBase<>();
		try {
			// Session expire validate assertion.
			resp.getData().put(KEY_SESSION_VALID_ASSERT, this.authHandler.sessionValidate(param));

		} catch (Exception e) {
			log.error("Sessions expire validate failed.", e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(e.getMessage());
		}

		if (log.isInfoEnabled()) {
			log.info("Sessions expire validate response: {}", resp);
		}
		return resp;
	}

}
