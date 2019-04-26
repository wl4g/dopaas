/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR factors OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.util.Assert;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.ExtraController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.common.utils.SessionBindings;
import com.wl4g.devops.iam.handler.verification.AbstractVerification.VerifyCode;
import com.wl4g.devops.iam.handler.verification.GraphBasedVerification;
import com.wl4g.devops.iam.handler.verification.SmsVerification;

import static com.wl4g.devops.common.utils.web.WebUtils2.*;
import static com.wl4g.devops.iam.handler.verification.SmsVerification.MobileNumber.parse;
import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_GRAPH_VERIFICATION;
import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_SMS_VERIFICATION;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.lockFactors;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_CHECK;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_CAPTCHA_APPLY;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_VERIFY_APPLY;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_LOCALE_APPLY;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_EXT_ERRREAD;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_USE_LOCALE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_ERR_SESSION_SAVED;

import java.util.List;
import java.util.Locale;

/**
 * IAM DIABLO extra controller
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
@ExtraController
public class DiabloExtraController extends AbstractAuthenticatorController {

	/**
	 * Control whether the validation code key name is enabled
	 */
	final public static String KEY_CAPTCHA_ENABLED = "captchaEnabled";

	/**
	 * Encrypted public key requested before login returns key name
	 */
	final public static String KEY_APPLY_SECRET = "secret";

	/**
	 * Apply SMS verification code to create a timestamp
	 */
	final public static String KEY_VERIFYCODE_CREATE = "verifyCodeCreateTime";

	/**
	 * The number of milliseconds to wait after applying for an SMS dynamic
	 * password (you can reapply).
	 * 
	 */
	final public static String KEY_VERIFYCODE_DELAY = "verifyCodeDelayMs";

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password
	 */
	final public static String KEY_VERIFYCODE_REMAIN_DELAY = "verifyCodeRemainDelayMs";

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
	 * Check the initial configuration. (e.g: whether to enable the verification
	 * code etc)
	 * 
	 * @param request
	 */
	@RequestMapping(value = URI_S_EXT_CHECK, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public RespBase<?> check(HttpServletRequest request) {
		RespBase<String> resp = RespBase.create();
		try {
			// Login account number or mobile number(Optional)
			String principal = WebUtils.getCleanParam(request, config.getParam().getPrincipalName());

			// Lock factors
			List<String> factors = lockFactors(getHttpRemoteAddr(request), principal);

			// Get the CAPTCHA enabled
			String captchaEnabled = graphVerification.isEnabled(factors) ? "yes" : "no";
			resp.getData().put(KEY_CAPTCHA_ENABLED, captchaEnabled);

			/*
			 * When the login page is loaded, the parameter 'principal' will be
			 * empty, no need to generate a key. When submitting the login
			 * request parameter 'principal' will not be empty, you need to
			 * generate 'secret'.
			 */
			if (!StringUtils.isEmpty(principal)) {
				// Apply credentials encryption secret key
				String secret = securer.applySecret(principal);
				resp.getData().put(KEY_APPLY_SECRET, secret);
			}

			/*
			 * When the SMS verification code is not empty, this creation
			 * timestamp is returned (used to display the current remaining
			 * number of seconds before the front end can resend the SMS
			 * verification code).
			 */
			VerifyCode verifyCode = smsVerification.getVerifyCode(false);
			if (verifyCode != null) {
				resp.getData().put(KEY_VERIFYCODE_CREATE, String.valueOf(verifyCode.getCreateTime()));
				resp.getData().put(KEY_VERIFYCODE_DELAY, String.valueOf(config.getMatcher().getFailFastSmsDelay()));
				resp.getData().put(KEY_VERIFYCODE_REMAIN_DELAY, String.valueOf(getRemainingSmsDelay(verifyCode)));
			}

		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(Exceptions.getRootCauses(e).getMessage());
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
	@RequestMapping(value = URI_S_EXT_LOCALE_APPLY, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public RespBase<?> applyLocale(HttpServletRequest request) {
		RespBase<Locale> resp = RespBase.create();
		try {
			String lang = WebUtils.getCleanParam(request, config.getParam().getLanguage());
			Assert.hasText(lang, String.format("'%s' must not be empty", config.getParam().getLanguage()));

			Locale locale = new Locale(lang);
			SessionBindings.bind(KEY_USE_LOCALE, locale);

			resp.getData().put(KEY_USE_LOCALE, locale);
		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.PARAM_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(Exceptions.getRootCauseMessage(e));
			log.error("Failure to apply for locale", e);
		}
		return resp;
	}

	/**
	 * Apply CAPTCHA graph stream.
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = URI_S_EXT_CAPTCHA_APPLY, method = { RequestMethod.GET, RequestMethod.POST })
	public void applyCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			if (!coprocessor.preApplyCapcha(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}

			// Login account number or mobile number(Optional)
			String principal = WebUtils.getCleanParam(request, config.getParam().getPrincipalName());

			// Lock factors
			List<String> factors = lockFactors(getHttpRemoteAddr(request), principal);

			// Apply CAPTCHA
			if (graphVerification.isEnabled(factors)) { // Enabled?
				graphVerification.apply(factors, request, response);
			} else { // Invalid request
				log.warn(
						"Currently no captcha is required, it is recommended that the front end reduce invalid requests. factors: {}",
						factors);
				throw new IamException(String.format(
						"Currently no captcha is required, it is recommended that the front end reduce invalid requests."));
			}

		} catch (Exception e) {
			RespBase<?> resp = RespBase.create();
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
				if (e instanceof VerificationException) {
					resp.setCode(RetCode.LOCKD_ERR);
				}
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(e.getMessage());
			log.error("Failure to apply for captcha", e);

			// Respond to the JSON message that failed to apply for the CAPTCHA
			write(response, HttpStatus.OK.value(), MediaType.APPLICATION_JSON_UTF8_VALUE,
					JacksonUtils.toJSONString(resp).getBytes(Charsets.UTF_8));
		}
	}

	/**
	 * Apply verification code
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = URI_S_EXT_VERIFY_APPLY, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public RespBase<?> applyVerify(HttpServletRequest request, HttpServletResponse response) {
		RespBase<String> resp = RespBase.create();
		try {
			if (!coprocessor.preApplyVerify(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}

			// Login account number or mobile number(Required)
			String mobileName = config.getParam().getPrincipalName();
			String mobileNumber = WebUtils.getCleanParam(request, mobileName);
			parse(mobileNumber);

			// Lock factors
			List<String> factors = lockFactors(getHttpRemoteAddr(request), mobileNumber);

			// Request CAPTCHA
			String captcha = WebUtils.getCleanParam(request, config.getParam().getCaptchaName());
			// Graph validation
			graphVerification.validate(factors, captcha, false);

			// Apply SMS verify-code
			smsVerification.apply(factors, request, response);

			/*
			 * The creation time of the currently created SMS authentication
			 * code (must exist).
			 */
			VerifyCode verifyCode = smsVerification.getVerifyCode(true);
			resp.getData().put(KEY_VERIFYCODE_CREATE, String.valueOf(verifyCode.getCreateTime()));
			resp.getData().put(KEY_VERIFYCODE_DELAY, String.valueOf(config.getMatcher().getFailFastSmsDelay()));
			resp.getData().put(KEY_VERIFYCODE_REMAIN_DELAY, String.valueOf(getRemainingSmsDelay(verifyCode)));

		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(e.getMessage());
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
	@GetMapping(URI_S_EXT_ERRREAD)
	@ResponseBody
	public RespBase<?> errReads(HttpServletRequest request, HttpServletResponse response) {
		RespBase<String> resp = RespBase.create();
		try {
			// Get error message in session
			String errmsg = SessionBindings.getBindValue(KEY_ERR_SESSION_SAVED, true);
			errmsg = StringUtils.isEmpty(errmsg) ? "" : errmsg;
			resp.getData().put(KEY_ERR_SESSION_SAVED, errmsg);
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(Exceptions.getRootCauses(e).getMessage());
			log.error("Get error on session failed", e);
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