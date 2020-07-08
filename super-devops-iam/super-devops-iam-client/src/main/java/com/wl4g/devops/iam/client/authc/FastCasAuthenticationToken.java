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

import javax.validation.constraints.NotBlank;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;

/**
 * This class represents a token for a CAS authentication (service ticket + user
 * id + remember me).
 *
 * @since 1.2
 */
public class FastCasAuthenticationToken implements RememberMeAuthenticationToken, IamAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	/*
	 * the service ticket returned by the CAS server
	 */
	private String ticket;

	/*
	 * the user identifier
	 */
	private String userId;

	/**
	 * Authenticating host.
	 */
	private String host;

	/*
	 * is the user in a remember me mode ?
	 */
	private boolean isRememberMe = false;

	public FastCasAuthenticationToken(String ticket) {
		this(ticket, null);
	}

	public FastCasAuthenticationToken(String ticket, String host) {
		this.ticket = ticket;
		this.host = host;
	}

	@Override
	public Object getPrincipal() {
		return userId;
	}

	public void setPrincipal(String principal) {
		this.userId = principal;
	}

	@NotBlank
	@Override
	public Object getCredentials() {
		return ticket;
	}

	public void setCredentials(String credentials) {
		this.ticket = credentials;
	}

	@Override
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public boolean isRememberMe() {
		return isRememberMe;
	}

	public void setRememberMe(boolean isRememberMe) {
		this.isRememberMe = isRememberMe;
	}

}