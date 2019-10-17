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
package com.wl4g.devops.iam.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_AUTHENTICATOR;
import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.iam.common.utils.SessionBindings.bindKVParameters;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;

import com.wl4g.devops.common.exception.iam.IllegalCallbackDomainException;
import com.wl4g.devops.common.utils.web.WebUtils2.ResponseType;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.AuthenticatorAuthenticationToken;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;

/**
 * IAM client authenticator authorization filter.</br>
 * </br>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月6日
 * @since
 */
@IamFilter
public class AuthenticatorAuthenticationFilter extends ROOTAuthenticationFilter {
	final public static String NAME = "authenticatorFilter";

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);

		try {
			// Check authenticate request validity.
			authHandler.checkAuthenticateValidity(getFromAppName(request), getFromRedirectUrl(request));

			// Remember request parameters
			savedRequestParameters(request, response);
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

	/**
	 * Saved the latest authentication request configuration, such as
	 * response_type, source application, etc.</br>
	 * E.G.:</br>
	 * </br>
	 * 
	 * <b>Req1：</b>http://localhost:14040/devops-iam/view/login.html?service=devops-iam-example&redirect_url=http://localhost:14041/devops-iam-example/index.html</br>
	 * <b>Resp1：</b>login.html</br>
	 * </br>
	 * <b>Req2：(Intercepted by
	 * rootFilter)</b>http://localhost:14040/devops-iam/favicon.ico</br>
	 * <b>Resp2：</b>
	 * 302->http://localhost:14040/devops-iam/view/login.html?service=devops-iam-example&redirect_url=http://localhost:14041/devops-iam-example/index.html</br>
	 * </br>
	 * <b>Req3：</b>http://localhost:14040/devops-iam/view/login.html</br>
	 * </br>
	 * 
	 * No parameters for the second request for login.html ??? This is the
	 * problem to be solved by this method.
	 * 
	 * @param request
	 * @param response
	 */
	private void savedRequestParameters(ServletRequest request, ServletResponse response) {
		// Parameter names.
		String fromAppKey = config.getParam().getApplication();
		String respTypeKey = ResponseType.DEFAULT_RESPTYPE_NAME;
		String redirectUrlKey = config.getParam().getRedirectUrl();

		// Parameter values.
		String fromApp = getCleanParam(request, fromAppKey);
		String respType = getCleanParam(request, respTypeKey);
		String redirectUrl = getCleanParam(request, redirectUrlKey);

		// Safety encoding.
		redirectUrl = safeEncodeHierarchyRedirectUrl(redirectUrl);

		// Overlay to save the latest parameters
		bindKVParameters(KEY_REQ_AUTH_PARAMS, fromAppKey, fromApp, respTypeKey, respType, redirectUrlKey, redirectUrl);
		if (log.isDebugEnabled()) {
			log.debug("Binding for fromApp[{}], respType[{}], redirectUrl[{}]", fromApp, respType, redirectUrl);
		}
	}

	@Override
	protected IamAuthenticationToken postCreateToken(String remoteHost, String fromAppName, String redirectUrl,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new AuthenticatorAuthenticationToken(remoteHost, fromAppName, redirectUrl);
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