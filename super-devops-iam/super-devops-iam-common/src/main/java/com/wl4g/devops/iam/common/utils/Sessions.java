package com.wl4g.devops.iam.common.utils;

import java.io.Serializable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.util.Assert;

/**
 * SHIRO session utility
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月13日
 * @since
 */
public abstract class Sessions {

	/**
	 * Getting current session
	 * 
	 * @param create
	 * @return
	 */
	public static Session getSeesion() {
		return SecurityUtils.getSubject().getSession(true);
	}

	/**
	 * Getting current session
	 * 
	 * @param create
	 * @return
	 */
	public static Session getSeesion(boolean create) {
		return SecurityUtils.getSubject().getSession(create);
	}

	/**
	 * Getting session-id
	 * 
	 * @return
	 */
	public static Serializable getSessionId() {
		return getSessionId(SecurityUtils.getSubject());
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
	public static long getSeesionExpiredTime() {
		return getSeesionExpiredTime(getSeesion());
	}

	/**
	 * Get session expire time
	 * 
	 * @param session
	 *            Shiro session
	 * @return Current remaining expired milliseconds of the session
	 */
	public static long getSeesionExpiredTime(Session session) {
		Assert.notNull(session, "'session' must not be null");
		long now = System.currentTimeMillis();
		long lastTime = session.getLastAccessTime().getTime();
		return session.getTimeout() - (now - lastTime);
	}

}
