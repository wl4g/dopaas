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

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.web.WebUtils2.getFullRequestURL;
import static com.wl4g.devops.tool.common.web.WebUtils2.isMediaRequest;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.annotations.Beta;
import com.wl4g.devops.iam.authc.RootAuthenticationToken;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;

/**
 * Root path authentication routing filter.<br/>
 * <br/>
 * Filter's execution chain's
 * {@link org.apache.catalina.core.StandardWrapperValve.invoke}
 * {@link org.apache.catalina.core.ApplicationFilterChain#internalDoFilter}
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月6日
 * @since
 */
@Beta
@IamFilter
public class ROOTAuthenticationFilter extends AbstractIamAuthenticationFilter<IamAuthenticationToken> {
	final public static String NAME = "rootFilter";

	@SuppressWarnings("unchecked")
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		log.debug("Request url: '{}'", () -> getFullRequestURL(toHttp(request)));

		// Logged-in or login page request passed
		return (getSubject(request, response).isAuthenticated() || isLoginRequest(request, response)
				|| isMediaRequest((HttpServletRequest) request));
	}

	/**
	 * Cannot call executeLogin() because it's not a login submission request,
	 * just redirect to the login page, indicates that you currently need to
	 * login.
	 * {@link org.apache.shiro.web.filter.authc.AuthenticatingFilter#executeLogin}
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (!getSubject(request, response).isAuthenticated()) {
			// When an unregistered or requested URL is a protected resource,
			// the login failure logic (that is, redirect to the login page) is
			// executed.
			return super.onLoginFailure(createToken(request, response), null, request, response);
		}
		return false;
	}

	@Override
	protected IamAuthenticationToken doCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new RootAuthenticationToken(remoteHost, redirectInfo);
	}

	@Override
	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		return matchRequest(getLoginUrl(), request, response);
	}

	/**
	 * To solve the problem of mismatching request URLs, such as: only pages
	 * embedded in containers are matched, that is, the page path at the
	 * beginning of "/". For example, if the login URI is
	 * 'http://passport.mydomain.com/devops-iam/login.html', it will not be
	 * matched successfully, but it is indeed embedded in its own page, which
	 * needs to continue matching (to be compatible).
	 *
	 * @param defineUrl
	 *            Configuration defined URLs
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean matchRequest(String defineUrl, ServletRequest request, ServletResponse response) {
		hasTextOf(defineUrl, "defineUrl");
		// Relative path and complete path matching
		return (pathsMatch(defineUrl, request) || defineUrl.equals(getFullRequestURL(toHttp(request), false)));
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return "/**";
	}

}