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
package com.wl4g.devops.support.redis.jedis;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wl4g.devops.components.tools.common.lang.StringUtils2;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.serialize.JdkSerializeUtils;
import com.wl4g.devops.components.tools.common.serialize.ProtostuffUtils;

import org.springframework.util.Assert;
import redis.clients.jedis.ScanParams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.wl4g.devops.components.tools.common.collection.Collections2.safeList;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.components.tools.common.serialize.ProtostuffUtils.serialize;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * JEDIS adapter service template.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018年9月16日
 * @since
 */
public class JedisService {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link CompositeJedisOperatorsAdapter}
	 */
	final protected CompositeJedisOperatorsAdapter jedisAdapter;

	public JedisService(CompositeJedisOperatorsAdapter jedisAdapter) {
		Assert.isTrue(jedisAdapter != null, "jedisAdapter");
		this.jedisAdapter = jedisAdapter;
	}

	public CompositeJedisOperatorsAdapter getJedisAdapter() {
		return this.jedisAdapter;
	}

	// --- Basic ---

	public String get(final String key) {
		return doExecuteWithRedis(adapter -> {
			String value = adapter.get(key);
			value = !isBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
			log.debug("get {} = {}", key, value);
			return value;
		});
	}

	public String set(final String key, final String value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			String result = null;
			if (cacheSeconds != 0) {
				result = adapter.setex(key, cacheSeconds, value);
			} else {
				result = adapter.set(key, value);
			}
			log.debug("set {} = {}", key, value);
			return result;
		});

	}

	public <T> ScanCursor<T> scan(final String pattern, final int batch, final Class<T> valueType) {
		byte[] match = trimToEmpty(pattern).getBytes(Charsets.UTF_8);
		ScanParams params = new ScanParams().count(batch).match(match);
		return new ScanCursor<T>(getJedisAdapter(), valueType, params) {
		}.open();
	}

	public Long del(final String key) {
		return doExecuteWithRedis(adapter -> {
			Long result = adapter.del(key);
			log.debug("del {}", key);
			return result;
		});

	}

	public Long delObject(final String key) {
		return doExecuteWithRedis(adapter -> {
			long result = adapter.del(getBytesKey(key));
			log.debug("delObject {}", key);
			return result;
		});
	}

	public Boolean exists(final String key) {
		return doExecuteWithRedis(adapter -> {
			Boolean result = adapter.exists(key);
			if (log.isDebugEnabled())
				log.debug("exists {}", key);
			return result;
		});
	}

	public Boolean existsObject(final String key) {
		return doExecuteWithRedis(adapter -> {
			boolean result = adapter.exists(getBytesKey(key));
			log.debug("existsObject {}", key);
			return result;
		});
	}

	// --- ObjectT ---

	public <T> T getObjectT(final String key, Class<T> clazz) {
		return doExecuteWithRedis(adapter -> {
			T value = ProtostuffUtils.deserialize(adapter.get(getBytesKey(key)), clazz);
			log.debug("getObjectT {} = {}", key, value);
			return value;
		});
	}

	public <T> String setObjectT(final String key, final T value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			String result = null;
			if (cacheSeconds > 0) {
				result = adapter.setex(getBytesKey(key), cacheSeconds, serialize(value));
			} else {
				byte[] serialize = serialize(value);
				System.out.println(serialize.length);
				result = adapter.set(getBytesKey(key), serialize);
			}
			log.debug("setObjectT {} = {}", key, value);
			return result;
		});
	}

	// --- ObjectAsJson ---

	public <T> String setObjectAsJson(final String key, final T value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			String result = null;
			if (cacheSeconds != 0) {
				result = adapter.setex(key, cacheSeconds, toJSONString(value));
			} else {
				result = adapter.set(key, toJSONString(value));
			}
			log.debug("setObjectAsJson {} = {}", key, value);
			return result;
		});
	}

	public <T> T getObjectAsJson(final String key, Class<T> clazz) {
		return doExecuteWithRedis(adapter -> {
			String json = adapter.get(key);
			if (isBlank(json)) {
				return null;
			}
			T value = parseJSON(json, clazz);
			log.debug("getObjectAsJson {} = {}", key, value);
			return value;
		});
	}

	// --- get/set object ---

	public Object getObject(final String key) {
		return doExecuteWithRedis(adapter -> {
			Object value = toObject(adapter.get(getBytesKey(key)));
			log.debug("getObject {} = {}", key, value);
			return value;
		});
	}

	public String setObject(final String key, final Object value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			String result = adapter.set(getBytesKey(key), toBytes(value));
			if (cacheSeconds != 0) {
				adapter.expire(key, cacheSeconds);
			}
			log.debug("setObject {} = {}", key, value);
			return result;
		});
	}

	// --- String list ---

	public List<String> getList(final String key) {
		return doExecuteWithRedis(adapter -> {
			List<String> value = adapter.lrange(key, 0, -1);
			log.debug("getList {} = {}", key, value);
			return value;
		});
	}

	public Long setList(final String key, final List<String> values, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			Long result = adapter.rpush(key, values.toArray(new String[] {}));
			if (cacheSeconds > 0) {
				adapter.expire(key, cacheSeconds);
			}
			log.debug("setList {} = {}", key, values);
			return result;
		});
	}

	public Long listAdd(final String key, final String... values) {
		return doExecuteWithRedis(adapter -> {
			Long result = adapter.rpush(key, values);
			log.debug("listAdd {} = {}", key, values);
			return result;
		});
	}

	/**
	 * Delete ordinary members from List cache
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public Long delListMember(final String key, final String member) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (!isBlank(member)) {
				result = adapter.lrem(key, 0, member);
			}
			log.debug("delListMember {}", key);
			return result;
		});
	}

	// --- Object list ---

	public <T> List<T> getObjectList(final String key, final Class<T> clazz) {
		return doExecuteWithRedis(adapter -> {
			return safeList(adapter.lrange(key, 0, -1)).stream().map(e -> parseJSON(e, clazz)).collect(toList());
		});
	}

	public <T> Long setObjectList(final String key, final List<T> values, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (!isEmpty(values)) {
				List<String> members = safeList(values).stream().map(v -> toJSONString(v)).collect(toList());
				result = adapter.rpush(key, members.toArray(new String[] {}));
			}
			if (cacheSeconds != 0) {
				adapter.expire(key, cacheSeconds);
			}
			log.debug("setObjectList {} = {}", key, values);
			return result;
		});
	}

	@SuppressWarnings("unchecked")
	public <T> Long listObjectAdd(final String key, final T... values) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (values != null && values.length != 0) {
				String[] members = new String[values.length];
				for (int i = 0; i < values.length; i++) {
					members[i] = toJSONString(values[i]);
				}
				result = adapter.rpush(key, members);
			}
			log.debug("listObjectAdd {} = {}", key, values);
			return result;
		});
	}

	// --- String set ---

	public Set<String> getSet(final String key) {
		return doExecuteWithRedis(adapter -> {
			Set<String> value = adapter.smembers(key);
			log.debug("getSet {} = {}", key, value);
			return value;
		});

	}

	public Long setSet(final String key, final Set<String> value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (value != null && !value.isEmpty())
				result = adapter.sadd(key, value.toArray(new String[] {}));
			if (cacheSeconds != 0)
				adapter.expire(key, cacheSeconds);

			log.debug("setSet {} = {}", key, value);
			return result;
		});

	}

	/**
	 * Adding values to Set cache
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setSetAdd(final String key, final String... value) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (value != null && value.length != 0)
				result = adapter.sadd(key, value);
			log.debug("setSetAdd {} = {}", key, value);
			return result;
		});
	}

	/**
	 * Delete ordinary members from Set cache
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	public Long delSetMember(final String key, final String... members) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (members != null && members.length != 0)
				result = adapter.srem(key, members);
			log.debug("delSetMember {}", key);
			return result;
		});
	}

	// --- Object set ---

	@SuppressWarnings("unchecked")
	public <T> Set<T> getObjectSet(final String key) {
		return doExecuteWithRedis(adapter -> {
			Set<T> value = Sets.newHashSet();
			Set<byte[]> set = adapter.smembers(getBytesKey(key));
			for (byte[] bs : set) {
				value.add((T) toObject(bs));
			}
			log.debug("getObjectSet {} = {}", key, value);
			return value;
		});
	}

	/**
	 * Set caching
	 * 
	 * @param key
	 * @param value
	 * @param cacheSeconds
	 *            Time-out, 0 is no time-out
	 * @return
	 */
	public Long setObjectSet(final String key, final Set<Object> value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (value != null && !value.isEmpty()) {
				byte[][] members = new byte[value.size()][0];
				int i = 0;
				for (Object o : value) {
					members[i] = toBytes(o);
					++i;
				}
				result = adapter.sadd(getBytesKey(key), members);
			}
			if (cacheSeconds != 0) {
				adapter.expire(key, cacheSeconds);
			}
			log.debug("setObjectSet {} = {}", key, value);
			return result;
		});
	}

	/**
	 * Adding values to Set cache
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setSetObjectAdd(final String key, final Object... value) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (value != null && value.length != 0) {
				byte[][] members = new byte[value.length][0];
				int i = 0;
				for (Object o : value) {
					members[i] = toBytes(o);
					++i;
				}
				result = adapter.sadd(getBytesKey(key), members);
			}
			log.debug("setSetObjectAdd {} = {}", key, value);
			return result;
		});

	}

	/**
	 * Delete object members in set Cache
	 * 
	 * @param key
	 * @param members
	 * @return
	 */
	@Deprecated
	public Long delSetObjectMember(final String key, final Object... members) {
		return doExecuteWithRedis(adapter -> {
			Long result = 0L;
			if (members != null && members.length != 0) {
				byte[][] members0 = new byte[members.length][0];
				int i = 0;
				for (Object o : members) {
					members0[i] = toBytes(o);
					++i;
				}
				result = adapter.srem(getBytesKey(key), members0);
			}
			log.debug("delSetMember {}", key);
			return result;
		});
	}

	// --- get/set map ---

	/**
	 * Getting Map Cache
	 * 
	 * @param key
	 * @return
	 */

	public Map<String, String> getMap(final String key) {
		return (Map<String, String>) doExecuteWithRedis(adapter -> {
			Map<String, String> value = adapter.hgetAll(key);
			log.debug("getMap {} = {}", key, value);
			return value;
		});
	}

	/**
	 * Setting up Map Cache
	 * 
	 * @param key
	 * @param value
	 * @param cacheSeconds
	 *            Time-out, 0 is no time-out
	 * @return
	 */
	public String setMap(final String key, final Map<String, String> value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			String result = adapter.hmset(key, value);
			if (cacheSeconds != 0) {
				adapter.expire(key, cacheSeconds);
			}
			log.debug("setMap {} = {}", key, value);
			return result;
		});
	}

	/**
	 * Adding values to the Map cache
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String mapPut(final String key, final Map<String, String> value) {
		return doExecuteWithRedis(adapter -> {
			String result = adapter.hmset(key, value);
			log.debug("mapPut {} = {}", key, value);
			return result;
		});
	}

	/**
	 * Remove the value from the Map cache
	 * 
	 * @param key
	 * @param mapKey
	 * @return
	 */
	public Long mapRemove(final String key, final String mapKey) {
		return doExecuteWithRedis(adapter -> {
			Long result = adapter.hdel(key, mapKey);
			log.debug("mapRemove {}  {}", key, mapKey);
			return result;
		});
	}

	public Boolean mapExists(final String key, final String mapKey) {
		return doExecuteWithRedis(adapter -> {
			Boolean result = adapter.hexists(key, mapKey);
			log.debug("mapObjectExists {}  {}", key, mapKey);
			return result;
		});

	}

	// --- get/set object map ---

	/**
	 * Getting Map Cache
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, Object> getObjectMap(final String key) {
		return doExecuteWithRedis(adapter -> {
			Map<String, Object> value = Maps.newHashMap();
			Map<byte[], byte[]> map = adapter.hgetAll(getBytesKey(key));
			for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
				value.put(StringUtils2.toString(e.getKey()), toObject(e.getValue()));
			}
			log.debug("getObjectMap {} = {}", key, value);
			return value;
		});
	}

	/**
	 * Setting up Map Cache
	 * 
	 * @param key
	 * @param value
	 * @param cacheSeconds
	 *            Time-out, 0 is no time-out
	 * @return
	 */
	public String setObjectMap(final String key, final Map<String, Object> value, final int cacheSeconds) {
		return doExecuteWithRedis(adapter -> {
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			String result = adapter.hmset(getBytesKey(key), (Map<byte[], byte[]>) map);
			if (cacheSeconds != 0) {
				adapter.expire(key, cacheSeconds);
			}
			log.debug("setObjectMap {} = {}", key, value);
			return result;
		});
	}

	/**
	 * Adding values to the Map cache
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String mapObjectPut(final String key, final Map<String, Object> value) {
		return doExecuteWithRedis(adapter -> {
			String result = null;
			Map<byte[], byte[]> map = Maps.newHashMap();
			for (Map.Entry<String, Object> e : value.entrySet()) {
				map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
			}
			result = adapter.hmset(getBytesKey(key), map);
			log.debug("mapObjectPut {} = {}", key, value);
			return result;
		});
	}

	@Deprecated
	public Long mapObjectRemove(final String key, final String mapKey) {
		return doExecuteWithRedis(adapter -> {
			Long result = adapter.hdel(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectRemove {}  {}", key, mapKey);
			return result;
		});
	}

	public Boolean mapObjectExists(final String key, final String mapKey) {
		return doExecuteWithRedis(adapter -> {
			Boolean result = adapter.hexists(getBytesKey(key), getBytesKey(mapKey));
			log.debug("mapObjectExists {}  {}", key, mapKey);
			return result;
		});
	}

	/**
	 * Do execute with redis operations.
	 * 
	 * @param invoker
	 * @return
	 */
	private <T> T doExecuteWithRedis(Function<CompositeJedisOperatorsAdapter, T> invoker) {
		try {
			return invoker.apply(jedisAdapter);
		} catch (Throwable t) {
			log.error("Redis processing fail.", t);
			throw t;
		} finally {
			// Redis adapter mode does not need to display the release of
			// resources.
		}
	}

	// --- Function's ---

	public static byte[] getBytesKey(Object object) {
		if (object instanceof String) {
			return StringUtils2.getBytes((String) object);
		} else {
			return JdkSerializeUtils.serialize(object);
		}
	}

	public static byte[] toBytes(Object object) {
		return JdkSerializeUtils.serialize(object);
	}

	public static Object toObject(byte[] bytes) {
		return JdkSerializeUtils.unserialize(bytes);
	}

}