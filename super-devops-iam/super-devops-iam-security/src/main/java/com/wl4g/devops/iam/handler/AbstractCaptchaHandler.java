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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.handler;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.Assert;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MESSAGE_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_CAPTCHA_FAILER;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.exception.iam.CaptchaException;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.i18n.DelegateBundleMessageSource;
import com.wl4g.devops.iam.config.IamProperties;

/**
 * Abstract IAM CAPTCHA handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class AbstractCaptchaHandler implements CaptchaHandler {

	/**
	 * Key name used to store authentication code to session
	 */
	final public static String KEY_CAPTCHA_SESSION = AbstractCaptchaHandler.class.getSimpleName() + ".CAPTCHA";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Server configuration properties
	 */
	final protected IamProperties config;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under Multi-Node
	 */
	final protected JedisCacheManager cacheManager;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MESSAGE_SOURCE)
	protected DelegateBundleMessageSource bundle;

	public AbstractCaptchaHandler(IamProperties config, JedisCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");
		this.config = config;
		this.cacheManager = cacheManager;
	}

	@Override
	public void validate(@NotNull List<String> conditions, String captchaReq) throws CaptchaException {
		Assert.isTrue(!CollectionUtils.isEmpty(conditions), "Conditions must not be empty");

		try {
			if (!isEnabled(conditions)) {
				return; // not enabled
			}

			// Get store the text
			Object capText = getCaptcha(false);
			if (capText == null) {
				throw new CaptchaException(bundle.getMessage("AbstractCaptchaHandler.captcha.expired", captchaReq));
			}

			if (!isEqualCaptcha(capText, captchaReq)) {
				if (log.isErrorEnabled()) {
					log.error("Captcha mismatch. {} => {}", captchaReq, capText);
				}
				throw new CaptchaException(bundle.getMessage("AbstractCaptchaHandler.captcha.mismatch", captchaReq));
			}

		} finally {
			reset(false); // Reset-clean
		}
	}

	/**
	 * {@link com.google.code.kaptcha.servlet.KaptchaServlet#doGet(javax.servlet.http.HttpServletRequest, HttpServletResponse)}
	 */
	@Override
	public void apply(HttpServletResponse response) throws IOException {
		// Set to expire far in the past.
		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// return a jpeg
		response.setContentType("image/jpeg");

		// Reset to create a new captcha
		reset(true);

		// Create the text for the image and output captcha image buffer.
		out(response, getCaptcha(true));
	}

	@Override
	public String reset(boolean create) {
		String capText = (String) getSession().removeAttribute(KEY_CAPTCHA_SESSION);
		if (create) {
			// Create the text for the image
			Assert.state(!StringUtils.isEmpty(capText = createText()), "'capText' must not be null");
			// Store the text in the cache
			getSession().setAttribute(KEY_CAPTCHA_SESSION, capText);
		}

		return capText;
	}

	@Override
	public Long accumulative(@NotNull List<String> conditions, long value) {
		Assert.isTrue(!CollectionUtils.isEmpty(conditions), "Conditions must not be empty");

		// Cumulated maximum number of failures
		long cumulatedMaxFailCount = 0;
		for (String condit : conditions) {
			// Captcha failer cache
			EnhancedCache cache = cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER);
			// Reset cumulated
			if (value <= 0) {
				cumulatedMaxFailCount = Math.max(cumulatedMaxFailCount, (Long) cache.remove(new EnhancedKey(condit)));
			}
			// Positive increasing
			else {
				cumulatedMaxFailCount = Math.max(cumulatedMaxFailCount, cache.incrementGet(condit, value));
			}
		}

		return cumulatedMaxFailCount;
	}

	@Override
	public Long getCumulative(@NotBlank String condition) {
		Long failCount = (Long) cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER).get(new EnhancedKey(condition, Long.class));
		return failCount == null ? 0 : failCount;
	}

	@Override
	public Long getCumulatives(@NotNull List<String> conditions) {
		Assert.isTrue(!CollectionUtils.isEmpty(conditions), "Conditions must not be empty");

		// Accumulated maximum number of failures
		long cumulatedMaxFailCount = 0;
		for (String condit : conditions) {
			// Get count of failures by condition and take max
			cumulatedMaxFailCount = Math.max(cumulatedMaxFailCount, getCumulative(condit));
		}

		return cumulatedMaxFailCount;
	}

	@Override
	public void cancel(@NotNull List<String> conditions) {
		Assert.isTrue(!CollectionUtils.isEmpty(conditions), "Conditions must not be empty");

		EnhancedCache cache = cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER);
		conditions.forEach(condit -> {
			try {
				cache.remove(new EnhancedKey(condit));
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

	@Override
	public boolean isEnabled(@NotNull List<String> conditions) {
		Assert.isTrue(!CollectionUtils.isEmpty(conditions), "Conditions must not be empty");

		// Captcha required attempts
		int captchaMaxAttempts = config.getMatcher().getFailureCaptchaMaxAttempts();

		// If the number of failures exceeds the upper limit, captcha is
		// enabled
		return getCumulatives(conditions) >= captchaMaxAttempts;
	}

	/**
	 * Get stored captcha text value of session
	 * 
	 * @param assertion
	 * @return
	 */
	private String getCaptcha(boolean assertion) {
		// Get already created text
		String capText = (String) getSession().getAttribute(KEY_CAPTCHA_SESSION);

		if (assertion) {
			Assert.state(!StringUtils.isEmpty(capText), "'capText' must not be null");
		}

		return capText;
	}

	/**
	 * Get SHIRO session
	 * 
	 * @return
	 */
	private Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	/**
	 * Comparing whether the verification codes are equal
	 * 
	 * @param capText
	 * @param capReq
	 * @return
	 */
	protected boolean isEqualCaptcha(Object capText, Object capReq) {
		return MessageDigest.isEqual(String.valueOf(capText).toLowerCase(Locale.ENGLISH).getBytes(Charsets.UTF_8),
				String.valueOf(capReq).toLowerCase(Locale.ENGLISH).getBytes(Charsets.UTF_8));
	}

	/**
	 * Create captcha text
	 * 
	 * @return
	 */
	protected abstract String createText();

	/**
	 * Output captcha buffer image
	 * 
	 * @param capText
	 * @return
	 */
	protected abstract void out(HttpServletResponse response, String capText) throws IOException;

}