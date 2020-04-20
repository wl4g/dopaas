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

import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bind;
import static com.wl4g.devops.tool.common.web.UserAgentUtils.isBrowser;
import static com.wl4g.devops.tool.common.web.WebUtils2.getAvaliableRequestRememberUrl;
import static com.wl4g.devops.tool.common.web.WebUtils2.getFullRequestURL;
import static com.wl4g.devops.tool.common.web.WebUtils2.isXHRRequest;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;

import com.wl4g.devops.iam.client.authc.FastCasAuthenticationToken;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.configure.ClientSecurityConfigurer;
import com.wl4g.devops.iam.client.configure.ClientSecurityCoprocessor;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.cache.JedisIamCacheManager;

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

	public ROOTAuthenticationFilter(IamClientProperties config, ClientSecurityConfigurer context,
			ClientSecurityCoprocessor coprocessor, JedisIamCacheManager cacheManager) {
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
	protected FastCasAuthenticationToken doCreateToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String ticket = getCleanParam(request, config.getParam().getGrantTicket());
		return new FastCasAuthenticationToken(ticket);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		log.debug("ROOT requestURL: {}", getFullRequestURL(toHttp(request)));

		/**
		 * See:{@link com.wl4g.devops.iam.client.filter.AbstractAuthenticationFilter#getClearSavedRememberUrl()}
		 */
		if (config.isUseRememberRedirect() && isBrowser(toHttp(request)) && !isXHRRequest(toHttp(request))) {
			// Remember URL.
			String rememberUrl = getAvaliableRequestRememberUrl(toHttp(request));
			if (isNotBlank(rememberUrl)) {
				bind(KEY_REMEMBER_URL, rememberUrl);
			} else {
				log.warn("Can't get remember via requestURL: {}", getFullRequestURL(toHttp(request)));
			}
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