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
package com.wl4g.devops.iam.common.security.xsrf.repository;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasLength;

/**
 * A default XSRF token that is used to protect against CSRF attacks.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月7日
 */
public final class DefaultXsrfToken implements XsrfToken {
	private static final long serialVersionUID = 9081452892797891148L;

	/**
	 * XSRF token value.
	 */
	final private String xsrfToken;

	/**
	 * XSRF parameter name/
	 */
	final private String xsrfParamName;

	/**
	 * XSRF header name.
	 */
	final private String xsrfHeaderName;

	/**
	 * Creates a new instance
	 * 
	 * @param xsrfHeaderName
	 *            the HTTP header name to use
	 * @param xsrfParamName
	 *            the HTTP parameter name to use
	 * @param token
	 *            the value of the token (i.e. expected value of the HTTP
	 *            parameter of parametername).
	 */
	public DefaultXsrfToken(String xsrfHeaderName, String xsrfParamName, String xsrfToken) {
		hasLength(xsrfHeaderName, "xsrfHeaderName cannot be null or empty");
		hasLength(xsrfParamName, "xsrfParamName cannot be null or empty");
		hasLength(xsrfToken, "xsrfToken cannot be null or empty");
		this.xsrfHeaderName = xsrfHeaderName;
		this.xsrfParamName = xsrfParamName;
		this.xsrfToken = xsrfToken;
	}

	@Override
	public String getXsrfHeaderName() {
		return this.xsrfHeaderName;
	}

	@Override
	public String getXsrfParamName() {
		return this.xsrfParamName;
	}

	@Override
	public String getXsrfToken() {
		return this.xsrfToken;
	}
}