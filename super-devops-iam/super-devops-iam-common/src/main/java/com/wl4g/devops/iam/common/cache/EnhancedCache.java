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