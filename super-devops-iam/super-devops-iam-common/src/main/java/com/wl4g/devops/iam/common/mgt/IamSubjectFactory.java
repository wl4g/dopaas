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
package com.wl4g.devops.iam.common.mgt;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_ACCESSTOKEN_SIGN_NAME;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasText;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.web.CookieUtils.getCookie;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.isMediaRequest;
import static com.wl4g.devops.iam.common.utils.IamAuthenticatingUtils.*;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.getPathWithinApplication;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.subject.WebSubjectContext;

import com.wl4g.devops.common.exception.iam.InvalidAccessTokenAuthenticationException;
import com.wl4g.devops.common.exception.iam.UnauthenticatedException;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.core.IamShiroFilterFactoryBean;

import static com.wl4g.devops.iam.common.filter.AbstractIamAuthenticationFilter.*;
import static com.wl4g.devops.iam.common.session.mgt.AbstractIamSessionManager.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.shiro.subject.support.DefaultSubjectContext.*;

/**
 * {@link org.apache.shiro.mgt.SubjectFactory Subject} implementation to be used
 * in CAS-enabled applications.
 *
 * @since 1.2
 */
public class IamSubjectFactory extends DefaultWebSubjectFactory {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link AbstractIamProperties}
	 */
	final protected AbstractIamProperties<? extends ParamProperties> config;

	/**
	 * {@link IamShiroFilterFactoryBean#getFilterChainDefinitionMap()}
	 */
	final protected IamShiroFilterFactoryBean iamFilterFactory;

	public IamSubjectFactory(AbstractIamProperties<? extends ParamProperties> config,
			IamShiroFilterFactoryBean iamFilterFactory) {
		notNullOf(config, "config");
		notNullOf(iamFilterFactory, "iamFilterFactory");
		this.config = config;
		this.iamFilterFactory = iamFilterFactory;
	}

	@Override
	public Subject createSubject(SubjectContext context) {
		// the authenticated flag is only set by the SecurityManager after a
		// successful authentication attempt.
		//
		// although the SecurityManager 'sees' the submission as a successful
		// authentication, in reality, the
		// login might have been just a CAS rememberMe login. If so, set the
		// authenticated flag appropriately:
		AuthenticationToken token = context.getAuthenticationToken();
		if (!isNull(token) && token instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken tk = (RememberMeAuthenticationToken) token;
			// set the authenticated flag of the context to true only if the
			// CAS subject is not in a remember me mode
			if (tk.isRememberMe()) {
				context.setAuthenticated(false);
			}
		}

		// Validation of enhanced session additional signature.
		if (isAssertRequestAccessTokens(context)) {
			try {
				assertRequestAccessTokenValidity(context);
			} catch (UnauthenticatedException e) {
				// #Forced sets notauthenticated
				context.setAuthenticated(false);
				context.getSession().setAttribute(AUTHENTICATED_SESSION_KEY, false);
				if (log.isDebugEnabled())
					log.debug("Invalid accesstoken", e);
				else
					log.warn("Invalid accesstoken. - {}", e.getMessage());
			}
		}

		return super.createSubject(context);
	}

	/**
	 * Gets accessToken from requests.
	 * 
	 * @param request
	 * @return
	 */
	protected String getRequestAccessToken(HttpServletRequest request) {
		String accessToken = getCleanParam(request, config.getParam().getAccessTokenName());
		accessToken = isBlank(accessToken) ? request.getHeader(config.getParam().getAccessTokenName()) : accessToken;
		accessToken = isBlank(accessToken) ? getCookie(request, config.getParam().getAccessTokenName()) : accessToken;
		return accessToken;
	}

	/**
	 * Is assertion request accessTokens validity.
	 * 
	 * @param context
	 * @return
	 */
	protected boolean isAssertRequestAccessTokens(SubjectContext context) {
		HttpServletRequest request = toHttp(((WebSubjectContext) context).resolveServletRequest());
		return config.getSession().isEnableAccessTokenValidity() && !isMediaRequest(request)
				&& !isInternalProtocolNonAccessTokenRequest(request);
	}

	/**
	 * Assertion request accessToken(signature) validity.
	 * 
	 * @param context
	 * @throws UnauthenticatedException
	 * @see {@link AbstractIamAuthenticationFilter#makeLoggedResponse}
	 */
	private final void assertRequestAccessTokenValidity(SubjectContext context) throws UnauthenticatedException {
		// Additional signature verification will only be performed on those
		// who have logged in successful.
		// e.g: Authentication requests or internal API requests does not
		// require signature verification.
		if (context.isAuthenticated() || isNull(context.getSession()))
			return;

		WebSubjectContext wsc = (WebSubjectContext) context;
		Session session = wsc.getSession();
		HttpServletRequest request = toHttp(wsc.resolveServletRequest());

		// Gets protocol configure info.
		String sessionId = valueOf(session.getId());
		String accessTokenSignKey = (String) session.getAttribute(KEY_ACCESSTOKEN_SIGN_NAME);

		// Gets request accessToken.
		final String accessToken = getRequestAccessToken(request);
		log.debug("Asserting accessToken, sessionId:{}, accessTokenSignKey: {}, accessToken: {}", sessionId, accessTokenSignKey,
				accessToken);

		// Requires authentication parameters verified.
		hasText(accessToken, UnauthenticatedException.class, "accessToken is required");
		hasText(sessionId, UnauthenticatedException.class, "sessionId is required");
		hasText(accessTokenSignKey, UnauthenticatedException.class, "No accessTokenSignKey"); // Shouldn't-here

		// Calculating accessToken(signature).
		final String validAccessToken = generateAccessToken(session, accessTokenSignKey);
		log.debug("Asserted accessToken of sessionId: {}, accessTokenSignKey: {}, validAccessToken: {}, accessToken: {}",
				sessionId, accessTokenSignKey, validAccessToken, accessToken);

		// Compare accessToken(signature)
		if (!accessToken.equals(validAccessToken)) {
			throw new InvalidAccessTokenAuthenticationException(
					format("Illegal authentication accessToken: %s, accessTokenSignKey: %s", accessToken, accessTokenSignKey));
		}

	}

	/**
	 * Check is iam internal non accessToken valid request. </br>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private final boolean isInternalProtocolNonAccessTokenRequest(ServletRequest request) {
		String requestPath = getPathWithinApplication(toHttp(request));

		/**
		 * Check is internal protocol {@link IamFilter} chain mappings?
		 */
		for (Entry<String, String> ent : iamFilterFactory.getFilterChainDefinitionMap().entrySet()) {
			String pattern = ent.getKey();
			String filterName = ent.getValue();
			if (!NAME_ROOT_FILTER.equals(filterName) && defaultNonAccessTokenMatcher.matchStart(pattern, requestPath)) {
				return true;
			}
		}

		/**
		 * Check is internal protocol ticket authenticating request?
		 */
		return isInternalTicketRequest(request);
	}

	/**
	 * Non accessToken matcher.
	 */
	final private static AntPathMatcher defaultNonAccessTokenMatcher = new AntPathMatcher();

}