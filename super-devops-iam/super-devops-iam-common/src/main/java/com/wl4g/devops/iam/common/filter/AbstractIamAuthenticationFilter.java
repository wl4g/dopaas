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
package com.wl4g.devops.iam.common.filter;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_LOGIN_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_LOGIN_PERMITS;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.tool.common.web.UserAgentUtils.isBrowser;
import static com.wl4g.devops.tool.common.web.WebUtils2.getRFCBaseURI;
import static com.wl4g.devops.tool.common.web.WebUtils2.toQueryParams;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract iam authentication filter.
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
public abstract class AbstractIamAuthenticationFilter<C extends AbstractIamProperties<? extends ParamProperties>>
		extends AuthenticatingFilter implements IamAuthenticationFilter {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Iam properties.
	 */
	@Autowired
	protected C config;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	/**
	 * Gets legal authentication customization parameters.
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected Map getLegalCustomParameters(ServletRequest request) {
		Map<String, String> customParams = toQueryParams(toHttp(request).getQueryString());
		// Cleaning not matches custom parameters.
		Iterator<Entry<String, String>> it = customParams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> param = it.next();
			if (!config.getParam().getCustomeParams().contains(param.getKey())) {
				it.remove();
			}
		}
		return customParams;
	}

	/**
	 * Puts principal authorization(roles/permissions) to cookies.
	 * 
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 */
	protected Map<String, Object> putAuthorizationInfoCookieIfNecessary(AuthenticationToken token, ServletRequest request,
			ServletResponse response) {
		Map<String, Object> authzInfo = new HashMap<>();

		// Sets authorizes permits info.
		String permitUrl = getRFCBaseURI(toHttp(request), true) + URI_S_LOGIN_BASE + "/" + URI_S_LOGIN_PERMITS;
		authzInfo.put(config.getParam().getAuthzPermitsName(), permitUrl);
		if (isBrowser(toHttp(request))) {
			Cookie c = new SimpleCookie(config.getCookie());
			c.setName(config.getParam().getAuthzPermitsName());
			c.setValue(permitUrl);
			c.setMaxAge(60);
			c.saveTo(toHttp(request), toHttp(response));
		}

		return authzInfo;
	}

	/**
	 * Gets authentication filter name.
	 */
	public abstract String getName();

	/**
	 * Root filter.
	 */
	final public static String NAME_ROOT_FILTER = "rootFilter";

}