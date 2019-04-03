package com.wl4g.devops.iam.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.Assert;

import redis.clients.jedis.JedisCluster;

/**
 * RedisCache Manager implements let Shiro use Redis caching
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2017年4月13日
 * @since
 */
public class JedisCacheManager implements EnhancedCacheManager {

	final private Map<String, EnhancedCache> caching = new ConcurrentHashMap<>();

	private String prefix;
	private JedisCluster jedisCluster;

	public JedisCacheManager(String prefix, JedisCluster jedisCluster) {
		Assert.notNull(prefix, "'prefix' must not be null");
		Assert.notNull(jedisCluster, "'jedisCluster' must not be null");
		this.prefix = prefix;
		this.jedisCluster = jedisCluster;
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cache<EnhancedKey, Object> getCache(String name) throws CacheException {
		return this.getEnhancedCache(name);
	}

	/**
	 * Getting enhanced cache instance
	 * 
	 * @param name
	 * @return
	 * @throws CacheException
	 */
	@Override
	public EnhancedCache getEnhancedCache(String name) throws CacheException {
		String cacheName = this.getCacheName(name);
		EnhancedCache cache = this.caching.get(cacheName);
		if (cache == null) {
			this.caching.put(cacheName, (cache = new JedisEnhancedCache(cacheName, this.jedisCluster)));
		}
		return cache;
	}

	private String getCacheName(String name) {
		return this.prefix + name;
	}

}
