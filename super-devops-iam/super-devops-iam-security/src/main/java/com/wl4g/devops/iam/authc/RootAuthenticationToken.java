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
package com.wl4g.devops.iam.authc;

import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken;

/**
 * Authenticator authentication token
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public class RootAuthenticationToken extends AbstractIamAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	public RootAuthenticationToken(final String remoteHost, final RedirectInfo redirectInfo) {
		super(remoteHost, redirectInfo);
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public String getHost() {
		return null;
	}

}