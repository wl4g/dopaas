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
package com.wl4g.devops.iam.client.authc;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Signout authentication token.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年3月26日
 * @since
 */
public class LogoutAuthenticationToken implements AuthenticationToken {

	final private static long serialVersionUID = -7503506620220450148L;

	/**
	 * Forced signout?
	 */
	final private boolean forced;

	/**
	 * Principal currently exiting
	 */
	final private String principal;

	public LogoutAuthenticationToken(final boolean forced, final String principal) {
		this.forced = forced;
		/**
		 * @see {@link com.wl4g.devops.iam.client.filter.LogoutAuthenticationFilter#doCreateToken()}
		 */
		// hasTextOf(principal, "principal");
		this.principal = principal;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	public boolean isForced() {
		return forced;
	}

}