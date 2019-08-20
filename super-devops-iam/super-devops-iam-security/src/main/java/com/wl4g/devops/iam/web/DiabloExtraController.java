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

import com.google.common.base.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.ExtraController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.handler.verification.AbstractVerification.VerifyCode;
import com.wl4g.devops.iam.handler.verification.GraphBasedVerification;
import com.wl4g.devops.iam.handler.verification.SmsVerification;
import static com.wl4g.devops.iam.common.utils.SessionBindings.*;
import static com.wl4g.devops.iam.common.utils.Securitys.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.common.utils.Exceptions.getRootCauseMessage;
import static com.wl4g.devops.common.utils.Exceptions.getRootCauses;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.common.utils.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_GRAPH_VERIFICATION;
import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_SMS_VERIFICATION;
import static com.wl4g.devops.iam.handler.verification.SmsVerification.MobileNumber.parse;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

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
	@RequestMapping(value = URI_S_EXT_CHECK, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public RespBase<?> check(HttpServletRequest request) {
		RespBase<Object> resp = RespBase.create();
		try {
			// Login account number or mobile number(Optional)
			String principal = getCleanParam(request, config.getParam().getPrincipalName());

			// Lock factors
			List<String> factors = createFactors(getHttpRemoteAddr(request), principal);

			// Get the CAPTCHA enabled
			String captchaEnabled = graphVerification.isEnabled(factors) ? "yes" : "no";

			/*
			 * When the login page is loaded, the parameter 'principal' will be
			 * empty, no need to generate a key. When submitting the login
			 * request parameter 'principal' will not be empty, you need to
			 * generate 'secret'.
			 */
			String secret = EMPTY;
			if (isNotBlank(principal)) {
				// Apply credentials encryption secret key
				secret = securer.applySecret(principal);
			}
			resp.getData().put(GeneralCheckResp.KEY_CHECKER, new GeneralCheckResp(captchaEnabled, secret));

			/*
			 * When the SMS verification code is not empty, this creation
			 * time-stamp is returned (used to display the current remaining
			 * number of seconds before the front end can re-send the SMS
			 * verification code).
			 */
			VerifyCode verifyCode = smsVerification.getVerifyCode(false);
			if (verifyCode != null) {
				resp.getData().put(SMSVerifyCheckResp.KEY_CHECKER, new SMSVerifyCheckResp(verifyCode.getCreateTime(),
						config.getMatcher().getFailFastSmsDelay(), getRemainingSmsDelay(verifyCode)));
			}

		} catch (Exception e) {
			if (e instanceof IamException) {
				resp.setCode(RetCode.BIZ_ERR);
			} else {
				resp.setCode(RetCode.SYS_ERR);
			}
			resp.setMessage(getRootCauses(e).getMessage());
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
			String lang = getCleanParam(request, config.getParam().getI18nLang());

			Locale locale = request.getLocale(); // default lang
			if (isNotBlank(lang)) {
				locale = new Locale(lang); // determine lang
			}
			bind(KEY_USE_LOCALE, locale);

			resp.getData().put(KEY_USE_LOCALE, locale);
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
			String principal = getCleanParam(request, config.getParam().getPrincipalName());

			// Lock factors
			List<String> factors = createFactors(getHttpRemoteAddr(request), principal);

			// Apply CAPTCHA
			if (graphVerification.isEnabled(factors)) { // Enabled?
				graphVerification.apply(factors, request, response);
			}
			// Invalid request
			else {
				String errmsg = "Currently no captcha is required, it is recommended that the front end reduce invalid requests";
				log.warn("{} factors: {}", errmsg, factors);
				throw new IamException(String.format("%s", errmsg));
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
			resp.setMessage(getRootCauseMessage(e));

			if (log.isDebugEnabled()) {
				log.debug("Failed to apply for captcha.", e);
			} else {
				log.warn("Failed to apply for captcha. caused by: {}", getRootCauseMessage(e));
			}

			// Respond to the JSON message that failed to apply for the CAPTCHA
			write(response, HttpStatus.OK.value(), APPLICATION_JSON_UTF8_VALUE, toJSONString(resp).getBytes(Charsets.UTF_8));
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
		RespBase<Object> resp = RespBase.create();
		try {
			if (!coprocessor.preApplyVerify(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}

			// Login account number or mobile number(Required)
			String mobileName = config.getParam().getPrincipalName();
			String mobileNumber = getCleanParam(request, mobileName);
			parse(mobileNumber);

			// Lock factors
			List<String> factors = createFactors(getHttpRemoteAddr(request), mobileNumber);

			// Request CAPTCHA
			String captcha = getCleanParam(request, config.getParam().getCaptchaName());
			// Graph validation
			graphVerification.validate(factors, captcha, false);

			// Apply SMS verify-code
			smsVerification.apply(factors, request, response);

			/*
			 * The creation time of the currently created SMS authentication
			 * code (must exist).
			 */
			VerifyCode verifyCode = smsVerification.getVerifyCode(true);
			resp.getData().put(SMSVerifyCheckResp.KEY_CHECKER, new SMSVerifyCheckResp(verifyCode.getCreateTime(),
					config.getMatcher().getFailFastSmsDelay(), getRemainingSmsDelay(verifyCode)));

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
	@GetMapping(URI_S_EXT_ERRREAD)
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

	/**
	 * General PreCheck response.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月20日
	 * @since
	 */
	public static class GeneralCheckResp implements Serializable {
		private static final long serialVersionUID = -5279195217830694101L;

		final public static String KEY_CHECKER = "checkGeneral";

		/**
		 * Control whether the validation code key name is enabled
		 */
		private String captchaEnabled;

		/**
		 * Encrypted public key requested before login returns key name
		 */
		private String secret;

		public GeneralCheckResp() {
			super();
		}

		public GeneralCheckResp(String captchaEnabled, String secret) {
			super();
			this.captchaEnabled = captchaEnabled;
			this.secret = secret;
		}

		public String getCaptchaEnabled() {
			return captchaEnabled;
		}

		public void setCaptchaEnabled(String captchaEnabled) {
			this.captchaEnabled = captchaEnabled;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

	}

	/**
	 * SMS PreCheck response.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月20日
	 * @since
	 */
	public static class SMSVerifyCheckResp implements Serializable {
		private static final long serialVersionUID = -5279195217830694103L;

		final public static String KEY_CHECKER = "checkSms";

		/**
		 * Apply SMS verification code to create a timestamp
		 */
		private long createTime;

		/**
		 * The number of milliseconds to wait after applying for an SMS dynamic
		 * password (you can reapply).
		 */
		private long delayMs;

		/**
		 * The remaining milliseconds to wait to re-apply for SMS dynamic
		 * password
		 */
		private long remainDelayMs;

		public SMSVerifyCheckResp() {
			super();
		}

		public SMSVerifyCheckResp(long createTime, long delayMs, long remainDelayMs) {
			super();
			this.createTime = createTime;
			this.delayMs = delayMs;
			this.remainDelayMs = remainDelayMs;
		}

		public long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

		public long getDelayMs() {
			return delayMs;
		}

		public void setDelayMs(long delayMs) {
			this.delayMs = delayMs;
		}

		public long getRemainDelayMs() {
			return remainDelayMs;
		}

		public void setRemainDelayMs(long remainDelayMs) {
			this.remainDelayMs = remainDelayMs;
		}

	}

}