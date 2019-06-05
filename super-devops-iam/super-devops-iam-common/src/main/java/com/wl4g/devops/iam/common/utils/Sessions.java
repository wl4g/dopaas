/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
	public static Session getSession() {
		return SecurityUtils.getSubject().getSession(true);
	}

	/**
	 * Getting current session
	 * 
	 * @param create
	 * @return
	 */
	public static Session getSession(boolean create) {
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

}