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
package com.wl4g.devops.support.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wl4g.devops.common.utils.StringUtils2;
import com.wl4g.devops.common.utils.serialize.ObjectUtils;

import redis.clients.jedis.JedisCluster;

public class JedisService {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	private JedisCluster jedisCluster;

	public JedisService(JedisCluster jedisCluster) {
		Assert.isTrue(jedisCluster != null, "Redis cluster object creation failed.");
		this.jedisCluster = jedisCluster;
	}

	public JedisCluster getJedisCluster() {
		return this.jedisCluster;
	}

	public String get(final String key) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			String value = jedisCluster.get(key);
			value = StringUtils2.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
			if (log.isDebugEnabled())
				log.debug("get {} = {}", key, value);
			return value;
		});
	}

	public Object getObject(final String key) {
		return doInRedis((JedisCluster jedisCluster) -> {
			Object value = toObject(jedisCluster.get(getBytesKey(key)));
			if (log.isDebugEnabled()) {
				log.debug("getObject {} = {}", key, value);
			}
			return value;
		});
	}

	public String set(final String key, final String value, final int cacheSeconds) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			String result = null;
			if (cacheSeconds != 0) {
				result = jedisCluster.setex(key, cacheSeconds, value);
			} else {
				result = jedisCluster.set(key, value);
			}
			if (log.isDebugEnabled()) {
				log.debug("set {} = {}", key, value);
			}
			return result;
		});

	}

	public String setObject(final String key, final Object value, final int cacheSeconds) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			String result = jedisCluster.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0) {
				jedisCluster.expire(key, cacheSeconds);
			}
			if (log.isDebugEnabled()) {
				log.debug("setObject {} = {}", key, value);
			}
			return result;
		});
	}

	@SuppressWarnings("unchecked")
	public List<String> getList(final String key) {
		return (List<String>) doInRedis((JedisCluster jedisCluster) -> {
			List<String> value = jedisCluster.lrange(key, 0, -1);
			if (log.isDebugEnabled()) {
				log.debug("getList {} = {}", key, value);
			}
			return value;
		});
	}

	@SuppressWarnings("unchecked")
	public List<Object> getObjectList(final String key) {
		return (List<Object>) doInRedis((JedisCluster jedisCluster) -> {
			List<Object> value = Lists.newArrayList();
			List<byte[]> list = jedisCluster.lrange(getBytesKey(key), 0, -1);
			for (byte[] bs : list) {
				value.add(toObject(bs));
			}
			return value;
		});
	}

	public Long setList(final String key, final List<String> value, final int cacheSeconds) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = jedisCluster.rpush(key, value.toArray(new String[] {}));
			if (cacheSeconds > 0) {
				jedisCluster.expire(key, cacheSeconds);
			}
			if (log.isDebugEnabled()) {
				log.debug("setList {} = {}", key, value);
			}
			return result;
		});
	}

	public Long setObjectList(final String key, final List<Object> value, final int cacheSeconds) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (value != null && !value.isEmpty()) {
				byte[][] members = new byte[value.size()][0];
				int i = 0;
				for (Object o : value) {
					members[i] = toBytes(o);
					++i;
				}
				result = jedisCluster.sadd(getBytesKey(key), members);
			}
			if (cacheSeconds != 0) {
				jedisCluster.expire(key, cacheSeconds);
			}
			if (log.isDebugEnabled()) {
				log.debug("setObjectList {} = {}", key, value);
			}
			return result;
		});
	}

	public Long listAdd(final String key, final String... value) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = jedisCluster.rpush(key, value);
			if (log.isDebugEnabled())
				log.debug("listAdd {} = {}", key, value);
			return result;
		});
	}

	public Long listObjectAdd(final String key, final Object... value) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (value != null && value.length != 0) {
				byte[][] members = new byte[value.length][0];
				int i = 0;
				for (Object o : value) {
					members[i] = this.toBytes(o);
					++i;
				}
				result = jedisCluster.rpush(getBytesKey(key), members);
			}
			if (log.isDebugEnabled()) {
				log.debug("listObjectAdd {} = {}", key, value);
			}
			return result;
		});
	}

	@SuppressWarnings("unchecked")
	public Set<String> getSet(final String key) {
		return (Set<String>) doInRedis((JedisCluster jedisCluster) -> {

			Set<String> value = jedisCluster.smembers(key);
			if (log.isDebugEnabled())
				log.debug("getSet {} = {}", key, value);
			return value;
		});

	}

	@SuppressWarnings("unchecked")
	public <T> Set<T> getObjectSet(final String key) {
		return (Set<T>) doInRedis((JedisCluster jedisCluster) -> {

			Set<T> value = Sets.newHashSet();
			Set<byte[]> set = jedisCluster.smembers(getBytesKey(key));
			for (byte[] bs : set) {
				value.add((T) toObject(bs));
			}
			if (log.isDebugEnabled()) {
				log.debug("getObjectSet {} = {}", key, value);
			}
			return value;
		});

	}

	public Long setSet(final String key, final Set<String> value, final int cacheSeconds) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (value != null && !value.isEmpty())
				result = jedisCluster.sadd(key, value.toArray(new String[] {}));
			if (cacheSeconds != 0)
				jedisCluster.expire(key, cacheSeconds);
			if (log.isDebugEnabled())
				log.debug("setSet {} = {}", key, value);
			return result;
		});

	}

	/**
	 * 设置Set缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public Long setObjectSet(final String key, final Set<Object> value, final int cacheSeconds) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (value != null && !value.isEmpty()) {
				byte[][] members = new byte[value.size()][0];
				int i = 0;
				for (Object o : value) {
					members[i] = this.toBytes(o);
					++i;
				}
				result = this.jedisCluster.sadd(getBytesKey(key), members);
			}
			if (cacheSeconds != 0)
				this.jedisCluster.expire(key, cacheSeconds);

			if (log.isDebugEnabled()) {
				log.debug("setObjectSet {} = {}", key, value);
			}
			return result;
		});
	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public Long setSetAdd(final String key, final String... value) {

		return (Long) doInRedis((JedisCluster jedisCluster) -> {

			Long result = 0L;
			if (value != null && value.length != 0)
				result = jedisCluster.sadd(key, value);
			if (log.isDebugEnabled()) {
				log.debug("setSetAdd {} = {}", key, value);
			}
			return result;
		});

	}

	/**
	 * 向Set缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public Long setSetObjectAdd(final String key, final Object... value) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (value != null && value.length != 0) {
				byte[][] members = new byte[value.length][0];
				int i = 0;
				for (Object o : value) {
					members[i] = toBytes(o);
					++i;
				}
				result = jedisCluster.sadd(getBytesKey(key), members);
			}
			log.debug("setSetObjectAdd {} = {}", key, value);
			return result;
		});

	}

	/**
	 * 删除Set缓存中普通成员
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public Long delSetMember(final String key, final String... members) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (members != null && members.length != 0)
				result = jedisCluster.srem(key, members);
			if (log.isDebugEnabled()) {
				log.debug("delSetMember {}", key);
			}
			return result;
		});

	}

	/**
	 * 删除Set缓存中对象成员
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public Long delSetObjectMember(final String key, final Object... members) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = 0L;
			if (members != null && members.length != 0) {
				byte[][] members0 = new byte[members.length][0];
				int i = 0;
				for (Object o : members) {
					members0[i] = toBytes(o);
					++i;
				}
				result = jedisCluster.srem(getBytesKey(key), members0);
			}
			if (log.isDebugEnabled()) {
				log.debug("delSetMember {}", key);
			}
			return result;
		});
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(final String key) {
		return (Map<String, String>) doInRedis((JedisCluster jedisCluster) -> {
			Map<String, String> value = jedisCluster.hgetAll(key);
			if (log.isDebugEnabled()) {
				log.debug("getMap {} = {}", key, value);
			}
			return value;
		});
	}

	/**
	 * 获取Map缓存
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getObjectMap(final String key) {
		return (Map<String, Object>) doInRedis((JedisCluster jedisCluster) -> {
			Map<String, Object> value = Maps.newHashMap();
			Map<byte[], byte[]> map = jedisCluster.hgetAll(getBytesKey(key));
			for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
				value.put(StringUtils2.toString(e.getKey()), toObject(e.getValue()));
			}
			if (log.isDebugEnabled()) {
				log.debug("getObjectMap {} = {}", key, value);
			}
			return value;
		});
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public String setMap(final String key, final Map<String, String> value, final int cacheSeconds) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			String result = jedisCluster.hmset(key, value);
			if (cacheSeconds != 0) {
				jedisCluster.expire(key, cacheSeconds);
			}
			if (log.isDebugEnabled()) {
				log.debug("setMap {} = {}", key, value);
			}
			return result;
		});
	}

	/**
	 * 设置Map缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param cacheSeconds
	 *            超时时间，0为不超时
	 * @return
	 */
	public String setObjectMap(final String key, final Map<String, Object> value, final int cacheSeconds) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			String result = jedisCluster.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if (cacheSeconds != 0) {
				jedisCluster.expire(key, cacheSeconds);
			}
			if (log.isDebugEnabled()) {
				log.debug("setObjectMap {} = {}", key, value);
			}
			return result;
		});
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public String mapPut(final String key, final Map<String, String> value) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			String result = jedisCluster.hmset(key, value);
			if (log.isDebugEnabled()) {
				log.debug("mapPut {} = {}", key, value);
			}
			return result;
		});
	}

	/**
	 * 向Map缓存中添加值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public String mapObjectPut(final String key, final Map<String, Object> value) {
		return (String) doInRedis((JedisCluster jedisCluster) -> {
			String result = null;
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = jedisCluster.hmset(getBytesKey(key), map);
			if (log.isDebugEnabled()) {
				log.debug("mapObjectPut {} = {}", key, value);
			}
			return result;
		});
	}

	/**
	 * 移除Map缓存中的值
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return
	 */
	public Long mapRemove(final String key, final String mapKey) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = jedisCluster.hdel(key, mapKey);
			if (log.isDebugEnabled()) {
				log.debug("mapRemove {}  {}", key, mapKey);
			}
			return result;
		});
	}

	public Long mapObjectRemove(final String key, final String mapKey) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = jedisCluster.hdel(getBytesKey(key), getBytesKey(mapKey));
			if (log.isDebugEnabled()) {
				log.debug("mapObjectRemove {}  {}", key, mapKey);
			}
			return result;
		});
	}

	public Boolean mapExists(final String key, final String mapKey) {
		return (Boolean) doInRedis((JedisCluster jedisCluster) -> {
			Boolean result = jedisCluster.hexists(key, mapKey);
			if (log.isDebugEnabled()) {
				log.debug("mapObjectExists {}  {}", key, mapKey);
			}
			return result;
		});

	}

	public Boolean mapObjectExists(final String key, final String mapKey) {
		return (Boolean) doInRedis((JedisCluster jedisCluster) -> {
			Boolean result = jedisCluster.hexists(getBytesKey(key), getBytesKey(mapKey));
			if (log.isDebugEnabled()) {
				log.debug("mapObjectExists {}  {}", key, mapKey);
			}
			return result;
		});

	}

	public Long del(final String key) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {
			Long result = jedisCluster.del(key);
			if (log.isDebugEnabled()) {
				log.debug("del {}", key);
			}
			return result;
		});

	}

	public Long delObject(final String key) {
		return (Long) doInRedis((JedisCluster jedisCluster) -> {

			long result = jedisCluster.del(getBytesKey(key));
			if (log.isDebugEnabled()) {
				log.debug("delObject {}", key);
			}
			return result;
		});

	}

	public Boolean exists(final String key) {
		return (Boolean) doInRedis((JedisCluster jedisCluster) -> {

			Boolean result = jedisCluster.exists(key);
			if (log.isDebugEnabled())
				log.debug("exists {}", key);
			return result;
		});

	}

	public Boolean existsObject(final String key) {
		return (Boolean) doInRedis((JedisCluster jedisCluster) -> {
			boolean result = jedisCluster.exists(getBytesKey(key));
			if (log.isDebugEnabled()) {
				log.debug("existsObject {}", key);
			}
			return result;
		});
	}

	public byte[] getBytesKey(Object object) {
		if (object instanceof String) {
			return StringUtils2.getBytes((String) object);
		} else {
			return ObjectUtils.serialize(object);
		}
	}

	public byte[] toBytes(Object object) {
		return ObjectUtils.serialize(object);
	}

	public Object toObject(byte[] bytes) {
		return ObjectUtils.unserialize(bytes);
	}

	private Object doInRedis(Callback callback) {
		try {
			return callback.execute(jedisCluster);
		} catch (Throwable t) {
			log.error("Redis processing fail.", t);
			throw t;
		} finally {
			// Redis cluster mode does not need to display the release of
			// resources.
		}
	}

	public static abstract interface Callback {
		public Object execute(JedisCluster jedisCluster);
	}
}