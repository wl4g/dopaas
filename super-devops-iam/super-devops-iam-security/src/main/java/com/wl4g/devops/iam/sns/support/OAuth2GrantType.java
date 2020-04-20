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
package com.wl4g.devops.iam.sns.support;

/**
 * OAuth2 supports two types of authorization flow, typically referred to as
 * "Client-side" and "Server-side". Use of implicit grant is discouraged unless
 * there is no other option available.
 *
 * @author Roy Clarkson
 */
public enum OAuth2GrantType {

	/**
	 * Authorization code mode (i.e. first login to get code, then token)
	 */
	AUTHORIZATION_CODE(true),

	/**
	 * Simplified mode (passing token in redirect_uri Hash; Auth client running
	 * in browsers, such as JS, Flash)
	 */
	IMPLICIT_GRANT,

	/**
	 * Password mode (Pass user name, password, get token directly)
	 */
	PASSWORD,

	/**
	 * Client mode (no user, the user registers with the client, and the client
	 * obtains resources from the server in its own name).<br/>
	 * <br/>
	 * Note: WeChat public platform, the parameter
	 * <font color=red>grant_type=client_credential</font> in get access_token
	 * of the basic API (instead of <font color=red>client_credentials)</font>.
	 * See:<a href=
	 * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183">https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183</a>
	 */
	CLIENT_CREDENTIALS,

	/**
	 * Refresh access_token
	 */
	REFRESH_TOKEN;

	private boolean isDefault = false;

	private OAuth2GrantType() {
	}

	private OAuth2GrantType(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public static OAuth2GrantType getDefault() {
		OAuth2GrantType defaultGrantType = null;
		for (OAuth2GrantType gt : values()) {
			if (gt.isDefault()) {
				if (defaultGrantType != null) {
					throw new IllegalStateException("There can only be one default value");
				}
				defaultGrantType = gt;
			}
		}
		return defaultGrantType;
	}

}