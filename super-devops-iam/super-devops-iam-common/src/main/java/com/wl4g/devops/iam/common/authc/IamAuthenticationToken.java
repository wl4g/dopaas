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
package com.wl4g.devops.iam.common.authc;

import static java.lang.Boolean.parseBoolean;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * IAM authentication token
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public interface IamAuthenticationToken extends HostAuthenticationToken {

	/**
	 * Get redirect information.
	 * 
	 * @return
	 */
	RedirectInfo getRedirectInfo();

	/**
	 * IAM client authentication redirection information.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月18日
	 * @since
	 */
	public static class RedirectInfo implements Serializable {
		private static final long serialVersionUID = -7747661274396168460L;

		/**
		 * Client authentication redirection application.
		 */
		private String fromAppName;

		/**
		 * Client authentication redirection URL.
		 */
		private String redirectUrl;

		/**
		 * Whether to enable backoff redirection address. For example, when the
		 * client's incoming redirecturl is not accessible, the default
		 * application's redirecturl will be used.</br>
		 * </br>
		 * Generally speaking, the client needs to be enabled when it is a web
		 * PC, but it does not need to be enabled when it is a non web client
		 * such as Android and iOS
		 * 
		 * @see {@link com.wl4g.devops.iam.realm.AbstractAuthorizingRealm#assertCredentialsMatch(AuthenticationToken, AuthenticationInfo)}
		 * @see {@link com.wl4g.devops.iam.handler.AuthenticationHandler#assertApplicationAccessAuthorized(String, String)}
		 */
		private boolean fallbackRedirect = true;

		public RedirectInfo() {
			this(null, null, true);
		}

		public RedirectInfo(String fromAppName, String redirectUrl) {
			this(fromAppName, redirectUrl, true);
		}

		public RedirectInfo(String fromAppName, String redirectUrl, boolean fallbackRedirect) {
			setFromAppName(fromAppName);
			setRedirectUrl(redirectUrl);
			setFallbackRedirect(fallbackRedirect);
		}

		public String getFromAppName() {
			return fromAppName;
		}

		public void setFromAppName(String fromAppName) {
			// hasText(fromAppName, "Application name must not be empty.");
			this.fromAppName = fromAppName;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			// hasText(redirectUrl, "Redirect url must not be empty.");
			this.redirectUrl = redirectUrl;
		}

		public boolean isFallbackRedirect() {
			return fallbackRedirect;
		}

		public void setFallbackRedirect(boolean fallbackRedirect) {
			this.fallbackRedirect = fallbackRedirect;
		}

		@Override
		public String toString() {
			return fromAppName + "@" + redirectUrl;
		}

		/**
		 * Build {@link RedirectInfo}
		 * 
		 * @param fromAppName
		 * @param redirectUrl
		 * @param fallbackRedirect
		 * @return
		 */
		public static RedirectInfo build(String fromAppName, String redirectUrl, String fallbackRedirect) {
			if (isBlank(fallbackRedirect)) {
				return new RedirectInfo(fromAppName, redirectUrl);
			} else {
				return new RedirectInfo(fromAppName, redirectUrl, parseBoolean(fallbackRedirect));
			}
		}

	}

}