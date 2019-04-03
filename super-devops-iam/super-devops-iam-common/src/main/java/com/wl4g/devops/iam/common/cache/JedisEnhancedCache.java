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
package com.wl4g.devops.iam.common.cache;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.utils.serialize.ProtostuffUtils;

import redis.clients.jedis.JedisCluster;

/**
 * REDIS enhanced implement cache
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
public class JedisEnhancedCache implements EnhancedCache {
	final private Logger log = LoggerFactory.getLogger(JedisEnhancedCache.class);
	private String name;
	private JedisCluster jedisCluster;

	public JedisEnhancedCache(String name, JedisCluster jedisCluster) {
		this.name = name;
		this.jedisCluster = jedisCluster;
		Assert.notNull(name, "'name' must not be null");
		Assert.notNull(jedisCluster, "'jedisCluster' must not be null");
	}

	@Override
	public Object get(final EnhancedKey key) throws CacheException {
		Assert.notNull(key, "'key' must not be null");
		Assert.notNull(key.getValueClass(), "'valueClass' must not be null");
		if (log.isDebugEnabled()) {
			log.debug("Get key={}", key);
		}
		byte[] data = this.jedisCluster.get(key.getKey(name));
		if (key.getDeserializer() != null) { // Using a custom deserializer
			return key.getDeserializer().deserialize(data, key.getValueClass());
		}
		return ProtostuffUtils.deserialize(data, key.getValueClass());
	}

	@Override
	public Object put(final EnhancedKey key, final Object value) throws CacheException {
		Assert.notNull(key, "'key' must not be null");
		Assert.notNull(value, "'value' must not be null");
		if (log.isDebugEnabled()) {
			log.debug("Put key={}, value={}", key, value);
		}

		byte[] data = null;
		if (key.getSerializer() != null) { // Using a custom serializer
			return key.getSerializer().serialize(value);
		} else {
			data = ProtostuffUtils.serialize(value);
		}

		String ret = null;
		if (key.isExpire()) {
			ret = this.jedisCluster.setex(key.getKey(name), key.getExpire(), data);
		} else {
			ret = this.jedisCluster.set(key.getKey(name), data);
		}
		return String.valueOf(ret).equalsIgnoreCase("nil") ? null : ret;
	}

	@Override
	public Object remove(final EnhancedKey key) throws CacheException {
		Assert.notNull(key, "'key' must not be null");
		if (log.isDebugEnabled()) {
			log.debug("Remove key={}", key);
		}
		return this.jedisCluster.del(key.getKey(name));
	}

	@Override
	public void clear() throws CacheException {
		if (log.isDebugEnabled()) {
			log.debug("Clear name={}", name);
		}
		this.jedisCluster.hdel(name);
	}

	@Override
	public int size() {
		if (log.isDebugEnabled()) {
			log.debug("Size name={}", name);
		}
		return this.jedisCluster.hlen(name).intValue();
	}

	@Deprecated
	@Override
	public Set<EnhancedKey> keys() {
		// if (log.isDebugEnabled()) {
		// log.debug("Keys name={}", name);
		// }
		// Set<byte[]> keys = this.jedisCluster.hkeys(name);
		// if (keys != null && !keys.isEmpty()) {
		// return keys.stream().map(key -> new
		// EnhancedKey(key)).collect(Collectors.toSet());
		// }
		// return Collections.emptySet();
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public Collection<Object> values() {
		// if (log.isDebugEnabled()) {
		// log.debug("Values name={}", name);
		// }
		// Collection<byte[]> vals = this.jedisCluster.hvals(name);
		// if (vals != null && !vals.isEmpty()) {
		// return vals.stream().collect(Collectors.toList());
		// }
		// return Collections.emptyList();
		throw new UnsupportedOperationException();
	}

	@Override
	public Long timeToLive(EnhancedKey key) throws CacheException {
		byte[] realKey = key.getKey(name);
		// New create.
		if (!this.jedisCluster.exists(realKey)) {
			// key -> createTime
			this.jedisCluster.set(realKey, String.valueOf(System.currentTimeMillis()).getBytes(Charsets.UTF_8));
		}
		// Get last TTL expire
		Long lastTTL = this.jedisCluster.ttl(realKey);
		// Less than or equal to 0 means immediate expiration
		if (key.isExpire()) {
			this.jedisCluster.expire(realKey, key.getExpire());
		}
		return lastTTL;
	}

	@Override
	public Long incrementGet(String key) throws CacheException {
		return this.jedisCluster.incr(EnhancedKey.toKeyBytes(name, key));
	}

	@Override
	public Long incrementGet(String key, long value) throws CacheException {
		return this.jedisCluster.incrBy(EnhancedKey.toKeyBytes(name, key), value);
	}

	@Override
	public Long decrementGet(String key) throws CacheException {
		return this.jedisCluster.decr(EnhancedKey.toKeyBytes(name, key));
	}

	@Override
	public Long decrementGet(String key, long value) throws CacheException {
		return this.jedisCluster.decrBy(EnhancedKey.toKeyBytes(name, key), value);
	}

}