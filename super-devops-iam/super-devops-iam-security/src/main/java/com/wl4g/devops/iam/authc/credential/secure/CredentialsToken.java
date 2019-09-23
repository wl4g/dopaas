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
package com.wl4g.devops.iam.authc.credential.secure;

import javax.validation.constraints.NotBlank;

import org.springframework.util.Assert;

/**
 * Final credentials token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月6日
 * @since
 */
public final class CredentialsToken {

	/**
	 * Request authentication principal.
	 */
	@NotBlank
	final private String principal;

	/**
	 * Request authentication credentials.
	 */
	@NotBlank
	final private String credentials;

	/**
	 * Whether the tag has resolved the encrypted password passed from the front
	 * end.
	 */
	final private boolean resolved;

	public CredentialsToken(CredentialsToken token, boolean solved) {
		this(token.getPrincipal(), token.getCredentials(), solved);
	}

	public CredentialsToken(String principal, String credentials) {
		this(principal, credentials, false);
	}

	public CredentialsToken(String principal, String credentials, boolean solved) {
		Assert.hasText(principal, "Principal must not be empty");
		Assert.hasText(credentials, "Credentials must not be empty");
		this.principal = principal;
		this.credentials = credentials;
		this.resolved = solved;
	}

	final public String getPrincipal() {
		return principal;
	}

	final public String getCredentials() {
		return credentials;
	}

	final public boolean isResolved() {
		return resolved;
	}

	@Override
	public String toString() {
		return "CredentialsToken [principal=" + principal + ", credentials=" + credentials + ", resolved=" + resolved + "]";
	}

}