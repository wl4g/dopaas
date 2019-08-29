package com.wl4g.devops.iam.verification.cumulation;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedKey;

/**
 * Default verification limiter accumulator
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public class DefaultCumulator implements Cumulator {

	final private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Distributed enhancedCache
	 */
	final private EnhancedCache cache;

	/**
	 * Expired milliseconds
	 */
	final private long expireMs;

	public DefaultCumulator(EnhancedCache cache, long expireMs) {
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
	public long getCumulative(@NotBlank String factor) {
		Long cumulated = (Long) cache.get(new EnhancedKey(factor, Long.class));
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
				cache.remove(new EnhancedKey(factor));
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

}
