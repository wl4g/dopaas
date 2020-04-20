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
package com.wl4g.devops.iam.sns.wechat;

import java.util.Map;

import org.apache.shiro.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.iam.config.properties.SnsProperties.AbstractSocialProperties;
import com.wl4g.devops.iam.sns.GenericOAuth2ApiBinding;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;
import com.wl4g.devops.iam.sns.wechat.model.WxBasedAccessToken;
import com.wl4g.devops.iam.sns.wechat.model.WxBasedOpenId;

/**
 * Based abstract WeChat social oauth2 template
 *
 * @author wangl.sir
 * @version v1.0 2019年2月18日
 * @since
 */
public abstract class BasedWechatOauth2Template<C extends AbstractSocialProperties, T extends WxBasedAccessToken, O extends WxBasedOpenId, U extends Oauth2UserProfile>
		extends GenericOAuth2ApiBinding<C, T, O, U> {

	final public static String URI_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

	public BasedWechatOauth2Template(C config, RestTemplate restTemplate, CacheManager cacheManager) {
		super(config, restTemplate, cacheManager);
	}

	/**
	 * Returns when the access token is acquired
	 */
	@SuppressWarnings("unchecked")
	@Override
	public WxBasedOpenId getUserOpenId(WxBasedAccessToken accessToken) {
		return new WxBasedOpenId(accessToken.getOpenId(), accessToken.getUnionId());
	}

	@Override
	public void postGetAuthorizationCodeUrl(Map<String, String> parameters) {
		// Wechat defines the name of the oauth2 (client_id) parameter as appid
		parameters.remove(DEFAULT_PARAM_CLIENT_ID);
		parameters.put("appid", config.getAppId());
	}

	@Override
	public void postGetAccessTokenUrl(Map<String, String> parameters) {
		parameters.remove(DEFAULT_PARAM_CLIENT_ID);
		parameters.put("appid", config.getAppId());

		parameters.remove(DEFAULT_PARAM_CLIENT_SECRET);
		parameters.put("secret", config.getAppSecret());

		parameters.remove(DEFAULT_PARAM_REDIRECT_URI);
	}

	@Override
	public void postGetOpenIdUrl(Map<String, String> parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void postGetUserInfoUrl(Map<String, String> parameters) {
		parameters.remove(DEFAULT_PARAM_CLIENT_ID);
	}

	@Override
	public String getAccessTokenUriEndpoint() {
		return URI_ACCESS_TOKEN;
	}

	@Override
	protected String getOpenIdUriEndpoint() {
		throw new UnsupportedOperationException();
	}

	/**
	 * https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
	 *
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月6日
	 * @since
	 */
	public static enum Scope {
		snsapi_login(true), snsapi_userinfo, snsapi_base;

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

}