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

import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.LoginAuthController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.verification.CompositeSecurityVerifierAdapter;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyCodeWrapper;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyType;
import com.wl4g.devops.iam.web.model.AuthenticationCodeModel;
import com.wl4g.devops.iam.web.model.CaptchaCheckModel;
import com.wl4g.devops.iam.web.model.GeneralCheckModel;
import com.wl4g.devops.iam.web.model.SmsCheckModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.common.utils.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.common.utils.web.WebUtils2.getRFCBaseURI;
import static com.wl4g.devops.iam.common.utils.Securitys.createLimitFactors;
import static com.wl4g.devops.iam.common.utils.Securitys.sessionStatus;
import static com.wl4g.devops.iam.common.utils.SessionBindings.*;
import static com.wl4g.devops.iam.web.model.AuthenticationCodeModel.*;
import static com.wl4g.devops.iam.web.model.CaptchaCheckModel.KEY_CAPTCHA_CHECK;
import static com.wl4g.devops.iam.web.model.GeneralCheckModel.KEY_GENERAL_CHECK;
import static com.wl4g.devops.iam.web.model.SmsCheckModel.KEY_SMS_CHECK;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
	 * Apply authentication code,
	 * 
	 * @param request
	 */
	@RequestMapping(value = URI_S_LOGIN_APPLY_AUTHCODE, method = { GET, POST })
	@ResponseBody
	public RespBase<?> applyAuthenticationCode(HttpServletRequest request) {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		try {
			/**
			 * Generate authentication code, using for sign in.
			 */
			String authCode = "acde" + randomAlphabetic(46);
			if (log.isDebugEnabled()) {
				log.debug("Apply authentication code: '{}'", authCode);
			}
			cacheManager.getCache(CACHE_AUTH_CODE).put(new EnhancedKey(authCode, 600), "");
			resp.getData().put(KEY_AUTHENTICATION_MODEL, new AuthenticationCodeModel(authCode));
		} catch (Exception e) {
			resp.handleError(e);
			log.error("Failed to apply session.", e);
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
	public RespBase<?> safeCheck(HttpServletRequest request, @Validated AuthenticationCodeModel authCode) {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		try {
			// Limit factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), null);

			// Secret(pubKey).
			resp.getData().put(KEY_GENERAL_CHECK, new GeneralCheckModel(securer.applySecret(authCode.getAuthenticationCode())));

			// CAPTCHA check.
			CaptchaCheckModel model = new CaptchaCheckModel(false);
			if (verifier.forAdapt(request).isEnabled(factors)) {
				model.setEnabled(true);
				model.setSupport(VerifyType.SUPPORT_ALL); // Default
				model.setApplyUri(getRFCBaseURI(request, true) + URI_S_VERIFY_BASE + "/" + URI_S_VERIFY_APPLY_CAPTCHA);
			}
			resp.getData().put(KEY_CAPTCHA_CHECK, model);

			// SMS check.
			/*
			 * When the SMS verification code is not empty, this creation
			 * time-stamp is returned (used to display the current remaining
			 * number of seconds before the front end can re-send the SMS
			 * verification code).
			 */
			VerifyCodeWrapper code = verifier.forAdapt(VerifyType.TEXT_SMS).getVerifyCode(false);

			// SMS apply owner(mobile number).
			Long mobileNum = null;
			if (code != null && code.getOwner() != null && isNumeric(code.getOwner())) {
				mobileNum = Long.parseLong(code.getOwner());
			}

			// Remaining delay.
			Long remainDelay = null;
			if (Objects.nonNull(code)) {
				remainDelay = code.getRemainDelay(config.getMatcher().getFailFastSmsDelay());
			}
			resp.getData().put(KEY_SMS_CHECK, new SmsCheckModel(mobileNum != null, mobileNum, remainDelay));

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

}