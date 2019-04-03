package com.wl4g.devops.iam.common.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

/**
 * Enhanced implementation of Shiro cache support for automatic expiratio
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public interface EnhancedCache extends Cache<EnhancedKey, Object> {

	/**
	 * Decay counter
	 * 
	 * @param key
	 *            Key that contains the actual EnhancedKey and expiration date
	 * @return Returns the remaining valid time of this EnhancedKey before this
	 *         setup
	 * @throws CacheException
	 */
	Long timeToLive(final EnhancedKey key) throws CacheException;

	/**
	 * Get and add an atomic counter at the same time
	 * 
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	Long incrementGet(final String key) throws CacheException;

	/**
	 * Get and add an atomic counter at the same time
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws CacheException
	 */
	Long incrementGet(final String key, long value) throws CacheException;

	/**
	 * Acquisition and reduction of atomic counter once at a time
	 * 
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	Long decrementGet(final String key) throws CacheException;

	/**
	 * Acquisition and reduction of atomic counter once at a time
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws CacheException
	 */
	Long decrementGet(final String key, long value) throws CacheException;

}
