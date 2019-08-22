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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.LoginController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.handler.verification.AbstractVerification.VerifyCode;
import com.wl4g.devops.iam.handler.verification.GraphBasedVerification;
import com.wl4g.devops.iam.handler.verification.SmsVerification;
import com.wl4g.devops.iam.handler.verification.SmsVerification.MobileNumber;

import static com.wl4g.devops.iam.common.utils.SessionBindings.*;
import static com.wl4g.devops.iam.common.utils.Securitys.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.common.utils.Exceptions.getRootCauseMessage;
import static com.wl4g.devops.common.utils.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_GRAPH_VERIFICATION;
import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_SMS_VERIFICATION;
import static com.wl4g.devops.iam.handler.verification.SmsVerification.*;
import static com.wl4g.devops.iam.handler.verification.SmsVerification.MobileNumber.parse;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * IAM DIABLO extra controller
 *
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
@LoginController
public class LoginAuthenticatorController extends AbstractAuthenticatorController {

	/**
	 * General PreCheck response key-name.
	 */
	final public static String KEY_GENERAL_CHECK_NAME = "checkGeneral";

	/**
	 * Login CAPTCHA token for session.
	 */
	final public static String KEY_GENERAL_CAPTCHA_TOKEN = "captchaToken";

	/**
	 * Encrypted public key requested before login returns key name
	 */
	final public static String KEY_GENERAL_SECRET = "secret";

	/**
	 * SMS PreCheck response key-name.
	 */
	final public static String KEY_SMS_CHECK_NAME = "checkSms";

	/**
	 * Apply SMS verification code to create a time-stamp key-name.
	 */
	final public static String KEY_SMS_CREATE = "createTime";

	/**
	 * The number of milliseconds to wait after applying for an SMS dynamic
	 * password (you can reapply)key-name.
	 */
	final public static String KEY_SMS_DELAY = "delayMs";

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password
	 * key-name.
	 */
	final public static String KEY_SMS_REMAIN = "remainDelayMs";

	/**
	 * Graphic verification handler
	 */
	@Resource(name = BEAN_GRAPH_VERIFICATION)
	protected GraphBasedVerification graphVerification;

	/**
	 * SMS verification handler
	 */
	@Resource(name = BEAN_SMS_VERIFICATION)
	protected SmsVerification smsVerification;

	/**
	 * IAM credentials securer
	 */
	@Autowired
	protected IamCredentialsSecurer securer;

	/**
	 * PreCheck the initial configuration. (e.g: whether to enable the
	 * verification code etc)
	 *
	 * @param request
	 */
	@RequestMapping(value = URI_S_LOGIN_CHECK, method = { GET, POST })
	@ResponseBody
	public RespBase<?> check(HttpServletRequest request) {
		RespBase<Object> resp = RespBase.create();
		try {
			// Login account(Optional)
			String principal = getCleanParam(request, config.getParam().getPrincipalName());
			// Lock factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), principal);

			// Generate CAPTCHA token.
			String captchaToken = EMPTY;
			if (graphVerification.isEnabled(factors)) { // Enabled?
				bind(KEY_GENERAL_CAPTCHA_TOKEN, (captchaToken = randomAlphanumeric(16)));
			}
			String sid = String.valueOf(getSessionId());

			// Apply credentials encryption secret key.
			String secret = securer.applySecret(sid);
			resp.build(KEY_GENERAL_CHECK_NAME).andPut(KEY_GENERAL_SECRET, secret).andPut(KEY_GENERAL_CAPTCHA_TOKEN, captchaToken);

			/*
			 * When the SMS verification code is not empty, this creation
			 * time-stamp is returned (used to display the current remaining
			 * number of seconds before the front end can re-send the SMS
			 * verification code).
			 */
			VerifyCode verifyCode = smsVerification.getVerifyCode(false);
			if (verifyCode != null) {
				resp.build(KEY_SMS_CHECK_NAME).andPut(KEY_SMS_CREATE, verifyCode.getCreateTime())
						.andPut(KEY_SMS_DELAY, config.getMatcher().getFailFastSmsDelay())
						.andPut(KEY_SMS_REMAIN, getRemainingSmsDelay(verifyCode));
			}
		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCauseMessage(e));
			log.error("Failure to initial check", e);
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
		RespBase<Locale> resp = RespBase.create();
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
			resp.setMessage(getRootCauseMessage(e));
			log.error("Failure to apply for locale", e);
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
			// Check CAPTCHA token.
			String reqCapToken = getCleanParam(request, KEY_GENERAL_CAPTCHA_TOKEN);
			String capToken = getBindValue(KEY_GENERAL_CAPTCHA_TOKEN, true);
			Assert.state(isNotBlank(capToken), "Invalid captcha token or expired.");
			Assert.state(trimToEmpty(capToken).equals(reqCapToken), String.format("Illegal captcha token for '%s'", reqCapToken));

			// Login account number or mobile number(Optional)
			String principal = getCleanParam(request, config.getParam().getPrincipalName());
			// Lock factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), principal);

			// Apply CAPTCHA
			if (graphVerification.isEnabled(factors)) { // Enabled?
				graphVerification.apply(factors, request, response);
			} else { // Invalid request
				log.warn("Invalid request, no captcha enabled, factors: {}", factors);
			}

		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to apply captcha.", e);
			} else {
				log.warn("Failed to apply captcha. caused by: {}", getRootCauseMessage(e));
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
		RespBase<Object> resp = RespBase.create();
		try {
			if (!coprocessor.preApplyVerify(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}
			// Login account number or mobile number(Required)
			MobileNumber mn = parse(getCleanParam(request, PARAM_MOBILENUM));
			// Lock factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), mn.asNumberText());

			// Request CAPTCHA
			String captcha = getCleanParam(request, config.getParam().getCaptchaName());
			// Graph validation
			graphVerification.validate(factors, captcha, false);

			// Apply SMS verify-code
			smsVerification.apply(factors, request, response);

			// The creation time of the currently created SMS authentication
			// code (must exist).
			VerifyCode verifyCode = smsVerification.getVerifyCode(true);
			resp.build(KEY_SMS_CHECK_NAME).andPut(KEY_SMS_CREATE, verifyCode.getCreateTime())
					.andPut(KEY_SMS_DELAY, config.getMatcher().getFailFastSmsDelay())
					.andPut(KEY_SMS_REMAIN, getRemainingSmsDelay(verifyCode));

		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCauseMessage(e));
			log.error("Failure to apply for sms verify-code", e);
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
	public RespBase<?> errReads(HttpServletRequest request, HttpServletResponse response) {
		RespBase<String> resp = RespBase.create();
		try {
			// Get error message in session
			String errmsg = getBindValue(KEY_ERR_SESSION_SAVED, true);
			errmsg = isBlank(errmsg) ? "" : errmsg;
			resp.getData().put(KEY_ERR_SESSION_SAVED, errmsg);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(getRootCauseMessage(e));
			log.error("Failed to get errRead.", e);
		}
		return resp;
	}

	/**
	 * Get remaining SMS delay
	 *
	 * @param verifyCode
	 * @return
	 */
	private long getRemainingSmsDelay(VerifyCode verifyCode) {
		// remainMs = NowTime - CreateTime - DelayTime
		long now = System.currentTimeMillis();
		return Math.max(now - verifyCode.getCreateTime() - config.getMatcher().getFailFastSmsDelay(), 0);
	}

}