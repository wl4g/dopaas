/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.LoginAuthController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.verification.CompositeSecurityVerifierAdapter;
import com.wl4g.devops.iam.verification.SmsSecurityVerifier.MobileNumber;
import com.wl4g.devops.iam.web.model.CaptchaCheckModel;
import com.wl4g.devops.iam.web.model.GeneralCheckModel;
import com.wl4g.devops.iam.web.model.SmsCheckModel;

import static com.wl4g.devops.iam.web.model.CaptchaCheckModel.*;
import static com.wl4g.devops.iam.web.model.GeneralCheckModel.*;
import static com.wl4g.devops.iam.web.model.SmsCheckModel.*;
import static com.wl4g.devops.iam.common.utils.SessionBindings.*;
import static com.wl4g.devops.iam.common.utils.Securitys.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.common.utils.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.common.utils.web.WebUtils2.getRFCBaseURI;
import static com.wl4g.devops.iam.verification.SmsSecurityVerifier.*;
import static com.wl4g.devops.iam.verification.SmsSecurityVerifier.MobileNumber.parse;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * IAM login extra controller
 *
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
@LoginAuthController
public class LoginAuthenticatorController extends AbstractAuthenticatorController {

	/**
	 * Composite verification handler.
	 */
	@Autowired
	protected CompositeSecurityVerifierAdapter verifier;

	/**
	 * IAM credentials securer
	 */
	@Autowired
	protected IamCredentialsSecurer securer;

	/**
	 * Apply session, applicable to mobile token session.
	 * 
	 * @param request
	 */
	@RequestMapping(value = URI_S_LOGIN_APPLY_SESSION, method = { GET, POST })
	@ResponseBody
	public RespBase<?> applySession(HttpServletRequest request) {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		try {
			resp.getData().put(config.getCookie().getName(), getSessionId());
		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCausesString(e));
			log.error("Failed to apply session.", e);
		}
		return resp;
	}

	/**
	 * Apply international locale.</br>
	 * See:{@link com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle}
	 * See:{@link org.springframework.context.support.MessageSourceAccessor}
	 *
	 * @param response
	 */
	@RequestMapping(value = URI_S_LOGIN_APPLY_LOCALE, method = { GET, POST })
	@ResponseBody
	public RespBase<?> applyLocale(HttpServletRequest request) {
		RespBase<Locale> resp = RespBase.create(sessionStatus());
		try {
			String lang = getCleanParam(request, config.getParam().getI18nLang());

			Locale locale = request.getLocale(); // by default
			if (isNotBlank(lang)) {
				locale = new Locale(lang);
			}
			bind(KEY_LANG_ATTRIBUTE_NAME, locale);
			resp.getData().put(KEY_LANG_ATTRIBUTE_NAME, locale);
		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.PARAM_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCausesString(e));
			log.error("Failed to apply for locale", e);
		}
		return resp;
	}

	/**
	 * Login before environmental security check.
	 *
	 * @param request
	 */
	@RequestMapping(value = URI_S_LOGIN_CHECK, method = { GET, POST })
	@ResponseBody
	public RespBase<?> safeCheck(HttpServletRequest request) {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		try {
			// Limit factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), null);

			// CAPTCHA.
			CaptchaCheckModel model = new CaptchaCheckModel(false);
			if (verifier.isEnabled(factors)) { // Enabled?
				model.setEnabled(true);
				model.setType(CAPTCHA_SIMPLE_TPYE); // Default
				String url = getRFCBaseURI(request, true) + URI_S_LOGIN_BASE + "/" + URI_S_LOGIN_APPLY_CAPTCHA;
				model.setApplyUrl(url);
			}
			resp.getData().put(KEY_CAPTCHA_CHECK, model);

			// Secret credentials(pubKey).
			resp.getData().put(KEY_GENERAL_CHECK, new GeneralCheckModel(securer.applySecret()));

			/*
			 * When the SMS verification code is not empty, this creation
			 * time-stamp is returned (used to display the current remaining
			 * number of seconds before the front end can re-send the SMS
			 * verification code).
			 */
			Long remainingDelay = null;
			VerifyCodeWrapper<?> code = verifier.getVerifyCode(false);
			if (code != null) {
				remainingDelay = getSmsRemainingDelay(code);
			}

			// SMS apply owner(mobile number).
			Long mobileNum = null;
			if (code != null && code.getOwner() != null && isNumeric(code.getOwner())) {
				mobileNum = Long.parseLong(code.getOwner());
			}
			resp.getData().put(KEY_SMS_CHECK, new SmsCheckModel(mobileNum != null, mobileNum, remainingDelay));

		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCausesString(e));
			log.error("Failed to safety check.", e);
		}
		return resp;
	}

	/**
	 * Apply CAPTCHA graph stream.
	 *
	 * @param param
	 *            CAPTCHA parameter, required
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = URI_S_LOGIN_APPLY_CAPTCHA, method = { GET, POST })
	public void applyCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			if (!coprocessor.preApplyCapcha(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}
			// LoginId number or mobileNum(Optional)
			String principal = getCleanParam(request, config.getParam().getPrincipalName());
			// Limit factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), principal);

			// Apply CAPTCHA
			if (verifier.isEnabled(factors)) { // Enabled?
				verifier.apply(null, factors, request, response);
			} else { // Invalid request
				log.warn("Invalid request, no captcha enabled, factors: {}", factors);
			}

		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to apply captcha.", e);
			} else {
				log.warn("Failed to apply captcha. caused by: {}", getRootCausesString(e));
			}
		}
	}

	/**
	 * Apply verification code
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = URI_S_LOGIN_SMS_APPLY, method = { GET, POST })
	@ResponseBody
	public RespBase<?> applySmsVerify(HttpServletRequest request, HttpServletResponse response) {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		try {
			if (!coprocessor.preApplyVerify(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}
			// Login account number or mobile number(Required)
			MobileNumber mn = parse(getCleanParam(request, config.getParam().getPrincipalName()));
			// Lock factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), mn.asNumberText());

			// Graph validation
			verifier.validate(factors, getCleanParam(request, config.getParam().getAttachCodeName()), false);

			// Apply SMS verify code.
			verifier.apply(mn.asNumberText(), factors, request, response);

			// The creation time of the currently created SMS authentication
			// code (must exist).
			VerifyCodeWrapper<?> code = verifier.getVerifyCode(true);
			resp.getData().put(KEY_SMS_CHECK, new SmsCheckModel(mn.getNumber(), getSmsRemainingDelay(code)));
		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCausesString(e));
			log.error("Failed to apply for sms verify-code", e);
		}
		return resp;
	}

	/**
	 * Read the error message stored in the current session.
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = URI_S_LOGIN_ERRREAD, method = { GET, POST })
	@ResponseBody
	public RespBase<?> errorRead(HttpServletRequest request, HttpServletResponse response) {
		RespBase<String> resp = RespBase.create(sessionStatus());
		try {
			// Get error message in session
			String errmsg = getBindValue(KEY_ERR_SESSION_SAVED, true);
			errmsg = isBlank(errmsg) ? "" : errmsg;
			resp.getData().put(KEY_ERR_SESSION_SAVED, errmsg);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(getRootCausesString(e));
			log.error("Failed to error reads.", e);
		}
		return resp;
	}

	/**
	 * Get remaining SMS delay
	 *
	 * @param code
	 * @return
	 */
	private long getSmsRemainingDelay(VerifyCodeWrapper<?> code) {
		// remainMs = NowTime - CreateTime - DelayTime
		long now = System.currentTimeMillis();
		return Math.max(config.getMatcher().getFailFastSmsDelay() - (now - code.getCreateTime()), 0);
	}

}