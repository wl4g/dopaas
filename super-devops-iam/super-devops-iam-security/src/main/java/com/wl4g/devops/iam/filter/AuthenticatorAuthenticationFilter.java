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
package com.wl4g.devops.iam.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_AUTHENTICATOR;

import com.wl4g.devops.common.exception.iam.IllegalCallbackDomainException;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.AuthenticatorAuthenticationToken;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.utils.SessionBindings;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;

/**
 * IAM client authenticator authorization filter.<br/>
 * <br/>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月6日
 * @since
 */
@IamFilter
public class AuthenticatorAuthenticationFilter extends ROOTAuthenticationFilter {
	final public static String NAME = "authenticatorFilter";

	public AuthenticatorAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = getSubject(request, response);

		try {
			// Check authentication login request parameters
			this.authHandler.checkAuthenticateRequests(getFromAppName(request), getFromRedirectUrl(request));

			// Binding request parameters
			this.bindingRequestParameters(request, response);

		} catch (IllegalCallbackDomainException e) {
			log.warn("Using default callback URI. cause by: {}", Exceptions.getMessage(e));
		}

		/*
		 * If it is an authenticated state, execute the success logic directly,
		 * Exclude default success pages to prevent unlimited redirects.
		 */
		if (subject.isAuthenticated() && !matchRequest(getSuccessUrl(), request, response)) {
			try {
				// No need to continue
				return this.onLoginSuccess(createToken(request, response), subject, request, response);
			} catch (Exception e) {
				log.error("Logged-in auto redirect to other applications failed", e);
			}
		}

		// Pass processing
		return super.isAccessAllowed(request, response, mappedValue);
	}

	/**
	 * Bind the latest authentication request configuration, such as
	 * response_type, source application, etc.<br/>
	 * e.g:<br/>
	 * <b>Req1：</b>http://localhost:14040/devops-iam/view/login.html?service=devops-iam-example&redirect_url=http://localhost:14041/devops-iam-example/index.html<br/>
	 * <b>Resp1：</b>login.html<br/>
	 * <br/>
	 * <b>Req2：(Intercepted by
	 * rootFilter)</b>http://localhost:14040/devops-iam/favicon.ico<br/>
	 * <b>Resp2：</b>
	 * 302->http://localhost:14040/devops-iam/view/login.html?service=devops-iam-example&redirect_url=http://localhost:14041/devops-iam-example/index.html<br/>
	 * <br/>
	 * <b>Req3：</b>http://localhost:14040/devops-iam/view/login.html<br/>
	 * <br/>
	 * No parameters for the second request for login.html ??? This is the
	 * problem to be solved by this method.
	 * 
	 * @param request
	 * @param response
	 */
	private void bindingRequestParameters(ServletRequest request, ServletResponse response) {
		// Parameter names
		String sourceAppKey = config.getParam().getApplication();
		String responseTypeKey = config.getParam().getResponseType();
		String redirectUrlKey = config.getParam().getRedirectUrl();

		// Parameter values
		String sourceApp = WebUtils.getCleanParam(request, sourceAppKey);
		String respType = WebUtils.getCleanParam(request, responseTypeKey);
		String redirectUrl = WebUtils.getCleanParam(request, redirectUrlKey);

		// Overlay to save the latest parameters
		SessionBindings.bindKVParameters(KEY_REQ_AUTH_PARAMS,
				new Object[] { sourceAppKey, sourceApp, responseTypeKey, respType, redirectUrlKey, redirectUrl });
		if (log.isDebugEnabled()) {
			log.debug("Bind requests. sourceApp[{}], respType[{}], redirectUrl[{}]", sourceApp, respType, redirectUrl);
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