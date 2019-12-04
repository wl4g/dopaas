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
package com.wl4g.devops.iam.verification;

import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.config.properties.MatcherProperties;
import com.wl4g.devops.iam.crypto.keypair.RSACryptographicService;
import com.wl4g.devops.iam.crypto.keypair.RSAKeySpecWrapper;
import com.wl4g.devops.iam.verification.cumulation.Cumulator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_FAILFAST_CAPTCHA_COUNTER;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_FAILFAST_MATCH_COUNTER;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bind;
import static com.wl4g.devops.iam.verification.cumulation.CumulateHolder.newCumulator;
import static com.wl4g.devops.iam.verification.cumulation.CumulateHolder.newSessionCumulator;
import static com.wl4g.devops.tool.common.codec.Encodes.encodeBase64;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

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
	 * Apply token parameter name.
	 */
	final public static String DEFAULT_APPLY_TOKEN = "applyToken";

	/**
	 * Apply token expireMs.
	 */
	final public static long DEFAULT_APPLY_TOKEN_EXPIREMS = 60_000;

	/**
	 * Apply UUID bit.
	 */
	final public static int DEFAULT_APPLY_TOKEN_BIT = 48;

	/**
	 * RSA cryptoGrapic service.
	 */
	@Autowired
	protected RSACryptographicService rsaCryptoService;

	/**
	 * Matching attempts accumulator
	 */
	protected Cumulator matchCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator.(Session-based)
	 */
	protected Cumulator sessionMatchCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator
	 */
	protected Cumulator applyCaptchaCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator.(Session-based)
	 */
	protected Cumulator sessionApplyCaptchaCumulator;

	/**
	 * {@link com.google.code.kaptcha.servlet.KaptchaServlet#doGet(HttpServletRequest, HttpServletResponse)}
	 */
	@Override
	public Object doApply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request) throws IOException {
		// Check and generate apply UUID.
		VerifyCodeWrapper wrap = getVerifyCode(true);
		Assert.state(Objects.nonNull(wrap), "Failed to apply captcha.");

		// Get RSA key.(Used to encrypt sliding X position)
		RSAKeySpecWrapper keySpec = rsaCryptoService.borrow();
		String applyToken = "capt" + randomAlphabetic(DEFAULT_APPLY_TOKEN_BIT);
		bind(applyToken, keySpec, DEFAULT_APPLY_TOKEN_EXPIREMS);
		if (log.isDebugEnabled()) {
			log.debug("Apply captcha for applyToken: {}, secretKey: {}", applyToken, keySpec);
		}

		// Custom processing.
		return postApplyGraphProperties(applyToken, wrap, keySpec);
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

	/**
	 * After apply graph properties processing.
	 *
	 * @param applyToken
	 * @param codeWrap
	 * @param keySpec
	 * @return
	 * @throws IOException
	 */
	protected abstract Object postApplyGraphProperties(String applyToken, VerifyCodeWrapper codeWrap, RSAKeySpecWrapper keySpec)
			throws IOException;

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
	 * Building image to base64.
	 *
	 * @param data
	 * @return
	 */
	protected String convertToBase64(byte[] data) {
		return "data:image/jpeg;base64," + encodeBase64(data);
	}

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
		return cacheManager.getEnhancedCache(suffix);
	}

}