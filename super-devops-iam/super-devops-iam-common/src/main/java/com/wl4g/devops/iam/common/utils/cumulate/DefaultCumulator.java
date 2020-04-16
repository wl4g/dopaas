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
package com.wl4g.devops.iam.common.utils.cumulate;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Default security limit accumulation counter based on distributed
 * {@link IamCache} implementation
 *
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public class DefaultCumulator implements Cumulator {
	final private SmartLogger log = getLogger(getClass());

	/**
	 * Distributed enhancedCache
	 */
	final private IamCache cache;

	/**
	 * Expired milliseconds
	 */
	final private long expireMs;

	public DefaultCumulator(IamCache cache, long expireMs) {
		Assert.notNull(cache, "EnhancedCache must not be null");
		Assert.isTrue(expireMs > 0, "expireMs must be greater than 0");
		this.cache = cache;
		this.expireMs = expireMs;
	}

	@Override
	public long accumulate(@NotNull List<String> factors, long incrBy) {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

		// Accumulated maximum number of failures
		long cumulatedMax = 0;
		for (String factor : factors) {
			CacheKey key = new CacheKey(factor, expireMs);
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
	public long getCumulative(@NotBlank String factor) {
		Long cumulated = (Long) cache.get(new CacheKey(factor, Long.class));
		return cumulated == null ? 0 : cumulated;
	}

	@Override
	public long getCumulatives(@NotNull List<String> factors) {
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
				cache.remove(new CacheKey(factor));
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

}