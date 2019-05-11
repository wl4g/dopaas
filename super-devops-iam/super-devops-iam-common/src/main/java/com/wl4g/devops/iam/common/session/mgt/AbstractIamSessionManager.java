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
package com.wl4g.devops.iam.common.session.mgt;

import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_ID;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.URL_SESSION_ID_SOURCE;

import static java.lang.Boolean.TRUE;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.util.Assert;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.utils.lang.StringUtils2;
import com.wl4g.devops.common.utils.web.UserAgentUtils;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * Abstract custom WEB session management
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract class AbstractIamSessionManager<C extends AbstractIamProperties<? extends ParamProperties>>
		extends DefaultWebSessionManager {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Cache name
	 */
	final protected String cacheName;

	/**
	 * Abstract IAM properties configuration
	 */
	final protected C config;

	/**
	 * Enhanced cache manager.
	 */
	@Autowired
	protected EnhancedCacheManager cacheManager;

	/**
	 * IAM session DAO.
	 */
	@Autowired
	protected IamSessionDAO sessionDAO;

	public AbstractIamSessionManager(C config, String cacheName) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(cacheName, "'cacheName' must not be null");
		this.config = config;
		this.cacheName = cacheName;
	}

	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
		/*
		 * This SID session is used if the parameter contains the "SID"
		 * parameter. For example:
		 * http://localhost/project?__sid=xxx&__cookie=yes
		 */
		String sid = WebUtils.getCleanParam(request, config.getParam().getSid());

		// Using SID mode sessions
		if (checkSafeBlank(sid)) {
			if (log.isDebugEnabled()) {
				log.debug("Using SID session by [{}]", sid);
			}

			// Storage session token
			storageTokenIfNecessary(request, response, sid);

			// Set the current session state. (session sources and URL)
			request.setAttribute(REFERENCED_SESSION_ID_SOURCE, URL_SESSION_ID_SOURCE);
			request.setAttribute(REFERENCED_SESSION_ID, sid);
			request.setAttribute(REFERENCED_SESSION_ID_IS_VALID, TRUE);
			return sid;
		}

		// Using grantTicket mode sessions
		String grantTicket = WebUtils.getCleanParam(request, config.getParam().getGrantTicket());
		if (checkSafeBlank(grantTicket)) {
			/*
			 * Synchronize with
			 * See:iam.handler.DefaultAuthenticationHandler#loggedin()
			 */
			String sessionId = (String) cacheManager.getCache(cacheName).get(new EnhancedKey(grantTicket, String.class));
			if (log.isDebugEnabled()) {
				log.debug("Using grantTicket:[{}] sessionId:[{}]", grantTicket, sessionId);
			}
			if (checkSafeBlank(sessionId)) {
				return sessionId;
			} else {
				log.warn("Get sessionId of grantTicket:[{}] is blank", grantTicket);
				// Continue, trying to using cookie mode sessions
			}
		}

		// Using default cookie mode sessions
		Serializable sessionId = super.getSessionId(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Using default sessions by [{}]", sessionId);
		}
		return sessionId;
	}

	/**
	 * See:{@link com.wl4g.devops.iam.common.session.mgt.JedisIamSessionDAO#getActiveSessions()}
	 */
	@Deprecated
	@Override
	protected Collection<Session> getActiveSessions() {
		return null;
	}

	protected Session retrieveSession(SessionKey sessionKey) {
		try {
			return super.retrieveSession(sessionKey);
		} catch (UnknownSessionException e) {
			// Failure to obtain SESSION does not throw an exception
			return null;
		}
	}

	public Date getStartTimestamp(SessionKey key) {
		try {
			return super.getStartTimestamp(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return null;
		}
	}

	public Date getLastAccessTime(SessionKey key) {
		try {
			return super.getLastAccessTime(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return null;
		}
	}

	public long getTimeout(SessionKey key) {
		try {
			return super.getTimeout(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return 0;
		}
	}

	public void setTimeout(SessionKey key, long maxIdleTimeInMillis) {
		try {
			super.setTimeout(key, maxIdleTimeInMillis);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
		}
	}

	public void touch(SessionKey key) {
		try {
			super.touch(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
		}
	}

	public String getHost(SessionKey key) {
		try {
			return super.getHost(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return null;
		}
	}

	public Collection<Object> getAttributeKeys(SessionKey key) {
		try {
			return super.getAttributeKeys(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return null;
		}
	}

	public Object getAttribute(SessionKey sessionKey, Object attributeKey) {
		try {
			return super.getAttribute(sessionKey, attributeKey);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return null;
		}
	}

	public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) {
		try {
			super.setAttribute(sessionKey, attributeKey, value);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
		}
	}

	public Object removeAttribute(SessionKey sessionKey, Object attributeKey) {
		try {
			return super.removeAttribute(sessionKey, attributeKey);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
			return null;
		}
	}

	public void checkValid(SessionKey key) {
		try {
			super.checkValid(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
		}
	}

	@Override
	protected Session doCreateSession(SessionContext context) {
		try {
			return super.doCreateSession(context);
		} catch (IllegalStateException e) {
			// Ignore exceptions
			return null;
		}
	}

	@Override
	protected Session newSessionInstance(SessionContext context) {
		Session session = super.newSessionInstance(context);
		session.setTimeout(getGlobalSessionTimeout());
		return session;
	}

	@Override
	public Session start(SessionContext context) {
		try {
			return super.start(context);
		} catch (NullPointerException e) {
			SimpleSession session = new SimpleSession();
			session.setId(0);
			return session;
		}
	}

	@Override
	protected void onStart(Session session, SessionContext context) {
		if (!WebUtils.isHttp(context)) {
			throw new IllegalStateException(String.format("IAM currently only supports HTTP protocol family!"));
		}

		HttpServletRequest request = WebUtils.getHttpRequest(context);
		HttpServletResponse response = WebUtils.getHttpResponse(context);
		if (isSessionIdCookieEnabled()) {
			if (StringUtils2.isEmpty(session.getId())) {
				throw new IllegalArgumentException("sessionId cannot be null when persisting for subsequent requests.");
			}

			// Storage session token
			storageTokenIfNecessary(request, response, session.getId().toString());

		} else if (log.isDebugEnabled()) {
			log.debug("Session ID cookie is disabled.  No cookie has been set for new session with id {}", session.getId());
		}

		request.removeAttribute(REFERENCED_SESSION_ID_SOURCE);
		request.setAttribute(REFERENCED_SESSION_IS_NEW, TRUE);
	}

	public void stop(SessionKey key) {
		try {
			super.stop(key);
		} catch (InvalidSessionException e) {
			// Ignore exceptions
		}
	}

	/**
	 * Check whether the parameters are null in a safe way.<br/>
	 * safeCheckText("null") == false<br/>
	 * safeCheckText("NULL") == false<br/>
	 * safeCheckText(null) == false<br/>
	 * safeCheckText("") == false<br/>
	 * safeCheckText(" ") == false<br/>
	 * safeCheckText("12345") == true<br/>
	 * safeCheckText(" 12345 ") == true<br/>
	 * 
	 * @param str
	 * @return
	 */
	protected boolean checkSafeBlank(String str) {
		return StringUtils.hasText(str) && !str.equalsIgnoreCase("NULL");
	}

	/**
	 * Storage session token(cookie)
	 * 
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	private void storageTokenIfNecessary(ServletRequest request, ServletResponse response, String sessionId) {
		boolean isBrowser = UserAgentUtils.isBrowser(WebUtils.toHttp(request));
		/*
		 * When a browser request or display specifies that cookies need to be
		 * saved.
		 */
		if (isBrowser || WebUtils.isTrue(request, config.getParam().getSidSaveCookie())) {
			Cookie cookie = new SimpleCookie(getSessionIdCookie());
			cookie.setValue(sessionId);
			cookie.saveTo(WebUtils.toHttp(request), WebUtils.toHttp(response));
			if (log.isTraceEnabled()) {
				log.trace("Set session ID cookie for session with id {}", sessionId);
			}
		} else {
			/*
			 * Addition customize security headers.
			 */
			WebUtils.toHttp(response).addHeader(getSessionIdCookie().getName(), sessionId);
		}

	}

}
