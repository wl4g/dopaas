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
package com.wl4g.devops.iam.common.authc;

/**
 * Abstract IAM authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public abstract class AbstractIamAuthenticationToken implements IamAuthenticationToken {

	private static final long serialVersionUID = 5483061935073949894L;

	/**
	 * Remote client host address
	 */
	final private String host;

	/**
	 * Source application name
	 */
	final private String fromAppName;

	/**
	 * Source application callback URL
	 */
	final private String redirectUrl;

	public AbstractIamAuthenticationToken() {
		this(null);
	}

	public AbstractIamAuthenticationToken(String remoteHost) {
		this.host = remoteHost;
		this.fromAppName = null;
		this.redirectUrl = null;
	}

	public AbstractIamAuthenticationToken(String remoteHost, String fromAppName, String redirectUrl) {
		this.host = remoteHost;
		this.fromAppName = fromAppName;
		this.redirectUrl = redirectUrl;
	}

	@Override
	public String getHost() {
		return host;
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