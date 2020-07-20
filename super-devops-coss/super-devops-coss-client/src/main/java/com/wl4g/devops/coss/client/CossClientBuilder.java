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
package com.wl4g.devops.coss.client;

import com.wl4g.devops.coss.client.config.ClientCossConfiguration;
import com.wl4g.devops.coss.common.auth.CredentialsProvider;
import com.wl4g.devops.coss.common.auth.DefaultCredentialProvider;

/**
 * {@link CossClientBuilder}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public abstract class CossClientBuilder {

	/**
	 * Building {@link CossClient}
	 * 
	 * @param endpoint
	 * @param accessKeyId
	 * @param secretAccessKey
	 * @return
	 */
	public CossClient build(String endpoint, String accessKeyId, String secretAccessKey) {
		return new CossClientImpl(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey), getClientConfiguration());
	}

	/**
	 * Building {@link CossClient}
	 * 
	 * @param endpoint
	 * @param accessKeyId
	 * @param secretAccessKey
	 * @param securityToken
	 * @return
	 */
	public CossClient build(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
		return new CossClientImpl(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken),
				getClientConfiguration());
	}

	/**
	 * Building {@link CossClient}
	 * 
	 * @param endpoint
	 * @param accessKeyId
	 * @param secretAccessKey
	 * @param config
	 * @return
	 */
	public CossClient build(String endpoint, String accessKeyId, String secretAccessKey, ClientCossConfiguration config) {
		return new CossClientImpl(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey),
				ensureClientConfiguration(config));
	}

	/**
	 * Building {@link CossClient}
	 * 
	 * @param endpoint
	 * @param accessKeyId
	 * @param secretAccessKey
	 * @param securityToken
	 * @param config
	 * @return
	 */
	public CossClient build(String endpoint, String accessKeyId, String secretAccessKey, String securityToken,
			ClientCossConfiguration config) {
		return new CossClientImpl(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken),
				ensureClientConfiguration(config));
	}

	/**
	 * Building {@link CossClient}
	 * 
	 * @param endpoint
	 * @param credsProvider
	 * @return
	 */
	public CossClient build(String endpoint, CredentialsProvider credsProvider) {
		return new CossClientImpl(endpoint, credsProvider, getClientConfiguration());
	}

	/**
	 * Building {@link CossClient}
	 * 
	 * @param endpoint
	 * @param credsProvider
	 * @param config
	 * @return
	 */
	public CossClient build(String endpoint, CredentialsProvider credsProvider, ClientCossConfiguration config) {
		return new CossClientImpl(endpoint, credsProvider, ensureClientConfiguration(config));
	}

	/**
	 * Gets coss client configuration
	 * 
	 * @return
	 */
	private static ClientCossConfiguration getClientConfiguration() {
		return new ClientCossConfiguration();
	}

	/**
	 * Gets coss client configuration
	 * 
	 * @param config
	 * @return
	 */
	private static ClientCossConfiguration ensureClientConfiguration(ClientCossConfiguration config) {
		if (config == null) {
			config = new ClientCossConfiguration();
		}
		return config;
	}

	/**
	 * Gets default credential provider
	 * 
	 * @param accessKeyId
	 * @param secretAccessKey
	 * @return
	 */
	private static DefaultCredentialProvider getDefaultCredentialProvider(String accessKeyId, String secretAccessKey) {
		return new DefaultCredentialProvider(accessKeyId, secretAccessKey);
	}

	/**
	 * Gets default credential provider
	 * 
	 * @param accessKeyId
	 * @param secretAccessKey
	 * @param securityToken
	 * @return
	 */
	private static DefaultCredentialProvider getDefaultCredentialProvider(String accessKeyId, String secretAccessKey,
			String securityToken) {
		return new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken);
	}

}