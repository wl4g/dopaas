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
package com.wl4g.devops.iam.client.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;

import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.client.authc.FastCasAuthenticationToken;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.context.ClientSecurityContext;
import com.wl4g.devops.iam.client.context.ClientSecurityCoprocessor;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;

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
@IamFilter
public class ROOTAuthenticationFilter extends AbstractAuthenticationFilter<FastCasAuthenticationToken> {
	final public static String NAME = "rootFilter";

	public ROOTAuthenticationFilter(IamClientProperties config, ClientSecurityContext context,
			ClientSecurityCoprocessor coprocessor, JedisCacheManager cacheManager) {
		super(config, context, coprocessor, cacheManager);
	}

	/**
	 * The token created for this authentication is a CasToken containing the
	 * CAS service ticket received on the CAS service url (on which the filter
	 * must be configured).
	 * 
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the outgoing response
	 * @throws Exception
	 *             if there is an error processing the request.
	 */
	@Override
	protected FastCasAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String ticket = WebUtils.getCleanParam(request, config.getParam().getGrantTicket());
		return new FastCasAuthenticationToken(ticket);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (log.isDebugEnabled()) {
			String url = WebUtils2.getFullRequestURL(WebUtils.toHttp(request));
			log.debug("Root request: {}", url);
		}

		/*
		 * See:xx.client.filter.AbstractAuthenticationFilter#getRememberUrl()
		 */
		if (WebUtils.toHttp(request).getMethod().equalsIgnoreCase(GET_METHOD)) {
			WebUtils.saveRequest(request);
		}

		return SecurityUtils.getSubject().isAuthenticated();
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