package com.wl4g.devops.iam.authc.credential;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_MATCHER_LOCKER;

import com.wl4g.devops.iam.authc.CaptchaAuthenticationToken;
import com.wl4g.devops.iam.common.cache.EnhancedKey;

/**
 * Abstract custom attempts credential matcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract class AbstractAttemptsMatcher extends IamBasedMatcher {

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		// Get preparatory signIn principal
		String principal = String.valueOf(token.getPrincipal());

		// Check if the account has been locked
		Long failedCount = this.checkAccountLocked(principal);

		// Check if the request captcha matches
		this.checkRequestCaptcha(principal, token);

		// Credentials verification
		final boolean matched = this.doCustomMatch(token, info);
		if (matched) {
			// Matched successful processing
			this.postMatchedSuccessProcess(principal);
		} else {
			// Matched failure processing
			failedCount = this.postMatchedFailureProcess(principal);
		}
		if (log.isInfoEnabled()) {
			log.info("Credentials[], matched[{}], principal[{}], failedCount[{}]", String.valueOf(token.getCredentials()),
					matched, principal, failedCount);
		}

		// This is an accident.
		return matched;
	}

	/**
	 * Execution custom match
	 * 
	 * @param token
	 * @param info
	 * @return
	 */
	protected abstract boolean doCustomMatch(AuthenticationToken token, AuthenticationInfo info);

	/**
	 * After matched failure processing
	 * 
	 * @param principal
	 * @param failedCount
	 * @return
	 */
	private Long postMatchedFailureProcess(String principal) {
		// Failure count accumulative increment by 1
		Long failedCount = this.captchaHandler.accumulative(principal, 1);
		if (log.isInfoEnabled()) {
			log.info("'{}' matched failure accumulative failedCount=[{}]", principal, failedCount);
		}
		return failedCount;
	}

	/**
	 * After matched success processing
	 * 
	 * @param principal
	 * @param failedCount
	 */
	private void postMatchedSuccessProcess(String principal) {
		// Reset captcha
		this.captchaHandler.accumulative(principal, 0);

		String lockerKey = this.getFailLockerKey(principal);
		if (log.isDebugEnabled()) {
			log.debug("'{}' matched success, cleaning lockerKey:[{}]", principal, lockerKey);
		}

		// Clear locker(if exists)
		this.cacheManager.getEnhancedCache(CACHE_MATCHER_LOCKER).remove(new EnhancedKey(lockerKey));
	}

	/**
	 * Check if the account has been locked
	 * 
	 * @param principal
	 * @return
	 */
	private Long checkAccountLocked(String principal) {
		// Failure locked max attempts
		int lockedMaxAttempts = this.config.getMatcher().getFailureLockedMaxAttempts();
		// Failure delay time
		long lockedDelay = this.config.getMatcher().getFailureLockedDelay();
		// Count of failures
		Long failedCount = this.captchaHandler.getCumulative(principal);

		// If the number of failures is exceeded, no login is allowed.
		if (failedCount > lockedMaxAttempts) {
			String lockerKey = this.getFailLockerKey(principal);
			// Update decay counter time
			Long remainLifecycleSec = this.cacheManager.getEnhancedCache(CACHE_MATCHER_LOCKER)
					.timeToLive(new EnhancedKey(lockerKey, lockedDelay));

			if (log.isWarnEnabled()) {
				log.warn(String.format(
						"Login failed, principal: [%s] attempts have been made to exceed the maximum limit [%s], remaining lifecycle [%s Sec] [%s]",
						principal, lockedMaxAttempts, remainLifecycleSec, lockerKey));
			}
			throw new LockedAccountException(delegate.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
		}
		return failedCount;
	}

	/**
	 * Check if the request verification code matches
	 * 
	 * @param principal
	 * @param token
	 * @return
	 */
	private void checkRequestCaptcha(String principal, AuthenticationToken token) {
		if (token instanceof CaptchaAuthenticationToken) {
			this.captchaHandler.validate(principal, ((CaptchaAuthenticationToken) token).getCaptcha());
		}
	}

	/**
	 * Get failed locker key
	 * 
	 * @param principal
	 * @return
	 */
	private String getFailLockerKey(String principal) {
		return principal;
	}

}
