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
package com.wl4g.devops.iam.sns.qq;

import java.util.Map;

import org.apache.shiro.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.iam.config.properties.SnsProperties.QQSocialProperties;
import com.wl4g.devops.iam.sns.GenericOAuth2ApiBinding;
import com.wl4g.devops.iam.sns.qq.model.QQAccessToken;
import com.wl4g.devops.iam.sns.qq.model.QQOpenId;
import com.wl4g.devops.iam.sns.qq.model.QQUserInfo;

/**
 * QQ social networking services template
 *
 * @author wangl.sir
 * @version v1.0 2019年2月18日
 * @since
 */
public class QQOauth2Template extends GenericOAuth2ApiBinding<QQSocialProperties, QQAccessToken, QQOpenId, QQUserInfo> {

	final public static String PROVIDER_ID = "qq";
	/**
	 * Old: https://graph.qq.com/oauth2.0/authorize
	 */
	final public static String URI_AUTH_CODE = "https://graph.qq.com/oauth2.0/show";
	final public static String URI_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";
	final public static String URI_OPEN_ID = "https://graph.qq.com/oauth2.0/me";
	final public static String URI_USER_INFO = "https://graph.qq.com/user/get_user_info";

	public QQOauth2Template(QQSocialProperties config, RestTemplate restTemplate, CacheManager cacheManager) {
		super(config, restTemplate, cacheManager);
	}

	@Override
	public String providerId() {
		return PROVIDER_ID;
	}

	@Override
	protected void postGetAuthorizationCodeUrl(Map<String, String> parameters) {
		parameters.put("which", "login");
		parameters.put("display", "pc");
	}

	@Override
	protected void postGetAccessTokenUrl(Map<String, String> parameters) {

	}

	@Override
	protected void postGetOpenIdUrl(Map<String, String> parameters) {

	}

	@Override
	protected void postGetUserInfoUrl(Map<String, String> parameters) {
		parameters.remove(DEFAULT_PARAM_CLIENT_ID);
		parameters.put("oauth_consumer_key", config.getAppId());
	}

	@Override
	protected String scope() {
		return Scope.get_user_info.name();
	}

	@Override
	protected String getAuthorizationCodeUriEndpoint() {
		return URI_AUTH_CODE;
	}

	@Override
	protected String getAccessTokenUriEndpoint() {
		return URI_ACCESS_TOKEN;
	}

	@Override
	protected String getOpenIdUriEndpoint() {
		return URI_OPEN_ID;
	}

	@Override
	protected String getUserInfoUriEndpoint() {
		return URI_USER_INFO;
	}

	public static enum Scope {
		get_user_info(true), list_album, upload_pic, do_like;

		private boolean isDefault = false;

		private Scope() {
		}

		private Scope(boolean isDefault) {
			this.isDefault = isDefault;
		}

		public boolean isDefault() {
			return isDefault;
		}

	}

	public static enum Display {
		pc(true), mobile;

		private boolean isDefault = false;

		private Display() {
		}

		private Display(boolean isDefault) {
			this.isDefault = isDefault;
		}

		public boolean isDefault() {
			return isDefault;
		}

	}

}