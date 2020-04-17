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
package com.wl4g.devops.iam.common.session.mgt;

import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_ID;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID;
import static org.apache.shiro.web.servlet.ShiroHttpServletRequest.URL_SESSION_ID_SOURCE;
import static org.apache.shiro.web.util.WebUtils.getPathWithinApplication;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.isTrue;
import static org.apache.shiro.web.util.WebUtils.toHttp;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.tool.common.web.UserAgentUtils.isBrowser;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

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
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.configure.SecurityCoprocessor;
import com.wl4g.devops.iam.common.session.GrantCredentialsInfo;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.tool.common.lang.StringUtils2;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract custom IAM session management
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract class AbstractIamSessionManager<C extends AbstractIamProperties<? extends ParamProperties>>
		extends DefaultWebSessionManager {
	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link GrantCredentialsInfo} granting ticket cache.
	 */
	final protected IamCache ticketCache;

	/**
	 * Abstract IAM properties configuration
	 */
	final protected C config;

	/**
	 * Enhanced cache manager.
	 */
	final protected IamCacheManager cacheManager;

	/**
	 * IAM session DAO.
	 */
	@Autowired
	protected IamSessionDAO sessionDAO;

	/**
	 * Security processor.
	 */
	@Autowired
	protected SecurityCoprocessor coprocessor;

	public AbstractIamSessionManager(C config, IamCacheManager cacheManager, String ticketCache) {
		notNullOf(config, "IamProperties");
		notNullOf(cacheManager, "cacheManager");
		hasTextOf(ticketCache, "ticketCacheName");
		this.config = config;
		this.cacheManager = cacheManager;
		this.ticketCache = cacheManager.getIamCache(ticketCache);
	}

	@Override
	protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
		// ->bug: e.g. infinite redirection error will occur if
		// iam-example/index.html fails to get sid
		// if (isMediaRequest(toHttp(request))) { // Static file pass.
		// return null;
		// }

		// Using extra custom session.
		Serializable sessionId = coprocessor.preGetSessionId(toHttp(request), toHttp(response));
		if (checkSessionValidity(sessionId)) {
			log.debug("Use extra sid '{}'", sessionId);
			// Sets session states
			setSessionStates(request, response, sessionId);
			// Storage sessions
			saveSessionIdCookieIfNecessary(request, response, sessionId);
			return sessionId;
		}

		// Using protocol internal ticket session.
		if (isProtocolInternalRequest(request, response)) {
			String grantTicket = getCleanParam(request, config.getParam().getGrantTicket());
			if (checkSessionValidity(grantTicket)) {
				/**
				 * {@link CentralAuthenticationHandler#loggedin()}
				 */
				sessionId = (String) ticketCache.get(new CacheKey(grantTicket, String.class));
				log.debug("Use ticket sid: '{}', grantTicket: '{}'", sessionId, grantTicket);
				if (checkSessionValidity(sessionId))
					return sessionId;
				else
					log.warn("Cannot gets sid with grantTicket: '{}'", grantTicket);
			}
			return null;
		}

		// Using URLs session. e.g.
		// http://localhost/project?__sid=xxx&__cookie=yes
		sessionId = getCleanParam(request, config.getParam().getSid());
		if (checkSessionValidity(sessionId)) {
			log.debug("Use url sid '{}'", sessionId);
			// Sets session states
			setSessionStates(request, response, sessionId);
			// Storage sessions
			saveSessionIdCookieIfNecessary(request, response, sessionId);
			return sessionId;
		}

		// Using cookie in URLs session.(e.g. Android/iOS/WechatApplet)
		sessionId = getCleanParam(request, config.getCookie().getName());
		if (checkSessionValidity(sessionId)) {
			log.debug("Use url cookie sid '{}'", sessionId);
			// Sets session states
			setSessionStates(request, response, sessionId);
			// Storage sessions
			saveSessionIdCookieIfNecessary(request, response, sessionId);
			return sessionId;
		}

		// Using header session.(e.g. Android/iOS/WechatApplet)
		sessionId = toHttp(request).getHeader(config.getCookie().getName());
		if (checkSessionValidity(sessionId)) {
			log.debug("Use header cookie sid '{}'", sessionId);
			// Sets session states
			setSessionStates(request, response, sessionId);
			return sessionId;
		}

		// Using default cookie session.
		sessionId = super.getSessionId(request, response);
		log.debug("Use default cookie sid: '{}'", sessionId);
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
			return new IamSession(0);
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
			saveSessionIdCookieIfNecessary(request, response, session.getId().toString());
		} else {
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
	 * @param sid
	 * @return
	 */
	protected boolean checkSessionValidity(Serializable sid) {
		return !isNull(sid) && isNotBlank(sid.toString()) && !sid.toString().equalsIgnoreCase("NULL");
	}

	/**
	 * Storage session tokens to cookie.
	 *
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	private void saveSessionIdCookieIfNecessary(ServletRequest request, ServletResponse response, Serializable sessionId) {
		if (isNull(sessionId))
			return;

		// When a browser request or display specifies that cookies need to
		// saved.
		boolean isSaveCookie = isTrue(request, config.getParam().getSidSaveCookie());
		if (isSaveCookie || isBrowser(toHttp(request))) {
			// Sets session cookie.
			Cookie sid = new SimpleCookie(getSessionIdCookie());
			// sid.setValue(valueOf(sessionId)+"; SameSite=None; Secure=false");
			sid.setValue(valueOf(sessionId));
			sid.saveTo(toHttp(request), toHttp(response));
			log.trace("Sets sessionId to cookies of: {}", sessionId);
		} else {
			// Addition customize security headers.
			toHttp(response).addHeader(getSessionIdCookie().getName(), valueOf(sessionId));
		}

	}

	/**
	 * Sets the session state. (session sources and URL)
	 * 
	 * @param request
	 * @param response
	 * @param sessionId
	 */
	private void setSessionStates(ServletRequest request, ServletResponse response, Serializable sessionId) {
		request.setAttribute(REFERENCED_SESSION_ID, sessionId);
		request.setAttribute(REFERENCED_SESSION_ID_SOURCE, URL_SESSION_ID_SOURCE);
		request.setAttribute(REFERENCED_SESSION_ID_IS_VALID, TRUE);
	}

	/**
	 * Check is iam protocol internal request.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private boolean isProtocolInternalRequest(ServletRequest request, ServletResponse response) {
		String requestPath = getPathWithinApplication(toHttp(request));
		for (String pattern : defaultInternalPathPatterns) {
			if (defaultAntMatcher.matchStart(pattern, requestPath)) {
				return true;
			}
		}
		return false;
	}

	final private static AntPathMatcher defaultAntMatcher = new AntPathMatcher();
	final private static String[] defaultInternalPathPatterns = { (URI_C_BASE + "/**"), (URI_S_BASE + "/**") };

}