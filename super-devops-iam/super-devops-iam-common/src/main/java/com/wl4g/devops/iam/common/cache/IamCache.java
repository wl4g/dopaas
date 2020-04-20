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

import static com.google.common.base.Charsets.UTF_8;

import java.util.Map;

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
public interface IamCache extends Cache<CacheKey, Object> {

	final static byte[] NXXX = "NX".getBytes(UTF_8);
	final static byte[] EXPX = "PX".getBytes(UTF_8);

	/**
	 * Decay counter
	 *
	 * @param key
	 *            Key that contains the actual EnhancedKey and expiration date
	 * @param value
	 * @return Returns the remaining valid time of this EnhancedKey before this
	 *         setup
	 * @throws CacheException
	 */
	Long timeToLive(CacheKey key, Object value) throws CacheException;

	/**
	 * Get and add an atomic counter at the same time
	 *
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	Long incrementGet(CacheKey key) throws CacheException;

	/**
	 * Get and add an atomic counter at the same time
	 *
	 * @param key
	 * @param incrBy
	 * @return
	 * @throws CacheException
	 */
	Long incrementGet(CacheKey key, long incrBy) throws CacheException;

	/**
	 * Acquisition and reduction of atomic counter once at a time
	 *
	 * @param key
	 * @return
	 * @throws CacheException
	 */
	Long decrementGet(CacheKey key) throws CacheException;

	/**
	 * Acquisition and reduction of atomic counter once at a time
	 *
	 * @param key
	 * @param decrBy
	 * @return
	 * @throws CacheException
	 */
	Long decrementGet(CacheKey key, long decrBy) throws CacheException;

	/**
	 * Put(If not exist)
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	boolean putIfAbsent(CacheKey key, Object value);

	// --- Enhanced API. ---

	/**
	 * Puts map field.
	 * 
	 * @param fieldKey
	 * @param fieldValue
	 * @param expireMs
	 * @return
	 */
	String mapPut(CacheKey fieldKey, Object fieldValue);

	/**
	 * Puts map fields all.
	 * 
	 * @param map
	 * @return
	 */
	String mapPutAll(Map<Object, Object> map);

	/**
	 * Puts map fields all.
	 * 
	 * @param map
	 * @param expireSec
	 * @return
	 */
	String mapPutAll(Map<Object, Object> map, int expireSec);

	/**
	 * Remove f ields map.
	 * 
	 * @param fieldKey
	 * @return
	 */
	Long mapRemove(String fieldKey);

	/**
	 * Remove fields ma p.
	 */
	void mapRemoveAll();

	/**
	 * Gets field of map.
	 * 
	 * @param fieldKey
	 * @return
	 */
	<T> T getMapField(CacheKey fieldKey);

	/**
	 * Gets fields map.
	 * 
	 * @return
	 */
	<T> Map<String, T> getMapAll(Class<T> valueClass);

	/**
	 * Gets fields map.
	 * 
	 * @return
	 */
	Map<byte[], byte[]> getMapAll();

}