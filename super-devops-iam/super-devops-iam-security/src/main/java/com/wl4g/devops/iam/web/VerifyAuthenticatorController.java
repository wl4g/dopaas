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

import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.VerifyAuthController;
import com.wl4g.devops.iam.verification.CompositeSecurityVerifierAdapter;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyCodeWrapper;
import com.wl4g.devops.iam.verification.SmsSecurityVerifier.MobileNumber;
import com.wl4g.devops.iam.web.model.SmsCheckModel;

import static com.wl4g.devops.iam.verification.SecurityVerifier.VerifyType.*;
import static com.wl4g.devops.iam.web.model.SmsCheckModel.*;
import static com.wl4g.devops.iam.common.utils.Securitys.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.common.utils.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.iam.verification.SmsSecurityVerifier.MobileNumber.parse;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * IAM verifier authenticator controller.
 *
 * @author wangl.sir
 * @version v1.0 2019年8月22日
 * @since
 */
@VerifyAuthController
public class VerifyAuthenticatorController extends AbstractAuthenticatorController {

	/**
	 * Verify CAPTCHA apply model key-name.
	 */
	final public static String KEY_APPLY_MODEL = "applyModel";

	/**
	 * Composite verification handler.
	 */
	@Autowired
	protected CompositeSecurityVerifierAdapter verifier;

	/**
	 * Apply CAPTCHA.
	 *
	 * @param param
	 *            CAPTCHA parameter, required
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = URI_S_VERIFY_APPLY_CAPTCHA, method = { GET, POST })
	@ResponseBody
	public RespBase<?> applyCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		try {
			if (!coprocessor.preApplyCapcha(request, response)) {
				throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
			}
			// LoginId number or mobileNum(Optional)
			String principal = getCleanParam(request, config.getParam().getPrincipalName());
			// Limit factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), principal);

			// Apply CAPTCHA
			if (verifier.forAdapt(request).isEnabled(factors)) { // Enabled?
				resp.getData().put(KEY_APPLY_MODEL, verifier.forAdapt(request).apply(principal, factors, request));
			} else { // Invalid requestVERIFIED_TOKEN_EXPIREDMS
				log.warn("Invalid request, no captcha enabled, factors: {}", factors);
			}

		} catch (Exception e) {
			String errmsg = getRootCausesString(e);
			resp.setCode(RetCode.SYS_ERR).setMessage(errmsg);
			if (log.isDebugEnabled()) {
				log.debug("Failed to apply captcha.", e);
			} else {
				log.warn("Failed to apply captcha. caused by: {}", errmsg);
			}
		}

		return resp;
	}

	/**
	 * Verify CAPTCHA code.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = URI_S_VERIFY_ANALYZE_CAPTCHA, method = { GET, POST })
	@ResponseBody
	public void verifyCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			// Limit factors
			List<String> factors = createLimitFactors(getHttpRemoteAddr(request), null);
			verifier.forAdapt(request).verify(request, factors);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to verify captcha.", e);
			} else {
				log.warn("Failed to verify captcha. caused by: {}", getRootCausesString(e));
			}
		}
	}

	/**
	 * Apply verification code
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = URI_S_VERIFY_SMS_APPLY, method = { GET, POST })
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
			verifier.forAdapt(request).validate(factors, getCleanParam(request, config.getParam().getVerifiedTokenName()), false);

			// Apply SMS verify code.
			resp.getData().put(KEY_APPLY_MODEL, verifier.forAdapt(TEXT_SMS).apply(mn.asNumberText(), factors, request));

			// The creation time of the currently created SMS authentication
			// code (must exist).
			VerifyCodeWrapper code = verifier.forAdapt(TEXT_SMS).getVerifyCode(true);
			resp.getData().put(KEY_SMS_CHECK,
					new SmsCheckModel(mn.getNumber(), code.getRemainDelay(config.getMatcher().getFailFastSmsDelay())));
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

}