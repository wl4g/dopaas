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
package com.wl4g.devops.iam.handler.verification;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_FAILFAST_CAPTCHA_COUNTER;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_FAILFAST_MATCH_COUNTER;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import com.google.common.io.Resources;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.handler.verification.Cumulators.Cumulator;

/**
 * Abstract graphic verification code handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class GraphBasedVerification extends AbstractVerification implements InitializingBean {

	/**
	 * Broken CAPTCHA bytes array stream.
	 */
	final public static byte[] BROKEN_CAPTCHA;

	static {
		try {
			String classpath = GraphBasedVerification.class.getName();
			int endIndex = classpath.length() - GraphBasedVerification.class.getSimpleName().length();
			String brokenPath = classpath.substring(0, endIndex).replaceAll("\\.", "/") + "broken.jpg";
			BROKEN_CAPTCHA = Resources.toByteArray(Resources.getResource(brokenPath));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Matching attempts accumulator
	 */
	private Cumulator matchCumulator;

	/**
	 * Apply CAPTCHA attempts accumulator
	 */
	private Cumulator applyCumulator;

	/**
	 * Key name used to store authentication code to session
	 */
	final protected static String KEY_CAPTCHA_SESSION = GraphBasedVerification.class.getSimpleName() + ".VERIFYCODE";

	/**
	 * {@link com.google.code.kaptcha.servlet.KaptchaServlet#doGet(javax.servlet.http.HttpServletRequest, HttpServletResponse)}
	 */
	@Override
	public void apply(@NotNull List<String> factors, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response)
			throws IOException {

		// Check limit attempts
		checkApplyAttempts(request, response, factors);

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

		// Recreate a CAPTCHA
		reset(true);

		// Create the text for the image and output CAPTCHA image buffer.
		write(response, getVerifyCode(true).getText());
	}

	@Override
	public boolean isEnabled(@NotNull List<String> factors) {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

		// Enabled CAPTCHA max attempts
		int enabledCaptchaMaxAttempts = config.getMatcher().getEnabledCaptchaMaxAttempts();

		// If the number of failures exceeds the upper limit, verification is
		// enabled
		return matchCumulator.getCumulatives(factors) >= enabledCaptchaMaxAttempts;
	}

	@Override
	protected String storageSessionKey() {
		return KEY_CAPTCHA_SESSION;
	}

	@Override
	protected long getExpireMs() {
		return config.getMatcher().getCaptchaExpireMs();
	}

	@Override
	protected void checkApplyAttempts(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull List<String> factors) {
		long failFastCaptchaMaxAttempts = config.getMatcher().getFailFastCaptchaMaxAttempts();

		// Accumulated number of apply
		Long applyCumulatedCount = applyCumulator.accumulate(factors, 1, config.getMatcher().getFailFastCaptchaDelay());
		if (applyCumulatedCount >= failFastCaptchaMaxAttempts) {
			log.warn("Apply for graph verification code too often, actual: {}, maximum: {}, factors: {}", applyCumulatedCount,
					failFastCaptchaMaxAttempts, factors);

			// Write default broken CAPTCHA
			try {
				WebUtils2.write(response, HttpStatus.OK.value(), MediaType.IMAGE_JPEG_VALUE, BROKEN_CAPTCHA);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Write output verify-code buffer image
	 * 
	 * @param response
	 * @param verifyCode
	 * @return
	 */
	protected abstract void write(HttpServletResponse response, String verifyCode) throws IOException;

	/**
	 * Initializing
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.matchCumulator = Cumulators.newCumulator(cacheManager, CACHE_FAILFAST_MATCH_COUNTER);
		this.applyCumulator = Cumulators.newCumulator(cacheManager, CACHE_FAILFAST_CAPTCHA_COUNTER);
		Assert.notNull(matchCumulator, "matchCumulator is null, please check configure");
		Assert.notNull(applyCumulator, "applyCumulator is null, please check configure");
	}

}