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
package com.wl4g.devops.iam.verification;

import static com.wl4g.devops.iam.common.utils.SessionBindings.bind;
import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;
import static com.wl4g.devops.iam.verification.cumulation.CumulateHolder.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.config.IamProperties.MatcherProperties;
import com.wl4g.devops.iam.verification.cumulation.Cumulator;

import org.springframework.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * Abstract graphic verification code handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class GraphBasedSecurityVerifier extends AbstractSecurityVerifier implements InitializingBean {

	/**
	 * Apply CAPTCHA image UUID parameter name.
	 */
	final public static String PARAM_APPLY_UUID = "applyUuid";

	/**
	 * Apply UUID expireMs.
	 */
	final public static long APPLY_UUID_EXPIREMS = 15_000;

	/**
	 * Apply UUID bit.
	 */
	final public static int APPLY_UUID_BIT = 32;

	/**
	 * Matching attempts accumulator
	 */
	private Cumulator matchCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator.(Session-based)
	 */
	private Cumulator sessionMatchCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator
	 */
	private Cumulator applyCaptchaCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator.(Session-based)
	 */
	private Cumulator sessionApplyCaptchaCumulator;

	/**
	 * {@link com.google.code.kaptcha.servlet.KaptchaServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 */
	@Override
	public void apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request) {
		// Check limit attempts
		checkApplyAttempts(request, factors);
		// Renew or cleanup CAPTCHA
		reset(owner, true);

		// Check and generate apply UUID.
		if (getVerifyCode(true) != null) {
			bind(PARAM_APPLY_UUID, randomAlphabetic(APPLY_UUID_BIT), APPLY_UUID_EXPIREMS);
		}
	}

	@Override
	public void render(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
		// Check apply UUID.
		String storedApplyUuid = getBindValue(PARAM_APPLY_UUID, true);
		Assert.hasText(storedApplyUuid, "Apply graphic captcha uuid has expired.");
		Assert.isTrue(storedApplyUuid.equals(getCleanParam(request, PARAM_APPLY_UUID)), "Invalid graphic captcha apply uuid.");

		// Set to expire far in the past.
		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// Response a JPEG
		response.setContentType("image/jpeg");

		// Create the text for the image and output CAPTCHA image buffer.
		imageWrite(request, response, getVerifyCode(true).getCode());
	}

	@Override
	public boolean isEnabled(@NotNull List<String> factors) {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");
		int enabledCaptchaMaxAttempts = config.getMatcher().getEnabledCaptchaMaxAttempts();

		// Cumulative number of matches based on cache, If the number of
		// failures exceeds the upper limit, verification is enabled
		Long matchCount = matchCumulator.getCumulatives(factors);
		if (log.isInfoEnabled()) {
			log.info("Logon match count: {}, factors: {}", matchCount, factors);
		}
		// Login matching failures exceed the upper limit.
		if (matchCount >= enabledCaptchaMaxAttempts) {
			return true;
		}

		// Cumulative number of matches based on session.
		long sessionMatchCount = sessionMatchCumulator.getCumulatives(factors);
		if (log.isInfoEnabled()) {
			log.info("Logon session match count: {}, factors: {}", sessionMatchCount, factors);
		}

		// Graphic verify-code apply over the upper limit.
		if (sessionMatchCount >= enabledCaptchaMaxAttempts) {
			return true;
		}

		return false;
	}

	@Override
	protected long getVerifyCodeExpireMs() {
		return config.getMatcher().getCaptchaExpireMs();
	}

	@Override
	protected void checkApplyAttempts(@NotNull HttpServletRequest request, @NotNull List<String> factors) {
		int failFastCaptchaMaxAttempts = config.getMatcher().getFailFastCaptchaMaxAttempts();

		// Cumulative number of applications based on caching.
		long applyCaptchaCount = applyCaptchaCumulator.accumulate(factors, 1);
		if (log.isInfoEnabled()) {
			log.info("Check graph verify-code apply, for apply count : {}", applyCaptchaCount);
		}
		if (applyCaptchaCount >= failFastCaptchaMaxAttempts) {
			log.warn("Too many times to apply for graph verify-code, actual: {}, maximum: {}, factors: {}", applyCaptchaCount,
					failFastCaptchaMaxAttempts, factors);
			throw new VerificationException(bundle.getMessage("GraphBasedVerification.locked"));
		}

		// Cumulative number of applications based on session
		long sessionApplyCaptchaCount = sessionApplyCaptchaCumulator.accumulate(factors, 1);
		if (log.isInfoEnabled()) {
			log.info("Check graph verify-code apply, for session apply count : {}", sessionApplyCaptchaCount);
		}
		// Exceeding the limit
		if (sessionApplyCaptchaCount >= failFastCaptchaMaxAttempts) {
			log.warn("Too many times to apply for session graph verify-code, actual: {}, maximum: {}, factors: {}",
					sessionApplyCaptchaCount, failFastCaptchaMaxAttempts, factors);
			throw new VerificationException(bundle.getMessage("GraphBasedVerification.locked"));
		}

	}

	/**
	 * Write output CAPTCHA buffer image
	 * 
	 * @param request
	 * @param response
	 * @param reqCode
	 * @return
	 */
	protected abstract void imageWrite(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object reqCode)
			throws IOException;

	@Override
	public void afterPropertiesSet() throws Exception {
		MatcherProperties matcher = config.getMatcher();
		// Match accumulator.
		this.matchCumulator = newCumulator(getCache(CACHE_FAILFAST_MATCH_COUNTER), matcher.getFailFastMatchDelay());
		this.sessionMatchCumulator = newSessionCumulator(CACHE_FAILFAST_MATCH_COUNTER, matcher.getFailFastMatchDelay());

		// CAPTCHA accumulator.
		this.applyCaptchaCumulator = newCumulator(getCache(CACHE_FAILFAST_CAPTCHA_COUNTER), matcher.getFailFastCaptchaDelay());
		this.sessionApplyCaptchaCumulator = newSessionCumulator(CACHE_FAILFAST_CAPTCHA_COUNTER,
				matcher.getFailFastCaptchaDelay());

		Assert.notNull(matchCumulator, "matchCumulator is null, please check configure");
		Assert.notNull(sessionMatchCumulator, "sessionMatchCumulator is null, please check configure");
		Assert.notNull(applyCaptchaCumulator, "applyCumulator is null, please check configure");
		Assert.notNull(sessionApplyCaptchaCumulator, "sessionApplyCumulator is null, please check configure");
	}

	/**
	 * Get enhanced cache.
	 * 
	 * @param suffix
	 * @return
	 */
	private EnhancedCache getCache(String suffix) {
		return cacheManager.getEnhancedCache(verifyType().name() + suffix);
	}

}