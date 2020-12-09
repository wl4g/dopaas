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
package com.wl4g.devops.doc.plugin.swagger.export.enhance.config;

import java.util.Collections;
import java.util.Map;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;

public class SwaggerFlows {

	/**
	 * Configuration for the OAuth Implicit flow
	 */
	@Parameter
	private Entry implicit;

	/**
	 * Configuration for the OAuth Resource Owner Password flow
	 */
	@Parameter
	private Entry password;

	/**
	 * Configuration for the OAuth Client Credentials flow. Previously called
	 * application in OpenAPI 2.0.
	 */
	@Parameter
	private Entry clientCredentials;

	/**
	 * Configuration for the OAuth Authorization Code flow. Previously called
	 * accessCode in OpenAPI 2.0.
	 */
	@Parameter
	private Entry authorizationCode;

	@Parameter
	private Map<String, Object> extensions = Collections.emptyMap();

	public OAuthFlows createOAuthFlowsModel() {
		OAuthFlows flows = new OAuthFlows();
		if (implicit != null) {
			flows.setImplicit(implicit.toOAuthFlowModel());
		}
		if (password != null) {
			flows.setPassword(password.toOAuthFlowModel());
		}
		if (clientCredentials != null) {
			flows.setClientCredentials(clientCredentials.toOAuthFlowModel());
		}
		if (authorizationCode != null) {
			flows.setAuthorizationCode(authorizationCode.toOAuthFlowModel());
		}
		flows.setExtensions(extensions);
		return flows;
	}

	public static class Entry {
		/**
		 * For implicit/authorizationCode flows: REQUIRED. The authorization URL
		 * to be used for this flow. This MUST be in the form of a URL.
		 */
		@Parameter
		private String authorizationUrl;

		/**
		 * For password/clientCredentials/AuthorizationCode flows: REQUIRED. The
		 * token URL to be used for this flow. This MUST be in the form of a
		 * URL.
		 */
		@Parameter
		private String tokenUrl;

		/**
		 * The URL to be used for obtaining refresh tokens. This MUST be in the
		 * form of a URL.
		 */
		@Parameter
		private String refreshUrl;

		/**
		 * REQUIRED. The available scopes for the OAuth2 security scheme. A map
		 * between the scope name and a short description for it.
		 */
		@Parameter(required = true)
		private Map<String, String> scopes = Collections.emptyMap();

		@Parameter
		private Map<String, Object> extensions = Collections.emptyMap();

		public OAuthFlow toOAuthFlowModel() {
			OAuthFlow flow = new OAuthFlow();

			flow.setAuthorizationUrl(authorizationUrl);
			flow.setTokenUrl(tokenUrl);
			flow.setRefreshUrl(refreshUrl);
			if (scopes != null && !scopes.isEmpty()) {
				Scopes ss = new Scopes();
				scopes.entrySet().forEach(s -> ss.addString(s.getKey(), s.getValue()));
				flow.setScopes(ss);
			}

			if (extensions != null && !extensions.isEmpty()) {
				flow.setExtensions(extensions);
			}
			return flow;
		}
	}
}