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

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_AUTHC_ACCOUNT_INFO;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.shiro.util.Assert.hasText;
import static org.apache.shiro.util.Assert.isTrue;
import static org.apache.shiro.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;

/**
 * Session bind holder utility.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月21日
 * @since
 */
public abstract class IamSecurityHolder extends SecurityUtils {
	final private static String KEY_ATTR_TTL_PREFIX = "attribute_ttl_";

	// --- Principal and session's. ---

	/**
	 * Getting current authenticate principal name.
	 *
	 * @param create
	 * @return
	 */
	public static String getPrincipal() {
		Object principal = getSubject().getPrincipal();
		notNull(principal,
				"The authentication subject is empty. The unauthenticated? or is @EnableIamServer/@EnableIamClient not enabled? Also note the call order!");
		return (String) principal;
	}

	/**
	 * Get current authenticate principal {@link IamPrincipalInfo}
	 * 
	 * @return
	 * @see {@link com.wl4g.devops.iam.realm.AbstractIamAuthorizingRealm#doGetAuthenticationInfo(AuthenticationToken)}
	 */
	public static IamPrincipalInfo getPrincipalInfo() {
		IamPrincipalInfo info = getBindValue(KEY_AUTHC_ACCOUNT_INFO);
		notNull(info,
				"The authentication subject is empty. The unauthenticated? or is @EnableIamServer/@EnableIamClient not enabled? Also note the call order!");
		return info;
	}

	/**
	 * Getting current session
	 *
	 * @param create
	 * @return
	 */
	public static Session getSession() {
		return getSubject().getSession(true);
	}

	/**
	 * Getting current session
	 *
	 * @param create
	 * @return
	 */
	public static Session getSession(boolean create) {
		return getSubject().getSession(create);
	}

	/**
	 * Getting session-id
	 *
	 * @return
	 */
	public static Serializable getSessionId() {
		return getSessionId(getSubject());
	}

	/**
	 * Getting session-id
	 *
	 * @param subject
	 * @return
	 */
	public static Serializable getSessionId(Subject subject) {
		Session session = subject.getSession();
		return (session != null) ? session.getId() : null;
	}

	/**
	 * Getting session-id
	 *
	 * @param session
	 * @return
	 */
	public static Serializable getSessionId(Session session) {
		return (session != null) ? session.getId() : null;
	}

	/**
	 * Get session expire time
	 *
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSessionExpiredTime() {
		return getSessionExpiredTime(getSession());
	}

	/**
	 * Get session expire time
	 *
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSessionExpiredTime(Session session) {
		Assert.notNull(session, "'session' must not be null");
		long now = System.currentTimeMillis();
		long lastTime = session.getLastAccessTime().getTime();
		return session.getTimeout() - (now - lastTime);
	}

	// --- Bind's. ---

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
		return KEY_ATTR_TTL_PREFIX + sessionKey;
	}

	/**
	 * Session attribute time to live model.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月23日
	 * @since
	 */
	public final static class SessionValueTTL implements Serializable {
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