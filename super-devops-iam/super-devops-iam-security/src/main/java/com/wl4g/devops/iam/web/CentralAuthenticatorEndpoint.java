/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.common.annotation.IamController;
import com.wl4g.devops.iam.common.authc.model.LogoutModel;
import com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel;
import com.wl4g.devops.iam.common.authc.model.SessionValidityAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidatedAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidateModel;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.tool.common.lang.Exceptions;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_LOGOUT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_VALIDATE;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getSessionId;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.tool.common.web.WebUtils2.getFullRequestURL;
import static com.wl4g.devops.tool.common.web.WebUtils2.isTrue;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
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
public class CentralAuthenticatorEndpoint extends AbstractAuthenticatorEndpoint {

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
	public RespBase<TicketValidatedAssertModel<IamPrincipalInfo>> validate(@NotNull @RequestBody TicketValidateModel param) {
		log.info("Ticket validating current sessionId: {} <= {}", getSessionId(), toJSONString(param));

		RespBase<TicketValidatedAssertModel<IamPrincipalInfo>> resp = new RespBase<>();
		// Ticket assertion.
		resp.setData(authHandler.validate(param));

		log.info("Ticket validated => {}", resp);
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
		log.info("Sessions logout <= {}", getFullRequestURL(request));

		RespBase<LogoutModel> resp = new RespBase<>();
		try {
			// Source application logout processing
			String appName = getCleanParam(request, config.getParam().getApplication());
			// hasTextOf(fromAppName, config.getParam().getApplication());

			// Using coercion ignores remote exit failures
			boolean forced = isTrue(request, config.getParam().getLogoutForced(), true);
			resp.setData(authHandler.logout(forced, appName, request, response));
		} catch (Exception e) {
			if (e instanceof IamException) {
				log.error("Failed to logout. caused by:{}", Exceptions.getRootCauseMessage(e));
			} else {
				log.error("Failed to logout.", e);
			}
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(Exceptions.getRootCauseMessage(e));
		}
		log.info("Sessions logout => ", resp);
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
	public RespBase<SecondAuthcAssertModel> secondaryValidate(HttpServletRequest request) {
		log.info("Second authentication validate <= {}", getFullRequestURL(request));

		RespBase<SecondAuthcAssertModel> resp = new RespBase<>();
		try {
			// Required parameters
			String secondAuthCode = WebUtils.getCleanParam(request, config.getParam().getSecondAuthCode());
			String fromAppName = WebUtils.getCleanParam(request, config.getParam().getApplication());
			// Secondary authentication assertion.
			resp.setData(authHandler.secondaryValidate(secondAuthCode, fromAppName));
		} catch (Exception e) {
			log.error("Failed to second authentication validate.", e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(e.getMessage());
		}

		log.info("Second authentication validate => {}", resp);
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
	public RespBase<SessionValidityAssertModel> sessionValidate(@NotNull @RequestBody SessionValidityAssertModel param) {
		log.info("Sessions expire validate <= {}", toJSONString(param));

		RespBase<SessionValidityAssertModel> resp = new RespBase<>();
		try {
			// Session expire validate assertion.
			resp.setData(authHandler.sessionValidate(param));
		} catch (Exception e) {
			log.error("Failed to session expire validate.", e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(e.getMessage());
		}

		log.info("Sessions expire validate => {}", resp);
		return resp;
	}

}