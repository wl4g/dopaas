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
package com.wl4g.devops.iam.authc;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.common.authc.ClientRef;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;

/**
 * General (Username/Password) authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public class GeneralAuthenticationToken extends UsernamePasswordToken
		implements IamAuthenticationToken, CaptchaAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	/**
	 * Source application name
	 */
	final private String fromAppName;

	/**
	 * Source application callback URL
	 */
	final private String redirectUrl;

	final private String captcha;

	final private ClientRef clientRef;

	public GeneralAuthenticationToken(final String remoteHost, final String fromAppName, final String redirectUrl,
			final String username, final String password, String clientRef, final String captcha) {
		super(username, password, remoteHost);
		this.fromAppName = fromAppName;
		this.redirectUrl = redirectUrl;
		this.clientRef = ClientRef.of(clientRef);
		this.captcha = captcha;
	}

	@Override
	public Object getCredentials() {
		Object credentials = super.getCredentials();
		Assert.notNull(credentials, "Credentials must not be null");
		return new String((char[]) credentials);
	}

	@Override
	public String getCaptcha() {
		return captcha;
	}

	public ClientRef getClientRef() {
		return clientRef;
	}

	@Override
	public String getFromAppName() {
		return fromAppName;
	}

	@Override
	public String getRedirectUrl() {
		return redirectUrl;
	}

}