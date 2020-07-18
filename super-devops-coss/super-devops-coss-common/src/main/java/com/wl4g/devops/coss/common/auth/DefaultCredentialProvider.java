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
package com.wl4g.devops.coss.common.auth;

import com.wl4g.devops.coss.common.exception.InvalidCredentialsException;

/**
 * Default implementation of {@link CredentialsProvider}.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public class DefaultCredentialProvider implements CredentialsProvider {

	/**
	 * COSS credentials info
	 */
	private volatile Credentials creds;

	public DefaultCredentialProvider(Credentials creds) {
		setCredentials(creds);
	}

	public DefaultCredentialProvider(String accessKeyId, String secretAccessKey) {
		this(accessKeyId, secretAccessKey, null);
	}

	public DefaultCredentialProvider(String accessKeyId, String secretAccessKey, String securityToken) {
		checkCredentials(accessKeyId, secretAccessKey);
		setCredentials(new DefaultCredentials(accessKeyId, secretAccessKey, securityToken));
	}

	/**
	 * Sets credentials.
	 */
	public synchronized void setCredentials(Credentials creds) {
		if (creds == null) {
			throw new InvalidCredentialsException("creds should not be null.");
		}

		checkCredentials(creds.getAccessKeyId(), creds.getSecretAccessKey());
		this.creds = creds;
	}

	@Override
	public Credentials getCredentials() {
		if (this.creds == null) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		return this.creds;
	}

	/**
	 * Check credentials
	 * 
	 * @param accessKeyId
	 * @param secretAccessKey
	 */
	private static void checkCredentials(String accessKeyId, String secretAccessKey) {
		if (accessKeyId == null || accessKeyId.equals("")) {
			throw new InvalidCredentialsException("Access key id should not be null or empty.");
		}

		if (secretAccessKey == null || secretAccessKey.equals("")) {
			throw new InvalidCredentialsException("Secret access key should not be null or empty.");
		}
	}

}