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

import static com.wl4g.devops.iam.common.session.NoOpSession.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_AUTHC_ACCOUNT_INFO;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;

import com.wl4g.devops.iam.common.session.NoOpSession;
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
	 * Gets current authenticated principal name.
	 *
	 * @return
	 */
	public static String getPrincipal() {
		return getPrincipal(true);
	}

	/**
	 * Gets current authenticated principal name.
	 *
	 * @param assertion
	 * @return
	 */
	public static String getPrincipal(boolean assertion) {
		Object principal = getSubject().getPrincipal();
		if (assertion) {
			notNull(principal,
					"The authentication subject is empty. The unauthenticated? or is @EnableIamServer/@EnableIamClient not enabled? Also note the call order!");
		}
		return (String) principal;
	}

	/**
	 * Gets current authenticate principal {@link IamPrincipalInfo}
	 * 
	 * @return
	 * @see {@link com.wl4g.devops.iam.realm.AbstractIamAuthorizingRealm#doGetAuthenticationInfo(AuthenticationToken)}
	 */
	public static IamPrincipalInfo getPrincipalInfo() {
		return getPrincipalInfo(true);
	}

	/**
	 * Gets current authenticate principal {@link IamPrincipalInfo}
	 * 
	 * @param assertion
	 * @return
	 * @see {@link com.wl4g.devops.iam.realm.AbstractIamAuthorizingRealm#doGetAuthenticationInfo(AuthenticationToken)}
	 */
	public static IamPrincipalInfo getPrincipalInfo(boolean assertion) {
		IamPrincipalInfo info = getBindValue(KEY_AUTHC_ACCOUNT_INFO);
		if (assertion) {
			notNull(info, UnauthenticatedException.class,
					"Authentication subject empty. unauthenticated? or is @EnableIamServer/@EnableIamClient not enabled? Also note the call order!");
		}
		return info;
	}

	/**
	 * Check if the current topic session is available. </br>
	 * Note: it only checks whether the current session exists. If it exists, it
	 * does not check the validity of the session
	 * 
	 * @throws UnknownSessionException
	 */
	public static void checkSession() throws UnknownSessionException {
		notNull(getSubject().getSession(false), UnknownSessionException.class, "No session in current subject.");
	}

	/**
	 * Gets current session, If there is no session currently,
	 * {@link NoOpSession#DefaultNoOpSession} will be returned
	 *
	 * @param create
	 * @return
	 */
	public static Session getSession() {
		Session session = getSession(false);
		return isNull(session) ? DefaultNoOpSession : session;
	}

	/**
	 * Gets current session
	 *
	 * @param create
	 * @return
	 */
	public static Session getSession(boolean create) {
		return getSubject().getSession(create);
	}

	/**
	 * Gets session-id
	 *
	 * @return
	 */
	public static Serializable getSessionId() {
		return getSessionId(getSubject());
	}

	/**
	 * Gets session-id
	 *
	 * @param subject
	 * @return
	 */
	public static Serializable getSessionId(Subject subject) {
		return getSessionId(subject.getSession(false));
	}

	/**
	 * Gets session-id
	 *
	 * @param session
	 * @return
	 */
	public static Serializable getSessionId(Session session) {
		return !isNull(session) ? session.getId() : null;
	}

	/**
	 * Gets session expire time
	 *
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSessionExpiredTime() {
		return getSessionExpiredTime(getSession());
	}

	/**
	 * Gets session expire time
	 *
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSessionExpiredTime(Session session) {
		notNullOf(session, "session");
		long now = currentTimeMillis();
		Date lastATime = session.getLastAccessTime();
		long lastTime = isNull(lastATime) ? 0 : lastATime.getTime();
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
		notNullOf(sessionKey, "sessionKey");
		notNullOf(target, "withInSessionTarget");

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
	 * Gets bind of session value
	 *
	 * @param sessionKey
	 *            Keys to save and session
	 * @param unbind
	 *            Whether to UN-bundle
	 * @return
	 */
	public static <T> T getBindValue(String sessionKey, @Deprecated boolean unbind) throws InvalidSessionException {
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
		hasTextOf(sessionKey, "sessionKey");
		// Gets bind value.
		T value = (T) getSession().getAttribute(sessionKey);
		// Gets value TTL.
		SessionValueTTL ttl = (SessionValueTTL) getSession().getAttribute(getExpireKey(sessionKey));
		if (!isNull(ttl)) { // Need to check expiration
			if ((currentTimeMillis() - ttl.getCreateTime()) >= ttl.getExpireMs()) { // Expired?
				unbind(sessionKey); // Remove
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
		notNullOf(sessionKey, "sessionKey");
		notNullOf(paramKey, "paramKey");

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
		hasTextOf(sessionKey, "sessionKey");
		notEmptyOf(keyValues, "keyValues");
		isTrueOf(keyValues.length % 2 == 0, "Illegal 'keyValues' length");

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
		isTrue(expireMs > 0, "Expire time must be greater than 0");
		bind(sessionKey, value);
		if (!isNull(value)) {
			bind(getExpireKey(sessionKey), new SessionValueTTL(expireMs));
		}
		return value;
	}

	/**
	 * Bind value to session
	 *
	 * @param sessionKey
	 * @param value
	 */
	public static <T> T bind(String sessionKey, T value) throws InvalidSessionException {
		hasTextOf(sessionKey, "sessionKey");
		if (!isNull(value)) {
			getSession().setAttribute(sessionKey, value);
		}
		return value;
	}

	/**
	 * UN-bind sessionKey of session
	 *
	 * @param sessionKey
	 * @return
	 */
	public static boolean unbind(String sessionKey) throws InvalidSessionException {
		hasTextOf(sessionKey, "sessionKey");
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
		hasTextOf(sessionKey, "sessionKey");
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
			stateOf(createTime != null, "'createTime' must not be null.");
			stateOf(expireMs != null, "'expireMs' must not be null.");
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