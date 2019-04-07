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
package com.wl4g.devops.iam.authc.credential;

import java.util.List;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.util.StringUtils;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_MATCHER_LOCKER;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.getFailConditions;
import com.wl4g.devops.iam.authc.CaptchaAuthenticationToken;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
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

	/**
	 * Enhanced cache interitable thread local.
	 */
	final private ThreadLocal<EnhancedCache> cacheLocal = new InheritableThreadLocal<>();

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		IamAuthenticationToken tk = (IamAuthenticationToken) token;
		// Get preparatory signIn principal
		String principal = (String) tk.getPrincipal();

		// Fail limiter condition keys
		List<String> conditions = getFailConditions(tk.getHost(), principal);
		Assert.notEmpty(conditions, "'conditions' must not be empty");

		Long cumulatedMaxFailCount = 0L;
		try {
			// Assert valid account has been locked
			cumulatedMaxFailCount = assertAccountLocked(principal, conditions);

			// Assert valid captcha
			assertRequestCaptcha(tk, principal, conditions);
		} catch (RuntimeException e) {
			cumulatedMaxFailCount = postMatchedFailureProcess(principal, conditions);
			throw e;
		}

		// Credentials verification
		final boolean matched = doCustomMatch(token, info);
		if (matched) { // Matched successful processing
			postMatchedSuccessProcess(principal, conditions);
		} else {
			cumulatedMaxFailCount = postMatchedFailureProcess(principal, conditions);
		}

		if (log.isInfoEnabled()) {
			log.info("Credentials[{}], matched[{}], principal[{}], anyFailCount[{}]", String.valueOf(token.getCredentials()),
					matched, principal, cumulatedMaxFailCount);
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
	 * @param conditions
	 * @return
	 */
	private Long postMatchedFailureProcess(String principal, List<String> conditions) {
		// Failure count accumulative increment by 1
		// Cumulative max fail count
		Long cumulatedMaxFailCount = captchaHandler.accumulative(conditions, 1);
		if (log.isInfoEnabled()) {
			log.info("Principal {} matched failure accumulative limiter condition {}, cumulatedMaxFailCount {}", principal,
					conditions, cumulatedMaxFailCount);
		}

		return cumulatedMaxFailCount;
	}

	/**
	 * After matched success processing
	 * 
	 * @param principal
	 * @param conditions
	 */
	private void postMatchedSuccessProcess(String principal, List<String> conditions) {
		// Reset captcha
		captchaHandler.accumulative(conditions, -1);
		if (log.isDebugEnabled()) {
			log.debug("Principal {} matched success, cleaning conditions:[{}]", principal, conditions);
		}

		// Clean all locker(if exists)
		EnhancedCache cache = getCache();
		conditions.forEach(condit -> {
			try {
				cache.remove(new EnhancedKey(condit));
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

	/**
	 * Assert check if the account has been locked
	 * 
	 * @param principal
	 * @param conditions
	 * @return
	 */
	private Long assertAccountLocked(String principal, List<String> conditions) {
		// Failure locked max attempts
		int lockedMaxAttempts = config.getMatcher().getFailureLockedMaxAttempts();
		// Failure delay time
		long lockedDelay = config.getMatcher().getFailureLockedDelay();
		// Cumulative max fail count
		long cumulatedMaxFailCount = 0;
		// Whether the tag is locked or not
		boolean locked = false;

		for (String condit : conditions) {
			// Present condition need locks
			boolean conditLocked = false;

			// Get count of failures by lockKey.
			Long cumulatived = captchaHandler.getCumulative(condit);

			// Stored max
			cumulatedMaxFailCount = Math.max(cumulatedMaxFailCount, cumulatived);

			// Check last locked remain time(if exist)
			String lockedPrincipal = (String) getCache().get(new EnhancedKey(condit, String.class));

			// Previous locks have not expired
			if (StringUtils.hasText(lockedPrincipal)) {
				conditLocked = true;
			}

			/*
			 * No previous locks, If the number of failures is exceeded, no
			 * login is allowed.
			 */
			if (cumulatived > lockedMaxAttempts) {
				conditLocked = true;
			}

			/*
			 * If lockout is required at present, Update decay counter time
			 */
			if (conditLocked) {
				Long remainTime = getCache().timeToLive(new EnhancedKey(condit, lockedDelay), principal);
				log.warn(String.format(
						"Login failed, limiter condition [%s] attempts have been made to exceed the maximum limit [%s], remain time [%s Sec] [%s]",
						condit, lockedMaxAttempts, remainTime, condit));
			}

			// The whole is marked as needing to be locked
			locked = conditLocked;
		}

		if (locked) { // Any condition matched
			throw new LockedAccountException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
		}

		return cumulatedMaxFailCount;
	}

	/**
	 * Assert check if the request verification code matches
	 * 
	 * @param token
	 * @param principal
	 * @param conditions
	 */
	private void assertRequestCaptcha(AuthenticationToken token, String principal, List<String> conditions) {
		if (token instanceof CaptchaAuthenticationToken) {
			captchaHandler.validate(conditions, ((CaptchaAuthenticationToken) token).getCaptcha());
		}
	}

	/**
	 * Get matcher locker cache.
	 * 
	 * @return
	 */
	private EnhancedCache getCache() {
		if (cacheLocal.get() == null) {
			cacheLocal.set(cacheManager.getEnhancedCache(CACHE_MATCHER_LOCKER));
		}
		return cacheLocal.get();
	}

}