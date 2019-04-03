package com.wl4g.devops.iam.handler;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MESSAGE_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_CAPTCHA_FAILER;

import com.wl4g.devops.common.exception.iam.CaptchaException;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.i18n.DelegateBoundleMessageSource;
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
	protected DelegateBoundleMessageSource delegate;

	public AbstractCaptchaHandler(IamProperties config, JedisCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");
		this.config = config;
		this.cacheManager = cacheManager;
	}

	@Override
	public void validate(String principal, String captchaRequest) throws CaptchaException {
		try {
			if (!this.isEnabled(principal)) {
				return; // not enabled
			}
			// Get store the text of session
			Object capText = getSession().getAttribute(KEY_CAPTCHA_SESSION);
			if (capText == null) {
				throw new CaptchaException(
						delegate.getMessage("AbstractCaptchaHandler.captcha.expired", new Object[] { captchaRequest }));
			}
			if (!String.valueOf(capText).equalsIgnoreCase(captchaRequest)) {
				if (log.isErrorEnabled()) {
					log.error("Captcha mismatch. {} => {}", captchaRequest, capText);
				}
				throw new CaptchaException(
						delegate.getMessage("AbstractCaptchaHandler.captcha.mismatch", new Object[] { captchaRequest }));
			}
		} finally {
			this.reset(false); // Reset-clean
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
		this.reset(true);

		// Create the text for the image
		String capText = this.getCaptcha();

		// Output captcha image buffer.
		this.out(response, capText);
	}

	@Override
	public String reset(boolean create) {
		String capText = (String) getSession().removeAttribute(KEY_CAPTCHA_SESSION);
		if (create) {
			// Create the text for the image
			Assert.state(!StringUtils.isEmpty(capText = createText()), "'capText' must not be null");
			// Store the text in the cache
			this.getSession().setAttribute(KEY_CAPTCHA_SESSION, capText);
		}
		return capText;
	}

	@Override
	public Long accumulative(String principal, long value) {
		// Cumulative with principal key
		String cumulativeKey = this.getCumulativeKey(principal);
		// Captcha failer cache
		EnhancedCache cache = this.cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER);
		if (value < 0) { // Reset clearance
			return (Long) cache.remove(new EnhancedKey(cumulativeKey));
		}
		// Positive increasing
		return cache.incrementGet(cumulativeKey, value);
	}

	@Override
	public Long getCumulative(String principal) {
		Long cumulative = (Long) this.cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER)
				.get(new EnhancedKey(getCumulativeKey(principal), Long.class));
		return (cumulative == null) ? 0 : cumulative;
	}

	@Override
	public void cancel(String principal) {
		this.cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER).remove(new EnhancedKey(getCumulativeKey(principal)));
	}

	@Override
	public boolean isEnabled(String principal) {
		// Captcha required attempts
		int captchaMaxAttempts = this.config.getMatcher().getFailureCaptchaMaxAttempts();

		Integer failedCount = (Integer) this.cacheManager.getEnhancedCache(CACHE_CAPTCHA_FAILER)
				.get(new EnhancedKey(getCumulativeKey(principal), Integer.class));
		failedCount = failedCount == null ? 0 : failedCount;

		// If the number of failures exceeds the upper limit, captcha is
		// enabled
		return failedCount >= captchaMaxAttempts;
	}

	/**
	 * Get captcha text value
	 * 
	 * @return
	 */
	private String getCaptcha() {
		// Get already created text
		String capText = (String) getSession().getAttribute(KEY_CAPTCHA_SESSION);
		Assert.state(!StringUtils.isEmpty(capText), "'capText' must not be null");
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
	 * Get cumulative counter key
	 * 
	 * @param principal
	 * @return
	 */
	protected String getCumulativeKey(String principal) {
		Assert.notNull(principal, "'principal' must not be null");
		return principal;
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
