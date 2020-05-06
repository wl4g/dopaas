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

import org.springframework.util.Assert;

/**
 * A CSRF token that is used to protect against CSRF attacks.
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class DefaultXsrfToken implements XsrfToken {
	private static final long serialVersionUID = 9081452892797891148L;

	/**
	 * XSRF token value.
	 */
	final private String token;

	/**
	 * XSRF parameter name/
	 */
	final private String parameterName;

	/**
	 * XSRF header name.
	 */
	final private String headerName;

	/**
	 * Creates a new instance
	 * 
	 * @param headerName
	 *            the HTTP header name to use
	 * @param parameterName
	 *            the HTTP parameter name to use
	 * @param token
	 *            the value of the token (i.e. expected value of the HTTP
	 *            parameter of parametername).
	 */
	public DefaultXsrfToken(String headerName, String parameterName, String token) {
		Assert.hasLength(headerName, "headerName cannot be null or empty");
		Assert.hasLength(parameterName, "parameterName cannot be null or empty");
		Assert.hasLength(token, "token cannot be null or empty");
		this.headerName = headerName;
		this.parameterName = parameterName;
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.web.csrf.CsrfToken#getHeaderName()
	 */
	public String getHeaderName() {
		return this.headerName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.web.csrf.CsrfToken#getParameterName()
	 */
	public String getParameterName() {
		return this.parameterName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.web.csrf.CsrfToken#getToken()
	 */
	public String getToken() {
		return this.token;
	}
}
