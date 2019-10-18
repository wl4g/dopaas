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

import static org.apache.shiro.util.Assert.notNull;
import static org.springframework.util.Assert.hasText;

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
	final private String remoteHost;

	/**
	 * Redirection information.
	 */
	final private RedirectInfo redirectInfo;

	public AbstractIamAuthenticationToken() {
		this(null);
	}

	public AbstractIamAuthenticationToken(final String remoteHost) {
		this.remoteHost = remoteHost;
		this.redirectInfo = null;
	}

	public AbstractIamAuthenticationToken(final String remoteHost, final RedirectInfo redirectInfo) {
		hasText(remoteHost, "Remote client host must not be null.");
		notNull(redirectInfo, "Redirect info must not be null.");
		this.remoteHost = remoteHost;
		this.redirectInfo = redirectInfo;
	}

	@Override
	public String getHost() {
		return remoteHost;
	}

	public RedirectInfo getRedirectInfo() {
		return redirectInfo;
	}

}