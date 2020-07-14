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
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
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
import com.wl4g.devops.common.utils.bean.BeanCopierUtils;
import com.wl4g.devops.iam.common.session.IamSession.RelationAttrKey;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfoWrapper;

/**
 * Session bind holder utility.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月21日
 * @since
 */
public abstract class IamSecurityHolder extends SecurityUtils {

	// --- Principal & session's. ---

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
		IamPrincipalInfoWrapper wrap = (IamPrincipalInfoWrapper) getSession()
				.getAttribute(new RelationAttrKey(KEY_AUTHC_ACCOUNT_INFO, IamPrincipalInfoWrapper.class));
		if (assertion) {
			isTrue((!isNull(wrap) && !isNull(wrap.getInfo())), UnauthenticatedException.class,
					"Iam subject is required! unauthenticated? or is @EnableIamServer/@EnableIamClient not enabled? Also note the call order!");
		}

		/**
		 * [MARK2]:</br>
		 * It is not recommended that external methods bind business attributes
		 * here. We recommend that external methods bind business attributes
		 * directly to the session, i.e: {@link #bind(Object, T)}
		 * 
		 * @see {@link com.wl4g.devops.iam.common.subject.SimplePrincipalInfo#setAttributes(Map)}#MARK1
		 */
		return BeanCopierUtils.clone(wrap.getInfo());
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
	 * Gets session remaining expire time
	 *
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSessionRemainingTime() {
		return getSessionRemainingTime(getSession());
	}

	/**
	 * Gets session remaining expire time
	 *
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSessionRemainingTime(Session session) {
		notNullOf(session, "session");
		long now = currentTimeMillis();
		Date startDate = session.getStartTimestamp();
		Date lastADate = session.getLastAccessTime();
		Long startTime = isNull(startDate) ? null : startDate.getTime();
		Long lastTime = isNull(lastADate) ? startTime : lastADate.getTime();
		notNull(lastTime, "Could't be here, session: '%s' startTime and lastTime is null!", session.getId());
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
		notNullOf(sessionKey, "sessionKey");
		notNullOf(target, "withInSessionTarget");
		try {
			Object sessionValue = getBindValue(sessionKey);
			if (isNull(sessionValue)) {
				return false;
			}
			if ((sessionValue instanceof String) && (target instanceof String)) { // String
				return String.valueOf(sessionValue).equalsIgnoreCase(String.valueOf(target));
			} else if ((sessionValue instanceof Enum) || (target instanceof Enum)) { // ENUM
				return (sessionValue == target || sessionValue.toString().equalsIgnoreCase(target.toString()));
			} else { // Other object
				return sessionValue == target;
			}
		} finally {
			if (unbind) {
				unbind(sessionKey);
			}
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
		return withIn(sessionKey, target, false);
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
	@SuppressWarnings("unchecked")
	public static <T> T getBindValue(Object sessionKey, boolean unbind) throws InvalidSessionException {
		notNullOf(sessionKey, "sessionKey");
		try {
			return (T) getSession().getAttribute(sessionKey);
		} finally {
			if (unbind) {
				unbind(sessionKey);
			}
		}
	}

	/**
	 * Gets bind of session value
	 *
	 * @param sessionKey
	 *            Keys to save and session
	 * @return
	 */
	public static <T> T getBindValue(Object sessionKey) throws InvalidSessionException {
		return getBindValue(sessionKey, false);
	}

	/**
	 * Bind value to session
	 *
	 * @param sessionKey
	 * @param value
	 */
	public static <T> T bind(Object sessionKey, T value) throws InvalidSessionException {
		notNullOf(sessionKey, "sessionKey");
		if (!isNull(value)) {
			getSession(true).setAttribute(sessionKey, value);
		}
		return value;
	}

	/**
	 * Unbind sessionKey of session
	 *
	 * @param sessionKey
	 * @return
	 */
	public static boolean unbind(Object sessionKey) throws InvalidSessionException {
		notNullOf(sessionKey, "sessionKey");
		return !isNull(getSession().removeAttribute(sessionKey));
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
	 * Bind key-values map to session
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
				if (!isNull(key) && isNotBlank(key.toString()) && !isNull(value) && isNotBlank(value.toString())) {
					parameters.put(key, value);
				}
			}
		}

		// Binding
		bind(sessionKey, parameters);
	}

}