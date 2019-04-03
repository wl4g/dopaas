package com.wl4g.devops.iam.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.StringUtils;
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
	public static <T> T getBindValue(String sessionKey, boolean unbind) {
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
	public static <T> T getBindValue(String sessionKey) {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		return (T) SecurityUtils.getSubject().getSession().getAttribute(sessionKey);
	}

	/**
	 * Extract key value parameters
	 * 
	 * @param sessionKey
	 * @param paramKey
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T extParameterValue(String sessionKey, String paramKey) {
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
	public static void bindKVParameters(String sessionKey, Object[] keyValues) {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		Assert.notEmpty(keyValues, "'keyValues' must not be null");
		Assert.isTrue(keyValues.length % 2 == 0, "Illegal 'keyValues' length");

		// Extract key values
		Map<Object, Object> paramster = new HashMap<>();
		for (int i = 0; i < keyValues.length - 1; i++) {
			if (i % 2 == 0) {
				Object key = keyValues[i];
				Object value = keyValues[i + 1];
				if (key != null && StringUtils.hasText(key.toString()) && value != null
						&& StringUtils.hasText(value.toString())) {
					paramster.put(key, value);
				}
			}
		}

		// Binding
		bind(sessionKey, paramster);
	}

	/**
	 * Bind value to session
	 * 
	 * @param sessionKey
	 * @param value
	 */
	public static void bind(String sessionKey, Object value) {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		SecurityUtils.getSubject().getSession().setAttribute(sessionKey, value);
	}

	/**
	 * UN-bind sessionKey of session
	 * 
	 * @param sessionKey
	 * @return
	 */
	public static boolean unbind(String sessionKey) {
		Assert.notNull(sessionKey, "'sessionKey' must not be null");
		return SecurityUtils.getSubject().getSession().removeAttribute(sessionKey) != null;
	}

}
