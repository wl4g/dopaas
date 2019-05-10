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
package com.wl4g.devops.iam.common.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.util.Assert;
import org.apache.shiro.web.util.WebUtils;

import com.wl4g.devops.common.kit.access.IPAccessControl;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * Interactive authentication processing filter for internal and application
 * services
 * 
 * {@link org.apache.shiro.web.filter.authz.HostFilter}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
public abstract class AbstractWhiteListInternalAuthenticationFilter extends BasedInternalAuthenticationFilter {

	private IPAccessControl control;

	public AbstractWhiteListInternalAuthenticationFilter(IPAccessControl control,
			AbstractIamProperties<? extends ParamProperties> config) {
		super(config);
		Assert.notNull(control, "'control' must not be null");
		this.control = control;
	}

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		final String remoteIp = getHttpRemoteIp(request);
		if (log.isDebugEnabled()) {
			log.debug("Access request remoteIp: {}", remoteIp);
		}

		final boolean allowed = control.isPermitted(remoteIp);
		if (!allowed) {
			log.warn("Illegal access request remoteIp: {}", remoteIp);
		}

		return allowed;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		return false;
	}

	/**
	 * Get HTTP remote client IP
	 * 
	 * @param request
	 * @return
	 */
	protected String getHttpRemoteIp(ServletRequest request) {
		return WebUtils2.getHttpRemoteAddr(WebUtils.toHttp(request));
	}

}