/*
 * Copyright 2017 ~ 2050 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.annotation.LoginAuthController;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.common.utils.IamSecurityHolder;
import com.wl4g.devops.iam.verification.CompositeSecurityVerifierAdapter;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyCodeWrapper;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyKind;
import com.wl4g.devops.iam.web.model.CaptchaCheckResult;
import com.wl4g.devops.iam.web.model.GenericCheckResult;
import com.wl4g.devops.iam.web.model.SmsCheckResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.wl4g.devops.tool.common.lang.TypeConverts.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.RiskControlSecurityUtils.*;
import static com.wl4g.devops.iam.web.model.CaptchaCheckResult.KEY_CAPTCHA_CHECK;
import static com.wl4g.devops.iam.web.model.GenericCheckResult.KEY_GENERIC_CHECK;
import static com.wl4g.devops.iam.web.model.SmsCheckResult.KEY_SMS_CHECK;
import static com.wl4g.devops.tool.common.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.tool.common.web.WebUtils2.getRFCBaseURI;
import static java.util.Objects.nonNull;
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
	 * Login before environmental security check.
	 *
	 * @param request
	 */
	@RequestMapping(value = URI_S_LOGIN_CHECK, method = { POST })
	@ResponseBody
	public RespBase<?> check(HttpServletRequest request, HttpServletResponse response) {
		RespBase<Object> resp = RespBase.create(sessionStatus());

		//
		// --- Check generic authenticating environments. ---
		//
		// Login account number or mobile number(Optional)
		String principal = getCleanParam(request, config.getParam().getPrincipalName());
		// Limit factors
		List<String> factors = getV1Factors(getHttpRemoteAddr(request), principal);

		// When the login page is loaded, the parameter 'principal' will be
		// empty, no need to generate a key. When submitting the login
		// request parameter 'principal' will not be empty, you need to
		// generate 'secret'.
		GenericCheckResult generic = new GenericCheckResult();
		if (!isBlank(principal)) {
			generic.setSessionKey(config.getCookie().getName());
			generic.setSessionValue(getSession().getId());
			// Apply credentials encryption secret(pubKey)
			generic.setSecret(securer.applySecret(principal));
		}
		// Assign a session ID to the current request. If not, create a new one.
		resp.forMap().put(KEY_GENERIC_CHECK, generic);

		//
		// --- Check captcha authenticating environments. ---
		//
		CaptchaCheckResult captcha = new CaptchaCheckResult(false);
		if (verifier.forOperator(request).isEnabled(factors)) {
			captcha.setEnabled(true);
			captcha.setSupport(VerifyKind.SUPPORT_ALL); // Default
			captcha.setApplyUri(getRFCBaseURI(request, true) + URI_S_VERIFY_BASE + "/" + URI_S_VERIFY_APPLY_CAPTCHA);
		}
		resp.forMap().put(KEY_CAPTCHA_CHECK, captcha);

		//
		// --- Check SMS authenticating environments. ---
		//
		// When the SMS verification code is not empty, this creation
		// time-stamp is returned (used to display the current remaining
		// number of seconds before the front end can re-send the SMS
		// verification code).
		VerifyCodeWrapper code = verifier.forOperator(VerifyKind.TEXT_SMS).getVerifyCode(false);

		// SMS apply owner(mobile number).
		Long mobileNum = null;
		if (nonNull(code)) {
			mobileNum = parseLongOrNull(code.getOwner());
		}

		// Remaining delay.
		Long remainDelay = null;
		if (Objects.nonNull(code)) {
			remainDelay = code.getRemainDelay(config.getMatcher().getFailFastSmsDelay());
		}
		resp.forMap().put(KEY_SMS_CHECK, new SmsCheckResult(nonNull(mobileNum), mobileNum, remainDelay));

		return resp;
	}

	/**
	 * Read the error message from currently session.
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = URI_S_LOGIN_ERRREAD, method = { GET })
	@ResponseBody
	public RespBase<?> readError(HttpServletRequest request, HttpServletResponse response) {
		RespBase<String> resp = RespBase.create(sessionStatus());
		// Check session is required
		//IamSecurityHolder.checkSession();

		// Read error message from session
		String errmsg = getBindValue(KEY_ERR_SESSION_SAVED, true);
		errmsg = isBlank(errmsg) ? "" : errmsg;
		resp.forMap().put(KEY_ERR_SESSION_SAVED, errmsg);

		return resp;
	}

	/**
	 * Apply international locale.</br>
	 * See:{@link com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle}
	 * See:{@link org.springframework.context.support.MessageSourceAccessor}
	 *
	 * @param response
	 */
	@RequestMapping(value = URI_S_LOGIN_APPLY_LOCALE, method = { POST })
	@ResponseBody
	public RespBase<?> applyLocale(HttpServletRequest request, HttpServletResponse response) {
		RespBase<Locale> resp = RespBase.create(sessionStatus());
		// Check session is required
		IamSecurityHolder.checkSession();

		String lang = getCleanParam(request, config.getParam().getI18nLang());
		// Gets apply locale.
		Locale locale = request.getLocale();
		if (isNotBlank(lang)) {
			locale = new Locale(lang);
		}
		bind(KEY_LANG_ATTRIBUTE_NAME, locale);
		resp.forMap().put(KEY_LANG_ATTRIBUTE_NAME, locale);
		return resp;
	}

}