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
package com.wl4g.devops.iam.verification.cumulation;

import static com.wl4g.devops.iam.common.utils.SessionBindings.bind;
import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;
import static com.wl4g.devops.iam.common.utils.SessionBindings.unbind;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Session verification limiter accumulator
 *
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public class SessionCumulator implements Cumulator {

	final private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Cache to session key.
	 */
	final private String sessionKey;

	/**
	 * Expired milliseconds
	 */
	final private long expireMs;

	public SessionCumulator(@NotBlank String sessionKey, long expireMs) {
		Assert.hasText(sessionKey, "SessionKey must not be empty");
		Assert.isTrue(expireMs > 0, "expireMs must be greater than 0");
		this.sessionKey = sessionKey;
		this.expireMs = expireMs;
	}

	@Override
	public long accumulate(@NotNull List<String> factors, long incrBy) {
		// Accumulated maximum number of failures
		long cumulatedMax = 0;
		for (String factor : factors) {
			SessionLimitCounter counter = getBindValue(getActualSessionKey(factor));
			// Non-exist or expired?
			if (counter == null || isExpired(counter.getCreate())) {
				bind(getActualSessionKey(factor), (counter = new SessionLimitCounter(0L))); // Initialize
			}
			// Positive or negative growth
			cumulatedMax = Math.max(cumulatedMax, counter.getCumulator().addAndGet(incrBy));
		}
		return cumulatedMax;
	}

	@Override
	public long getCumulative(@NotBlank String factor) {
		SessionLimitCounter counter = getBindValue(getActualSessionKey(factor));
		if (counter != null) {
			if (!isExpired(counter.getCreate())) { // Expired?
				return counter.getCumulator().get();
			}
		}

		return 0L;
	}

	@Override
	public long getCumulatives(@NotNull List<String> factors) {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

		// Accumulated maximum number of failures
		long cumulatedMax = 0L;
		for (String factor : factors) {
			// Get count of failures by factor and take max
			cumulatedMax = Math.max(cumulatedMax, getCumulative(factor));
		}
		return cumulatedMax;
	}

	@Override
	public void destroy(@NotNull List<String> factors) {
		Assert.notEmpty(factors, "factors must not be empty");

		factors.forEach(factor -> {
			try {
				unbind(getActualSessionKey(factor));
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

	/**
	 * Actual session key.
	 *
	 * @param factor
	 * @return
	 */
	private String getActualSessionKey(@NotBlank String factor) {
		return sessionKey + "_" + factor;
	}

	/**
	 * Current accumulated count of session is expired.
	 *
	 * @param createTime
	 * @return
	 */
	private boolean isExpired(long createTime) {
		return (System.currentTimeMillis() - createTime) >= expireMs;
	}

	/**
	 * The session-based limit accumulator is used to solve the problem that the
	 * session cannot be tracked without an account name when applying for an
	 * image authentication code. Reference: the accumulator corresponding to
	 * {@link CACHE_FAILFAST_CAPTCHA_COUNTER}.
	 *
	 * @author wangl.sir
	 * @version v1.0 2019年5月16日
	 * @since
	 */
	public static class SessionLimitCounter implements Serializable {
		private static final long serialVersionUID = -7643664591972701966L;

		/**
		 * Accumulated number of failed counter
		 */
		final private AtomicLong cumulated;

		/**
		 * Failure counter creation timestamp
		 */
		final private Long create;

		public SessionLimitCounter(Long count) {
			this(count, System.currentTimeMillis());
		}

		public SessionLimitCounter(Long count, Long create) {
			Assert.notNull(count, "fail count is null, please check configure");
			Assert.notNull(create, "Create time is null, please check configure");
			this.cumulated = new AtomicLong(count);
			this.create = create;
		}

		public AtomicLong getCumulator() {
			return cumulated;
		}

		public Long getCreate() {
			return create;
		}

		@Override
		public String toString() {
			return "SessionFailCounter [count=" + cumulated.get() + ", create=" + create + "]";
		}

	}

}