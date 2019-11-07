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

import com.wl4g.devops.iam.config.properties.SnsProperties.WechatSocialProperties;
import com.wl4g.devops.iam.sns.wechat.model.WxAccessToken;
import com.wl4g.devops.iam.sns.wechat.model.WxBasedOpenId;
import com.wl4g.devops.iam.sns.wechat.model.WxUserInfo;

/**
 * WeChat social networking services template
 *
 * @author wangl.sir
 * @version v1.0 2019年2月18日
 * @since
 */
public class WechatOauth2Template
		extends BasedWechatOauth2Template<WechatSocialProperties, WxAccessToken, WxBasedOpenId, WxUserInfo> {

	final public static String PROVIDER_ID = "wechat";

	/**
	 * QRcode getting authorizing code API URL
	 */
	final public static String URI_AUTH_CODE = "https://open.weixin.qq.com/connect/qrconnect#wechat_redirect";

	/**
	 * WeChat open platform getting user information API URL
	 */
	final public static String URI_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

	public WechatOauth2Template(WechatSocialProperties config, RestTemplate restTemplate, CacheManager cacheManager) {
		super(config, restTemplate, cacheManager);
	}

	@Override
	public String providerId() {
		return PROVIDER_ID;
	}

	@Override
	public void postGetAuthorizationCodeUrl(Map<String, String> parameters) {
		super.postGetAuthorizationCodeUrl(parameters);

		// Wechat has added a new page style that supports custom authorization
		parameters.put("href", config.getHref());
	}

	@Override
	public String scope() {
		return Scope.snsapi_login.name();
	}

	@Override
	public String getAuthorizationCodeUriEndpoint() {
		return URI_AUTH_CODE;
	}

	@Override
	public String getUserInfoUriEndpoint() {
		return URI_USER_INFO;
	}

}