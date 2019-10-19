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

import java.io.Serializable;

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

		final public static RedirectInfo EMPTY = new RedirectInfo(null, null);

		/**
		 * Client authentication redirection application.
		 */
		private String fromAppName;

		/**
		 * Client authentication redirection URL.
		 */
		private String redirectUrl;

		public RedirectInfo(String fromAppName, String redirectUrl) {
			// hasText(fromAppName, "Application name must not be empty.");
			// hasText(redirectUrl, "Redirect url must not be empty.");
			this.fromAppName = fromAppName;
			this.redirectUrl = redirectUrl;
		}

		public String getFromAppName() {
			return fromAppName;
		}

		public void setFromAppName(String fromAppName) {
			this.fromAppName = fromAppName;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

		@Override
		public String toString() {
			return fromAppName + "@" + redirectUrl;
		}

	}

}