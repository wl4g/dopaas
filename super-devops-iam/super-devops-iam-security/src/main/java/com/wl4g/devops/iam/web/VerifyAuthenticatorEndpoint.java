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

import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.framework.operator.NoSuchOperatorException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.annotation.VerifyAuthController;
import com.wl4g.devops.iam.verification.CompositeSecurityVerifierAdapter;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyCodeWrapper;
import com.wl4g.devops.iam.verification.SmsSecurityVerifier.MobileNumber;
import com.wl4g.devops.iam.verification.model.VerifiedTokenResult;
import com.wl4g.devops.iam.web.model.SmsCheckResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.wl4g.devops.iam.verification.model.VerifiedTokenResult.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.RiskControlSecurityUtils.*;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.sessionStatus;
import static com.wl4g.devops.iam.verification.SecurityVerifier.VerifyKind.TEXT_SMS;
import static com.wl4g.devops.iam.verification.SmsSecurityVerifier.MobileNumber.parse;
import static com.wl4g.devops.iam.web.model.SmsCheckResult.KEY_SMS_CHECK;
import static com.wl4g.devops.tool.common.web.WebUtils2.getHttpRemoteAddr;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * IAM verifier authenticator controller.
 *
 * @author wangl.sir
 * @version v1.0 2019年8月22日
 * @since
 */
@VerifyAuthController
public class VerifyAuthenticatorEndpoint extends AbstractAuthenticatorEndpoint {

	/**
	 * Verify CAPTCHA apply model key-name.
	 */
	final public static String KEY_APPLY_RESULT = "applyModel";

	/**
	 * Composite verifier handler.
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
	@RequestMapping(value = URI_S_VERIFY_APPLY_CAPTCHA, method = { POST })
	@ResponseBody
	public RespBase<?> applyCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		if (!coprocessor.preApplyCapcha(request, response)) {
			throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.accessReject"));
		}

		// LoginId number or mobileNum(Optional)
		String principal = getCleanParam(request, config.getParam().getPrincipalName());
		// Limit factors
		List<String> factors = getV1Factors(getHttpRemoteAddr(request), principal);

		// Apply CAPTCHA
		if (verifier.forOperator(request).isEnabled(factors)) { // Enabled?
			resp.forMap().put(KEY_APPLY_RESULT, verifier.forOperator(request).apply(principal, factors, request));
		} else { // Invalid requestVERIFIED_TOKEN_EXPIREDMS
			log.warn("Invalid request, no captcha enabled, factors: {}", factors);
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
	@RequestMapping(value = URI_S_VERIFY_ANALYSIS_CAPTCHA, method = { POST })
	@ResponseBody
	public RespBase<?> verifyAnalysis(String verifyData, HttpServletRequest request) throws Exception {
		RespBase<Object> resp = RespBase.create(sessionStatus());

		// Limit factors
		List<String> factors = getV1Factors(getHttpRemoteAddr(request), null);
		// Verifying
		String verifiedToken = verifier.forOperator(request).verify(verifyData, request, factors);
		resp.forMap().put(KEY_VWEIFIED_RESULT, new VerifiedTokenResult(true, verifiedToken));

		return resp;
	}

	/**
	 * Apply verification code
	 *
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws NoSuchOperatorException
	 */
	@RequestMapping(value = URI_S_VERIFY_SMS_APPLY, method = { POST })
	@ResponseBody
	public RespBase<?> applySmsCode(HttpServletRequest request, HttpServletResponse response)
			throws NoSuchOperatorException, IOException {
		RespBase<Object> resp = RespBase.create(sessionStatus());
		if (!coprocessor.preApplySmsCode(request, response)) {
			throw new AccessRejectedException(bundle.getMessage("AbstractAttemptsMatcher.accessReject"));
		}

		// Login account number or mobile number(Required)
		MobileNumber mn = parse(getCleanParam(request, config.getParam().getPrincipalName()));
		// Lock factors
		List<String> factors = getV1Factors(getHttpRemoteAddr(request), mn.asNumberText());

		// Graph validation
		verifier.forOperator(request).validate(factors, getCleanParam(request, config.getParam().getVerifiedTokenName()), false);

		// Apply SMS verify code.
		resp.forMap().put(KEY_APPLY_RESULT, verifier.forOperator(TEXT_SMS).apply(mn.asNumberText(), factors, request));

		// The creation time of the currently created SMS authentication
		// code (must exist).
		VerifyCodeWrapper code = verifier.forOperator(TEXT_SMS).getVerifyCode(true);
		resp.forMap().put(KEY_SMS_CHECK,
				new SmsCheckResult(mn.getNumber(), code.getRemainDelay(config.getMatcher().getFailFastSmsDelay())));

		return resp;
	}

}