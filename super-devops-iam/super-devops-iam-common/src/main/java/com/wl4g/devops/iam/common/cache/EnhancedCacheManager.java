package com.wl4g.devops.iam.common.cache;

import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * Enhanced cache manager implements let shiro use redis caching
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2017年4月13日
 * @since
 */
public interface EnhancedCacheManager extends CacheManager {

	/**
	 * Getting enhanced cache instance
	 * 
	 * @param name
	 * @return
	 * @throws CacheException
	 */
	EnhancedCache getEnhancedCache(String name) throws CacheException;
}
