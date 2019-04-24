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
package com.wl4g.devops.iam.handler.verification;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.EnhancedKey;

/**
 * Accumulator tools
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public abstract class Cumulators {

	/**
	 * New create cumulator
	 * 
	 * @param cacheManager
	 * @param cacheName
	 * @return
	 */
	public static Cumulator newCumulator(EnhancedCacheManager cacheManager, String cacheName) {
		Assert.notNull(cacheManager, "cacheManager is null, please check configure");
		Assert.hasText(cacheName, "cacheName is empty, please check configure");
		return new DefaultCumulator(cacheManager.getEnhancedCache(cacheName));
	}

	/**
	 * Verification limiter accumulator
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年4月19日
	 * @since
	 */
	public static interface Cumulator {

		/**
		 * Number of cumulative processing (e.g. to limit the number of login
		 * failures or the number of short messages sent by same source IP
		 * requests)
		 * 
		 * @param factors
		 *            Safety limiting factor(e.g. Client remote IP and login
		 *            user-name)
		 * @param incrBy
		 *            Step increment value
		 * @param expireMs
		 *            Expired milliseconds
		 * @return returns the maximum number of times under all constraint
		 *         factors.(e.g: max attempts failed count)
		 */
		default Long accumulate(@NotNull List<String> factors, long incrBy, long expireMs) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Get the cumulative number of failures for the specified condition
		 * 
		 * @param factors
		 *            Safety limiting factor(e.g. Client remote IP and login
		 *            user-name)
		 * @return returns the maximum number of times under all constraint
		 *         factors.(e.g: max attempts failed count)
		 */
		default Long getCumulative(@NotBlank String factors) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Gets the cumulative number of failures for the specified condition
		 * 
		 * @param factors
		 *            Safety limiting factor(e.g. Client remote IP and login
		 *            user-name)
		 * @return returns the maximum number of times under all constraint
		 *         factors.(e.g: max attempts failed count)
		 */
		default Long getCumulatives(@NotNull List<String> factors) {
			throw new UnsupportedOperationException();
		}

		/**
		 * Cancel verification code
		 * 
		 * @param factors
		 *            Safety limiting factor(e.g. Client remote IP and login
		 *            user-name)
		 */
		default void destroy(@NotNull List<String> factors) {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Default verification limiter accumulator
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年4月19日
	 * @since
	 */
	public static class DefaultCumulator implements Cumulator {

		private Logger log = LoggerFactory.getLogger(getClass());

		/**
		 * Distributed enhancedCache
		 */
		private EnhancedCache cache;

		public DefaultCumulator(EnhancedCache cache) {
			Assert.notNull(cache, "EnhancedCache must not be null");
			this.cache = cache;
		}

		@Override
		public Long accumulate(@NotNull List<String> factors, long incrBy, long expireMs) {
			Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

			// Accumulated maximum number of failures
			long cumulatedMax = 0;
			for (String factor : factors) {
				EnhancedKey key = new EnhancedKey(factor, expireMs);
				// Reset Accumulated
				if (incrBy <= 0) {
					cumulatedMax = Math.max(cumulatedMax, cache.decrementGet(key, Math.abs(incrBy)));
				}
				// Positive increasing
				else {
					cumulatedMax = Math.max(cumulatedMax, cache.incrementGet(key, incrBy));
				}
			}

			return cumulatedMax;
		}

		@Override
		public Long getCumulative(@NotBlank String factor) {
			Long cumulated = (Long) cache.get(new EnhancedKey(factor, Long.class));
			return cumulated == null ? 0 : cumulated;
		}

		@Override
		public Long getCumulatives(@NotNull List<String> factors) {
			Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

			// Accumulated maximum number of failures
			long cumulatedMax = 0;
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
					cache.remove(new EnhancedKey(factor));
				} catch (Exception e) {
					log.error("", e);
				}
			});
		}

	}

}
