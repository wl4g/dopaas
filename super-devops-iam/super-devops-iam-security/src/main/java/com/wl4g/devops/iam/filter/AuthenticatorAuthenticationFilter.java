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
package com.wl4g.devops.iam.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_AUTHENTICATOR;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getPrincipal;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;

import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.iam.IllegalCallbackDomainException;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.AuthenticatorAuthenticationToken;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;

/**
 * IAM client authenticator authorization filter.</br>
 * </br>
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月6日
 * @since
 */
@Beta
@IamFilter
public class AuthenticatorAuthenticationFilter extends ROOTAuthenticationFilter {
	final public static String NAME = "authenticatorFilter";

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);

		try {
			// Check authenticate redirect URL validity.
			RedirectInfo redirect = getRedirectInfo(request);
			authHandler.checkAuthenticateRedirectValidity(redirect.getFromAppName(), redirect.getRedirectUrl());
		} catch (IllegalCallbackDomainException e) {
			log.warn("Using default redirect URI. caused by: {}", getRootCausesString(e));
		}

		/*
		 * If it is an authenticated state, execute the success logic directly,
		 * Exclude default success pages to prevent unlimited redirects.
		 */
		if (subject.isAuthenticated() && !matchRequest(getSuccessUrl(), request, response)) {
			try {
				return onLoginSuccess(createToken(request, response), subject, request, response);
			} catch (Exception e) {
				log.error("Failed to redirect successUrl with authenticated.", e);
			}
		}

		// Pass processing
		return super.isAccessAllowed(request, response, mappedValue);
	}

	@Override
	protected IamAuthenticationToken doCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		/**
		 * [Note1]: The client wants to verify the credentials and redirect the
		 * request. That is, if the current session has a value (has TCT), you
		 * can get the current principal directly from the
		 * 
		 * @see {@link com.wl4g.devops.iam.common.utils.IamSecurityHolder#getPrincipal(boolean)}
		 */
		String principal = getPrincipal(false);

		/**
		 * [Note2]: The request is redirected from the client logout, i.e. the
		 * session is null(no TCT), To get the principal before logout, you can
		 * only get it from the client request parameter.
		 * 
		 * @see {@link org.apache.shiro.subject.Subject#getSession()}
		 */
		principal = !isBlank(principal) ? principal : getCleanParam(request, config.getParam().getPrincipalName());
		return new AuthenticatorAuthenticationToken(principal, remoteHost, redirectInfo);
	}

	@Override
	protected RedirectInfo determineFailureRedirect(RedirectInfo redirect, IamAuthenticationToken token,
			AuthenticationException ae, ServletRequest request, ServletResponse response) {
		/**
		 * Fix Infinite redirection, {@link AuthenticatorAuthenticationFilter}
		 * may redirect to loginUrl, if failRedirectUrl equals getLoginUrl, it
		 * will happen infinite redirection.
		 **/
		redirect.setRedirectUrl(getLoginUrl());
		return super.determineFailureRedirect(redirect, token, ae, request, response);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return URI_AUTHENTICATOR;
	}

}