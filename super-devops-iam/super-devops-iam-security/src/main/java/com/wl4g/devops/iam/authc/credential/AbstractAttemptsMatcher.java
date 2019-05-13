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
package com.wl4g.devops.iam.authc.credential;

import com.wl4g.devops.common.constants.IAMDevOpsConstants;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.handler.verification.Cumulators;
import com.wl4g.devops.iam.handler.verification.Cumulators.Cumulator;
import com.wl4g.devops.iam.handler.verification.Verification;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.handler.verification.GraphBasedVerification.FailCountWrapper;

/**
 * Abstract custom attempts credential matcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
abstract class AbstractAttemptsMatcher extends IamBasedMatcher implements InitializingBean {

	/**
	 * EnhancedCache
	 */
	private EnhancedCache lockCache;

	/**
	 * Attempts accumulator
	 */
	private Cumulator matchCumulator;

	/**
	 * Attempts CAPTCHA accumulator
	 */
	private Cumulator applyCaptchaCumulator;

	/**
	 * Attempts SMS accumulator
	 */
	private Cumulator applySmsCumulator;

	public AbstractAttemptsMatcher(Verification verification) {
		super(verification);
	}

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		IamAuthenticationToken tk = (IamAuthenticationToken) token;
		// Get preparatory signIn principal
		String principal = (String) tk.getPrincipal();

		// Fail limiter factor keys
		List<String> factors = lockFactors(tk.getHost(), principal);
		Assert.notEmpty(factors, "'factors' must not be empty");

		Long cumulatedMaxFailCount = 0L;
		try {
			// Assertion needs to be locked
			cumulatedMaxFailCount = assertAccountLocked(principal, factors);

			// Assertion verification
			assertRequestVerify(tk, principal, factors);

		} catch (RuntimeException e) {
			cumulatedMaxFailCount = postFailureProcess(principal, factors);
			throw e;
		}

		// Credentials verification
		final boolean matched = doMatching(token, info, factors);
		if (matched) { // Matched successful processing
			postSuccessProcess(principal, factors);
		} else {
			cumulatedMaxFailCount = postFailureProcess(principal, factors);
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
	 * @param factors
	 * @return
	 */
	protected abstract boolean doMatching(AuthenticationToken token, AuthenticationInfo info, List<String> factors);

	/**
	 * After matched failure processing
	 * 
	 * @param principal
	 * @param factors
	 * @return
	 */
	protected Long postFailureProcess(String principal, List<String> factors) {
		// Mathing failure count accumulative increment by 1
		Long cumulatedMaxFailCount = matchCumulator.accumulate(factors, 1, config.getMatcher().getFailFastMatchDelay());
		// add fail count into session
		postFailCountAdd();
		if (log.isInfoEnabled()) {
			log.info("Principal {} matched failure accumulative limiter factor {}, cumulatedMaxFailCount {}", principal, factors,
					cumulatedMaxFailCount);
		}

		return cumulatedMaxFailCount;
	}

	/**
	 * After matched success processing
	 * 
	 * @param principal
	 * @param factors
	 */
	protected void postSuccessProcess(String principal, List<String> factors) {
		// Destroy all cumulators
		destroyAllCumulators(factors);

		if (log.isDebugEnabled()) {
			log.debug("Principal {} matched success, cleaning factors: {}", principal, factors);
		}

		// Clean all locker(if exists)
		factors.forEach(factor -> {
			try {
				lockCache.remove(new EnhancedKey(factor));
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

	/**
	 * Assertion check if the account has been locked
	 * 
	 * @param principal
	 * @param factors
	 * @return
	 */
	protected Long assertAccountLocked(String principal, List<String> factors) {
		// Match failure lock max attempts
		int matchLockMaxAttempts = config.getMatcher().getFailFastMatchMaxAttempts();
		// Match failure delay time
		long matchLockDelay = config.getMatcher().getFailFastMatchDelay();
		// Cumulative max fail count
		long cumulatedMax = 0;
		// Whether the tag is locked or not
		boolean lock = false;

		for (String factor : factors) {
			// Present factor need locks
			boolean factorLock = false;

			// Got count of failures by factor.
			Long cumulated = matchCumulator.getCumulative(factor);

			// Stored accumulated max
			cumulatedMax = Math.max(cumulatedMax, cumulated);

			// Check last locked remain time(if exist)
			String lockedPrincipal = (String) lockCache.get(new EnhancedKey(factor, String.class));

			// Previous locks have not expired
			if (StringUtils.hasText(lockedPrincipal)) {
				factorLock = true;
			}

			/*
			 * No previous locks, If the number of failures is exceeded, no
			 * login is allowed.
			 */
			if (cumulated > matchLockMaxAttempts) {
				factorLock = true;
			}

			/*
			 * If lockout is required at present, Update decay counter time
			 */
			if (factorLock) {
				Long remainTime = lockCache.timeToLive(new EnhancedKey(factor, matchLockDelay), principal);
				log.warn(String.format(
						"Matching failed, limiter factor [%s] attempts have been made to exceed the maximum limit [%s], remain time [%s Sec] [%s]",
						factor, matchLockMaxAttempts, remainTime, factor));
			}

			// The whole is marked as needing to be locked
			lock = factorLock;
		}

		if (lock) { // Any factor matched
			log.warn("Client that has been locked. factors: {}", factors);
			throw new LockedAccountException(bundle.getMessage("AbstractAttemptsMatcher.ipAccessReject"));
		}

		return cumulatedMax;
	}

	/**
	 * Assertion some verifications before requesting authentication (e.g, graph
	 * verification code when password is logged in)
	 * 
	 * @param token
	 * @param principal
	 * @param factors
	 */
	protected abstract void assertRequestVerify(AuthenticationToken token, String principal, List<String> factors);

	/**
	 * Initializing
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.lockCache = cacheManager.getEnhancedCache(CACHE_MATCH_LOCK);
		this.matchCumulator = Cumulators.newCumulator(cacheManager, CACHE_FAILFAST_MATCH_COUNTER);
		this.applyCaptchaCumulator = Cumulators.newCumulator(cacheManager, CACHE_FAILFAST_CAPTCHA_COUNTER);
		this.applySmsCumulator = Cumulators.newCumulator(cacheManager, CACHE_FAILFAST_SMS_COUNTER);

		Assert.notNull(lockCache, "lockCache is null, please check configure");
		Assert.notNull(matchCumulator, "matchCumulator is null, please check configure");
		Assert.notNull(applyCaptchaCumulator, "applyCaptchaCumulator is null, please check configure");
		Assert.notNull(applySmsCumulator, "applySmsCumulator is null, please check configure");
	}

	/**
	 * Destroy verification cumulator all
	 * 
	 * @param factors
	 */
	private void destroyAllCumulators(@NotNull List<String> factors) {
		matchCumulator.destroy(factors);
		applyCaptchaCumulator.destroy(factors);
		applySmsCumulator.destroy(factors);
	}

	private void postFailCountAdd() {
		Session session = SecurityUtils.getSubject().getSession();
		FailCountWrapper failCountWrapper = null != session.getAttribute(IAMDevOpsConstants.GRAPH_VERIFY_FAIL_TIME) ?
				(FailCountWrapper) session.getAttribute(IAMDevOpsConstants.GRAPH_VERIFY_FAIL_TIME) :
				new FailCountWrapper(0);
		failCountWrapper.setCount(failCountWrapper.getCount() + 1);
		failCountWrapper.setCreateTime(System.currentTimeMillis());
		log.info("session:loginFailTimes=" + failCountWrapper.getCount());
		session.setAttribute(IAMDevOpsConstants.GRAPH_VERIFY_FAIL_TIME, failCountWrapper);
	}

}