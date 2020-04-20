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

import static com.wl4g.devops.tool.common.collection.Collections2.safeMap;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.iam.common.cache.CacheKey.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.cache.CacheException;

import com.google.common.base.Charsets;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.utils.serialize.ProtostuffUtils.*;
import com.wl4g.devops.tool.common.log.SmartLogger;

import redis.clients.jedis.JedisCluster;

/**
 * REDIS enhanced implement cache
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
public class JedisIamCache implements IamCache {
	final protected SmartLogger log = getLogger(getClass());

	private String name;
	private JedisCluster jedisCluster;

	public JedisIamCache(String name, JedisCluster jedisCluster) {
		notNull(name, "'name' must not be null");
		notNull(jedisCluster, "'jedisCluster' must not be null");
		this.name = name;
		this.jedisCluster = jedisCluster;
	}

	@Override
	public Object get(final CacheKey key) throws CacheException {
		notNullOf(key, "key");
		notNullOf(key.getValueClass(), "valueClass");
		log.debug("Get key={}", key);

		byte[] data = jedisCluster.get(key.getKey(name));
		if (key.getDeserializer() != null) { // Using a custom deserializer
			return key.getDeserializer().deserialize(data, key.getValueClass());
		}

		return deserialize(data, key.getValueClass());
	}

	@Override
	public Object put(final CacheKey key, final Object value) throws CacheException {
		notNullOf(key, "key");
		notNullOf(value, "value");
		log.debug("Put key={}, value={}", key, value);

		byte[] data = null;
		if (key.getSerializer() != null) { // Using a custom serializer
			data = key.getSerializer().serialize(value);
		} else {
			data = serialize(value);
		}

		String ret = null;
		if (key.hasExpire()) {
			ret = jedisCluster.setex(key.getKey(name), key.getExpire(), data);
		} else {
			ret = jedisCluster.set(key.getKey(name), data);
		}
		return String.valueOf(ret).equalsIgnoreCase("nil") ? null : ret;
	}

	@Override
	public Object remove(final CacheKey key) throws CacheException {
		notNull(key, "'key' must not be null");
		log.debug("Remove key={}", key);
		return jedisCluster.del(key.getKey(name));
	}

	@Override
	public void clear() throws CacheException {
		if (log.isDebugEnabled()) {
			log.debug("Clear name={}", name);
		}
		jedisCluster.hdel(name);
	}

	@Override
	public int size() {
		if (log.isDebugEnabled()) {
			log.debug("Size name={}", name);
		}
		return jedisCluster.hlen(name).intValue();
	}

	@Deprecated
	@Override
	public Set<CacheKey> keys() {
		// if (log.isDebugEnabled()) {
		// log.debug("Keys name={}", name);
		// }
		// Set<byte[]> keys = jedisCluster.hkeys(name);
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
		// Collection<byte[]> vals = jedisCluster.hvals(name);
		// if (vals != null && !vals.isEmpty()) {
		// return vals.stream().collect(Collectors.toList());
		// }
		// return Collections.emptyList();
		throw new UnsupportedOperationException();
	}

	@Override
	public Long timeToLive(CacheKey key, Object value) throws CacheException {
		notNull(value, "TTL key is null, please check configure");
		notNull(value, "TTL value is null, please check configure");

		byte[] realKey = key.getKey(name);
		// New create.
		if (!jedisCluster.exists(realKey)) {
			// key -> createTime
			jedisCluster.set(realKey, String.valueOf(value).getBytes(Charsets.UTF_8));
		}

		// Get last TTL expire
		Long lastTTL = jedisCluster.ttl(realKey);
		// Less than or equal to 0 means immediate expiration
		if (key.hasExpire()) {
			jedisCluster.expire(realKey, key.getExpire());
		}

		return lastTTL;
	}

	@Override
	public Long incrementGet(CacheKey key) throws CacheException {
		return incrementGet(key, 1);
	}

	@Override
	public Long incrementGet(CacheKey key, long incrBy) throws CacheException {
		byte[] realKey = key.getKey(name);
		// Increment
		Long res = jedisCluster.incrBy(key.getKey(name), incrBy);
		// Less than or equal to 0 means immediate expiration
		if (key.hasExpire()) {
			jedisCluster.expire(realKey, key.getExpire());
		}
		return res;
	}

	@Override
	public Long decrementGet(CacheKey key) throws CacheException {
		return decrementGet(key, 1);
	}

	@Override
	public Long decrementGet(CacheKey key, long decrBy) throws CacheException {
		byte[] realKey = key.getKey(name);
		// Decrement
		Long res = jedisCluster.decr(realKey);
		// Less than or equal to 0 means immediate expiration
		if (key.hasExpire()) {
			jedisCluster.expire(realKey, key.getExpire());
		}
		return res;
	}

	@Override
	public boolean putIfAbsent(final CacheKey key, final Object value) {
		notNull(key, "'key' must not be null");
		notNull(value, "'value' must not be null");
		log.debug("Put key={}, value={}", key, value);

		byte[] data = null;
		if (key.getSerializer() != null) { // Using a custom serializer
			data = key.getSerializer().serialize(value);
		} else {
			data = serialize(value);
		}

		if (key.hasExpire()) {
			return jedisCluster.set(key.getKey(name), data, NXXX, EXPX, key.getExpireMs()) != null;
		}
		return !isNull(jedisCluster.setnx(key.getKey(name), data));
	}

	// --- Enhanced API. ---

	@Override
	public String mapPut(CacheKey fieldKey, Object fieldValue) {
		notNull(fieldKey, "fieldKey");
		notNull(fieldValue, "fieldValue");
		log.debug("mapPut key={}, value={}", fieldKey, fieldValue);
		return mapPutAll(singletonMap(fieldKey, fieldValue), fieldKey.getExpire());
	}

	@Override
	public String mapPutAll(Map<Object, Object> map) {
		return mapPutAll(map, 0);
	}

	@Override
	public String mapPutAll(Map<Object, Object> map, int expireSec) {
		if (isEmpty(map))
			return null;
		log.debug("mapPut map={}", map);

		// Convert to fields map.
		Map<byte[], byte[]> dataMap = map.entrySet().stream().collect(toMap(e -> {
			notNull(e.getKey(), "fieldKey");
			if (e.getKey() instanceof CacheKey) {
				return ((CacheKey) e.getKey()).getKey();
			}
			return serialize(e.getKey());
		}, e -> {
			notNull(e.getValue(), "fieldValue");
			return serialize(e.getValue());
		}));
		// Hash map sets
		byte[] mapKey = toKeyBytes(name);
		String res = jedisCluster.hmset(mapKey, dataMap);
		if (expireSec > 0) {
			jedisCluster.expire(mapKey, expireSec);
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getMapField(CacheKey fieldKey) {
		notNullOf(fieldKey, "fieldKey");
		notNullOf(fieldKey.getValueClass(), "valueClass");
		byte[] data = jedisCluster.hget(toKeyBytes(name), fieldKey.getKey());
		return isNull(data) ? null : (T) deserialize(data, fieldKey.getValueClass());
	}

	@Override
	public <T> Map<String, T> getMapAll(Class<T> valueClass) {
		return safeMap(jedisCluster.hgetAll(toKeyBytes(name))).entrySet().stream()
				.collect(toMap(e -> new String(e.getKey(), UTF_8), e -> {
					if (isNull(e.getValue()))
						return null;
					return deserialize(e.getValue(), valueClass);
				}));
	}

	@Override
	public Map<byte[], byte[]> getMapAll() {
		return jedisCluster.hgetAll(toKeyBytes(name));
	}

	@Override
	public Long mapRemove(String fieldKey) {
		hasTextOf(fieldKey, "fieldKey");
		return jedisCluster.hdel(toKeyBytes(name), toKeyBytes(fieldKey));
	}

	@Override
	public void mapRemoveAll() {
		jedisCluster.del(toKeyBytes(name));
	}

}