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
package com.wl4g.devops.iam.common.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.shiro.util.Assert.hasText;
import static org.apache.shiro.util.Assert.isTrue;
import static org.apache.shiro.util.Assert.notEmpty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.session.InvalidSessionException;
import org.springframework.util.Assert;

/**
 * Session binding utility
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月21日
 * @since
 */
public abstract class SessionBindings extends Sessions {

	final private static String KEY_SESSION_ATTR_TTL_PREFIX = "attributeTTL_";

	/**
	 * Whether the comparison with the target is equal from the session
	 * attribute-Map
	 * 
	 * @param sessionKey
	 *            Keys to save and session
	 * @param target
	 *            Target object
	 * @param unbind
	 *            Whether to UN-bundle
	 * @return Is there a value in session that matches the target
	 */
	public static boolean withIn(String sessionKey, Object target, boolean unbind) {
		try {
			return withIn(sessionKey, target);
		} finally {
			unbind(sessionKey);
		}
	}

	/**
	 * Whether the comparison with the target is equal from the session
	 * attribute-Map
	 * 
	 * @param sessionKey
	 *            Keys to save and session
	 * @param target
	 *            Target object
	 * @return Is there a value in session that matches the target
	 */
	public static boolean withIn(String sessionKey, Object target) {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		Assert.notNull(target, "'target' must not be null");

		Object sessionValue = getBindValue(sessionKey);
		if (sessionValue != null) {
			if ((sessionValue instanceof String) && (target instanceof String)) { // String
				return String.valueOf(sessionValue).equalsIgnoreCase(String.valueOf(target));
			} else if ((sessionValue instanceof Enum) || (target instanceof Enum)) { // ENUM
				return (sessionValue == target || sessionValue.toString().equalsIgnoreCase(target.toString()));
			} else { // Other object
				return sessionValue == target;
			}
		}
		return false;
	}

	/**
	 * Get bind of session value
	 * 
	 * @param sessionKey
	 *            Keys to save and session
	 * @param unbind
	 *            Whether to UN-bundle
	 * @return
	 */
	public static <T> T getBindValue(String sessionKey, boolean unbind) throws InvalidSessionException {
		try {
			return getBindValue(sessionKey);
		} finally {
			unbind(sessionKey);
		}
	}

	/**
	 * Get bind of session value
	 * 
	 * @param sessionKey
	 *            Keys to save and session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBindValue(String sessionKey) throws InvalidSessionException {
		Assert.hasText(sessionKey, "Session key must not be empty.");
		// Get bind value.
		T value = (T) getSession().getAttribute(sessionKey);
		// Get value TTL.
		SessionValueTTL ttl = (SessionValueTTL) getSession().getAttribute(getExpireKey(sessionKey));
		if (ttl != null) { // Need to check expiration
			if ((System.currentTimeMillis() - ttl.getCreateTime()) >= ttl.getExpireMs()) { // Expired?
				unbind(sessionKey); // Cleanup
				return null; // Because it's expired.
			}
		}
		return value;
	}

	/**
	 * Extract key value parameters
	 * 
	 * @param sessionKey
	 * @param paramKey
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T extParameterValue(String sessionKey, String paramKey) throws InvalidSessionException {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		Assert.notNull(paramKey, "'paramKey' must not be null");

		// Extract parameter
		Map parameter = (Map) getBindValue(sessionKey);
		if (parameter != null) {
			return (T) parameter.get(paramKey);
		}
		return null;
	}

	/**
	 * Bind value to session
	 * 
	 * @param sessionKey
	 * @param keyValues
	 */
	public static void bindKVParameters(String sessionKey, Object... keyValues) throws InvalidSessionException {
		hasText(sessionKey, "'sessionKey' must not be null");
		notEmpty(keyValues, "'keyValues' must not be null");
		isTrue(keyValues.length % 2 == 0, "Illegal 'keyValues' length");

		// Extract key values
		Map<Object, Object> parameters = new HashMap<>();
		for (int i = 0; i < keyValues.length - 1; i++) {
			if (i % 2 == 0) {
				Object key = keyValues[i];
				Object value = keyValues[i + 1];
				if (key != null && isNotBlank(key.toString()) && value != null && isNotBlank(value.toString())) {
					parameters.put(key, value);
				}
			}
		}

		// Binding
		bind(sessionKey, parameters);
	}

	/**
	 * Bind value to session
	 * 
	 * @param sessionKey
	 * @param value
	 * @param expireMs
	 * @return
	 */
	public static <T> T bind(String sessionKey, T value, long expireMs) throws InvalidSessionException {
		Assert.isTrue(expireMs > 0, "Expire time must be greater than 0");
		bind(sessionKey, value);
		bind(getExpireKey(sessionKey), new SessionValueTTL(expireMs));
		return value;
	}

	/**
	 * Bind value to session
	 * 
	 * @param sessionKey
	 * @param value
	 */
	public static <T> T bind(String sessionKey, T value) throws InvalidSessionException {
		Assert.hasText(sessionKey, "Session key must not be empty.");
		getSession().setAttribute(sessionKey, value);
		return value;
	}

	/**
	 * UN-bind sessionKey of session
	 * 
	 * @param sessionKey
	 * @return
	 */
	public static boolean unbind(String sessionKey) throws InvalidSessionException {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		getSession().removeAttribute(getExpireKey(sessionKey)); // TTL-attribute?
		return getSession().removeAttribute(sessionKey) != null;
	}

	/**
	 * Get expire key.
	 * 
	 * @param sessionKey
	 * @return
	 */
	private static String getExpireKey(String sessionKey) {
		Assert.hasText(sessionKey, "'sessionKey' must not be empty");
		return KEY_SESSION_ATTR_TTL_PREFIX + sessionKey;
	}

	/**
	 * Session attribute timeToLive model
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月23日
	 * @since
	 */
	public static class SessionValueTTL implements Serializable {
		private static final long serialVersionUID = -5108678535942593956L;

		/**
		 * Create time.
		 */
		final private Long createTime;

		/**
		 * Expire time.
		 */
		final private Long expireMs;

		public SessionValueTTL(Long expireMs) {
			this(System.currentTimeMillis(), expireMs);
		}

		public SessionValueTTL(Long createTime, Long expireMs) {
			Assert.state(createTime != null, "'createTime' must not be null.");
			Assert.state(expireMs != null, "'expireMs' must not be null.");
			this.createTime = createTime;
			this.expireMs = expireMs;
		}

		public Long getCreateTime() {
			return createTime;
		}

		public Long getExpireMs() {
			return expireMs;
		}

	}

}