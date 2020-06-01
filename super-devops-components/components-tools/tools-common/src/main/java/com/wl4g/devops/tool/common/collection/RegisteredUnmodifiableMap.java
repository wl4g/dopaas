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
package com.wl4g.devops.tool.common.collection;

import static java.util.Collections.synchronizedMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.tool.common.lang.Assert2;

/**
 * The {@link Map} is applicable to the registration of framework instances. Its
 * feature is that after the registration is successful, it cannot be modified.
 * 
 * Note: it uses synchronous lock to control thread safety. It is not applicable
 * to high concurrency scenarios, but more applicable to registration
 * configuration when starting server.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月1日
 * @since
 */
public class RegisteredUnmodifiableMap<K, V> implements Map<K, V> {

	/**
	 * Read only map.
	 */
	final private Map<K, V> readOnlyMap;

	/**
	 * One-time modifiable map status marker.
	 */
	final private AtomicBoolean modified = new AtomicBoolean(false);

	public RegisteredUnmodifiableMap(Map<K, V> readOnlyMap) {
		Assert2.state(null != readOnlyMap, "Once modifiable read only map must not be null.");
		if (!(readOnlyMap instanceof ConcurrentMap)) {
			readOnlyMap = synchronizedMap(readOnlyMap);
		}
		this.readOnlyMap = readOnlyMap;
	}

	@Override
	public int size() {
		return readOnlyMap.size();
	}

	@Override
	public boolean isEmpty() {
		return readOnlyMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return readOnlyMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return readOnlyMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return readOnlyMap.get(key);
	}

	/**
	 * Only once initialize
	 */
	@Override
	public V put(K key, V value) {
		Assert2.notNull(key, "Once modifiable final map key must not be null.");
		Assert2.notNull(value, "Once modifiable final map value must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyMap.put(key, value);
		}
		throw new UnsupportedOperationException("A modifiable map does not support multiple modifications.");
	}

	@Override
	public V remove(Object key) {
		Assert2.notNull(key, "Once modifiable final map key must not be null.");
		if (modified.compareAndSet(false, true)) {
			return readOnlyMap.remove(key);
		}
		throw new UnsupportedOperationException("A modifiable map does not support multiple modifications.");
	}

	/**
	 * Only once initialize
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> finalMap) {
		Assert2.state(null != finalMap, "Once modifiable final map must not be null.");
		if (modified.compareAndSet(false, true)) {
			readOnlyMap.putAll(finalMap);
		} else {
			throw new UnsupportedOperationException("A modifiable map does not support multiple modifications.");
		}
	}

	@Override
	public void clear() {
		readOnlyMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return readOnlyMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return readOnlyMap.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return readOnlyMap.entrySet();
	}

}