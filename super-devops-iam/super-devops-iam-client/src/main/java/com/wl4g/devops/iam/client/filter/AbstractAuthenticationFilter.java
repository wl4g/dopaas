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
package com.wl4g.devops.iam.client.filter;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Assert;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_AUTHENTICATOR;
import static com.wl4g.devops.common.utils.web.WebUtils2.cleanURI;
import static com.wl4g.devops.common.utils.web.WebUtils2.getRFCBaseURI;
import static com.wl4g.devops.common.utils.web.WebUtils2.safeEncodeURL;
import static com.wl4g.devops.common.utils.web.WebUtils2.writeJson;
import static com.wl4g.devops.common.utils.web.WebUtils2.ResponseType.isJSONResponse;
import static com.wl4g.devops.common.web.RespBase.RetCode.OK;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_TICKET_C;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SERVICE_ROLE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SERVICE_ROLE_VALUE_IAMCLIENT;
import static com.wl4g.devops.iam.common.utils.AuthenticatingSecurityUtils.SESSION_STATUS_AUTHC;
import static com.wl4g.devops.iam.common.utils.AuthenticatingSecurityUtils.SESSION_STATUS_UNAUTHC;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bind;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getBindValue;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getSessionExpiredTime;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getSessionId;
import static com.wl4g.devops.tool.common.utils.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.apache.shiro.util.Assert.hasText;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.issueRedirect;
import static org.apache.shiro.web.util.WebUtils.toHttp;
import static org.springframework.util.Assert.notNull;

import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.exception.iam.InvalidGrantTicketException;
import com.wl4g.devops.common.exception.iam.UnauthorizedException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.client.authc.FastCasAuthenticationToken;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.configure.ClientSecurityConfigurer;
import com.wl4g.devops.iam.client.configure.ClientSecurityCoprocessor;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.tool.common.utils.lang.Exceptions;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.*;

/**
 * This filter validates the CAS service ticket to authenticate the user. It
 * must be configured on the URL recognized by the CAS server. For example, in
 * {@code shiro.ini}:
 * 
 * <pre>
 * [main]
 * casFilter = org.apache.shiro.cas.CasFilter
 * ...
 *
 * [urls]
 * /shiro-cas = casFilter
 * ...
 * </pre>
 * 
 * (example : http://host:port/mycontextpath/shiro-cas)
 *
 * @since 1.2
 */
public abstract class AbstractAuthenticationFilter<T extends AuthenticationToken> extends AuthenticatingFilter
		implements IamAuthenticationFilter {
	final public static String SAVE_GRANT_TICKET = AbstractAuthenticationFilter.class.getSimpleName() + ".GRANT_TICKET";

	/**
	 * What kind of URL request does not need to be remembered (i.e. using the
	 * default successUrl) when using the function of recording login successful
	 * callback URLs ? <br/>
	 * (For example, jump to IAM/login.html after executing logout)
	 */
	final public static String[] EXCLOUDE_SAVED_REDIRECT_URLS = { ("/" + LogoutAuthenticationFilter.NAME) };

	/**
	 * Remember last request URL.
	 */
	final public static String KEY_REMEMBER_URL = AbstractAuthenticationFilter.class.getSimpleName() + ".IamRememberUrl";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * IAM client configuration properties.
	 */
	final protected IamClientProperties config;

	/**
	 * Client security context handler.
	 */
	final protected ClientSecurityConfigurer configurer;

	/**
	 * Client security processor.
	 */
	final protected ClientSecurityCoprocessor coprocessor;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under
	 * Mutilate-Node.
	 */
	final protected EnhancedCache cache;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	public AbstractAuthenticationFilter(IamClientProperties config, ClientSecurityConfigurer context,
			ClientSecurityCoprocessor coprocessor, JedisCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(context, "'context' must not be null");
		Assert.notNull(coprocessor, "'interceptor' must not be null");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");
		this.config = config;
		this.configurer = context;
		this.coprocessor = coprocessor;
		this.cache = cacheManager.getEnhancedCache(CACHE_TICKET_C);
	}

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		return createAuthenticationToken(WebUtils.toHttp(request), WebUtils.toHttp(response));
	}

	protected abstract T createAuthenticationToken(HttpServletRequest request, HttpServletResponse response) throws Exception;

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		return super.executeLogin(request, response);
	}

	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response)
			throws Exception {
		FastCasAuthenticationToken ftoken = (FastCasAuthenticationToken) token;
		notNull(ftoken.getCredentials(), "token.credentials(grant ticket) must not be null");

		// Grant ticket
		String grantTicket = (String) ftoken.getCredentials();

		/*
		 * Binding session => grantTicket. Synchronize with
		 * FastCasAuthorizingRealm#doGetAuthenticationInfo
		 */
		bind(SAVE_GRANT_TICKET, grantTicket);
		if (log.isDebugEnabled()) {
			log.debug("Authentication bind grantTicket[{}], sessionId[{}]", grantTicket, getSessionId(subject));
		}

		/**
		 * Binding grantTicket => sessionId. Synchronize with
		 * IamClientSessionManager#getSessionId
		 */
		long expiredMs = getSessionExpiredTime();
		cache.put(new EnhancedKey(grantTicket, expiredMs), getSessionId(subject));

		// Determine success URL
		String successUrl = determineSuccessRedirectUrl(ftoken, subject, request, response);

		// JSON response
		if (isJSONResponse(toHttp(request))) {
			try {
				// Make logged response JSON.
				RespBase<String> loggedResp = makeLoggedResponse(request, subject, successUrl);

				// Callback custom success handling.
				coprocessor.postAuthenticatingSuccess(ftoken, subject, request, response, loggedResp.forMap());

				String logged = toJSONString(loggedResp);
				if (log.isInfoEnabled()) {
					log.info("Authenticated response to - {}", loggedResp);
				}
				writeJson(toHttp(response), logged);
			} catch (IOException e) {
				log.error("Logged response json error", e);
			}
		}
		// Redirection
		else {
			// Callback custom success handling.
			coprocessor.postAuthenticatingSuccess(ftoken, subject, request, response, null);

			if (log.isInfoEnabled()) {
				log.info("Authenticated redirect to - {}", successUrl);
			}
			issueRedirect(request, response, successUrl);
		}

		return false;
	}

	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
			ServletResponse response) {
		Throwable cause = Exceptions.getRootCause(ae);
		if (cause != null) {
			if (cause instanceof IamException) {
				log.error("Failed to caused by: {}", getMessage(cause));
			} else {
				log.error("Failed to authenticate.", cause);
			}
		}

		// Failure redirect URL
		String failRedirectUrl = makeFailureRedirectUrl(cause, toHttp(request));

		// Post handle of authenticate failure.
		coprocessor.postAuthenticatingFailure(token, ae, request, response);

		// Only if the error is not authenticated, can it be redirected to the
		// IAM server login page, otherwise the client will display the error
		// page directly (to prevent unlimited redirection).
		/** See:{@link AbstractBasedIamValidator#doGetRemoteValidation()} */
		if (isNull(cause) || (cause instanceof InvalidGrantTicketException)) {
			if (isJSONResponse(toHttp(request))) {
				try {
					String failMsg = makeFailedResponse(failRedirectUrl, cause);
					if (log.isInfoEnabled()) {
						log.info("Failed to invalid grantTicket response: {}", failMsg);
					}
					writeJson(toHttp(response), failMsg);
				} catch (IOException e) {
					log.error("Response json error", e);
				}
			} else { // Redirects the login page direct.
				try {
					issueRedirect(request, response, failRedirectUrl);
				} catch (IOException e) {
					log.error("Cannot redirect to failure url - {}", failRedirectUrl, e);
				}
			}
		}
		// For example, because of interface or permission errors, there is no
		// need to carry redirection URLs, because even redirection is the same
		// error, which may lead to unlimited redirection.
		else {
			try {
				String errmsg = String.format("%s, %s", bundle.getMessage("AbstractAuthenticationFilter.authc.failure"),
						getMessage(cause));
				/** See:{@link SmartSuperErrorsController#doHandleErrors()} */
				toHttp(response).sendError(SC_BAD_GATEWAY, errmsg);
			} catch (IOException e) {
				log.error("Failed to response error", e);
			}
		}

		// Suspend of execution. otherwise, dispatcherServlet will perform
		// redirection and eventually result in an exception:Cannot call
		// sendRedirect() after the response has been committed
		return false;
	}

	/**
	 * Make failure redirect URL
	 * 
	 * @param cause
	 * @param request
	 * @return
	 */
	protected String makeFailureRedirectUrl(Throwable cause, HttpServletRequest request) {
		// Redirect URL with callback.
		StringBuffer failRedirectUrl = new StringBuffer();

		if (cause instanceof UnauthorizedException) { // Unauthorized?
			failRedirectUrl.append(config.getUnauthorizedUri());
		} else { // UnauthenticatedException?
			// When the IAM server is authenticated successfully, the
			// callback redirects to the URL of the IAM client.
			String clientRedirectUrl = new StringBuffer(getRFCBaseURI(request, true)).append(URI_AUTHENTICATOR).toString();
			failRedirectUrl.append(getLoginUrl());
			failRedirectUrl.append("?").append(config.getParam().getApplication());
			failRedirectUrl.append("=").append(config.getServiceName());
			failRedirectUrl.append("&").append(config.getParam().getRedirectUrl());
			// Add custom parameters.
			String clientQueryStr = request.getQueryString();
			if (!isBlank(clientQueryStr)) {
				if (endsWith(clientRedirectUrl, "?")) {
					clientRedirectUrl += "&" + clientQueryStr;
				} else {
					clientRedirectUrl += "?" + clientQueryStr;
				}
			}
			failRedirectUrl.append("=").append(safeEncodeURL(clientRedirectUrl));
		}

		return failRedirectUrl.toString();
	}

	/**
	 * determine success redirect URL
	 * 
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 * @return
	 */
	protected String determineSuccessRedirectUrl(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) {
		// Priority obtain redirectURL from request.
		String successUrl = getRedirectUrl(request);
		if (isBlank(successUrl)) {
			// Secondary get remembered redirectURL.
			successUrl = getClearSavedRememberUrl(toHttp(request));
			if (isBlank(successUrl)) {
				// Fallback get the configured redirectURL as the default.
				successUrl = config.getSuccessUri();
			}
		}

		// Determine successUrl.
		successUrl = configurer.determineLoginSuccessUrl(successUrl, token, subject, request, response);
		Assert.notNull(successUrl, "'successUrl' must not be null");
		return cleanURI(successUrl); // Check & cleanup.
	}

	/**
	 * Get the URL from the redirectUrl from the authentication request(flexible
	 * API).
	 * 
	 * @return
	 */
	protected String getRedirectUrl(ServletRequest request) {
		return getCleanParam(request, config.getParam().getRedirectUrl());
	}

	/**
	 * Get remember last request URL
	 * 
	 * @param request
	 * @return
	 */
	protected String getClearSavedRememberUrl(HttpServletRequest request) {
		// Use remember redirection.
		if (config.isUseRememberRedirect()) {
			String rememberUrl = getBindValue(KEY_REMEMBER_URL, true);
			if (isNotBlank(rememberUrl)) {
				// URL excluding redirection remember
				if (!endsWithAny(rememberUrl, EXCLOUDE_SAVED_REDIRECT_URLS)) {
					return rememberUrl;
				}
			}
		}

		return null;
	}

	/**
	 * Make logged-in response message.
	 * 
	 * @see {@link com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter#makeLoggedResponse()}
	 * @param request
	 *            Servlet request
	 * @param redirectUri
	 *            login success redirect URL
	 * @return
	 */
	private RespBase<String> makeLoggedResponse(ServletRequest request, Subject subject, String redirectUri) {
		hasText(redirectUri, "'redirectUri' must not be null");

		// Make message
		RespBase<String> resp = RespBase.create(SESSION_STATUS_AUTHC);
		resp.setCode(OK).setMessage("Authentication successful");
		resp.forMap().put(config.getParam().getRedirectUrl(), redirectUri);

		// e.g. Used by mobile APP.
		resp.forMap().put(config.getParam().getSid(), String.valueOf(subject.getSession().getId()));
		resp.forMap().put(config.getParam().getApplication(), config.getServiceName());
		resp.forMap().put(KEY_SERVICE_ROLE, KEY_SERVICE_ROLE_VALUE_IAMCLIENT);
		return resp;
	}

	/**
	 * Make login failed response message.
	 * 
	 * @see {@link com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter#makeFailedResponse()}
	 * @param loginRedirectUrl
	 *            Login redirect URL
	 * @param err
	 *            Exception object
	 * @return
	 */
	private String makeFailedResponse(String loginRedirectUrl, Throwable err) {
		String errmsg = err != null ? err.getMessage() : "Authentication failure";
		// Make message
		RespBase<String> resp = RespBase.create(SESSION_STATUS_UNAUTHC);
		resp.setCode(RetCode.UNAUTHC).setMessage(errmsg);
		resp.forMap().put(config.getParam().getRedirectUrl(), loginRedirectUrl);
		resp.forMap().put(config.getParam().getApplication(), config.getServiceName());
		resp.forMap().put(KEY_SERVICE_ROLE, KEY_SERVICE_ROLE_VALUE_IAMCLIENT);
		return toJSONString(resp);
	}

	@Override
	public abstract String getName();

}