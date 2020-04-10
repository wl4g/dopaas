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
package com.wl4g.devops.common.web.embedded;

import static java.util.Objects.isNull;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.concurrent.ConcurrentMapCache;

/**
 * Webapps cache.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月31日
 * @since
 */
public interface WebResourceCache {

	/**
	 * Return the value to which this cache maps the specified key.
	 * <p>
	 * 
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return
	 * @see #get(Object, Class)
	 */
	byte[] get(Object key);

	/**
	 * Associate the specified value with the specified key in this cache.
	 * <p>
	 * If the cache previously contained a mapping for this key, the old value
	 * is replaced by the specified value.
	 * 
	 * @param key
	 *            the key with which the specified value is to be associated
	 * @param value
	 *            the value to be associated with the specified key
	 */
	void put(String key, byte[] value);

	/**
	 * Evict the mapping for this key from this cache if it is present.
	 * 
	 * @param key
	 *            the key whose mapping is to be removed from the cache
	 */
	void evict(Object key);

	/**
	 * Remove all mappings from the cache.
	 */
	void clear();

	/**
	 * Default webapps cache.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年3月31日
	 * @since
	 */
	public static class DefaultWebappsGuavaCache implements WebResourceCache {

		final private Cache cache = new ConcurrentMapCache(getClass().getSimpleName() + ".WebResourceCache");

		@Override
		public byte[] get(Object key) {
			ValueWrapper value = cache.get(key);
			return isNull(value) ? null : (byte[]) value.get();
		}

		@Override
		public void put(String key, byte[] value) {
			cache.put(key, value);
		}

		@Override
		public void evict(Object key) {
			cache.evict(key);
		}

		@Override
		public void clear() {
			cache.clear();
		}

	}

}
