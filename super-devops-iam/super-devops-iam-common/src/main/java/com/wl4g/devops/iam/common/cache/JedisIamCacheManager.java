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
package com.wl4g.devops.iam.common.cache;

import static com.wl4g.devops.support.redis.EnhancedJedisCluster.RedisFormatUtils.keyFormat;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import redis.clients.jedis.JedisCluster;

/**
 * RedisCache Manager implements let Shiro use Redis caching
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2017年4月13日
 * @since
 */
public class JedisIamCacheManager implements IamCacheManager {

	final private Map<String, IamCache> caching = new ConcurrentHashMap<>();

	private String prefix;
	private JedisCluster jedisCluster;

	public JedisIamCacheManager(String prefix, JedisCluster jedisCluster) {
		notNullOf(prefix, "prefix");
		notNullOf(jedisCluster, "jedisCluster");
		// e.g: iam-server => iam_server
		this.prefix = keyFormat(prefix, '_');
		this.jedisCluster = jedisCluster;
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cache<CacheKey, Object> getCache(String name) throws CacheException {
		return getIamCache(name);
	}

	/**
	 * Getting enhanced cache instance
	 *
	 * @param name
	 * @return
	 * @throws CacheException
	 */
	@Override
	public IamCache getIamCache(String name) throws CacheException {
		String cacheName = getCacheName(name);
		IamCache cache = caching.get(cacheName);
		if (Objects.isNull(cache)) {
			caching.put(cacheName, (cache = new JedisIamCache(cacheName, jedisCluster)));
		}
		return cache;
	}

	private String getCacheName(String name) {
		return prefix + name;
	}

}