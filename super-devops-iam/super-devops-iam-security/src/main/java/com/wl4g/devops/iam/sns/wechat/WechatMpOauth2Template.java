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

import com.wl4g.devops.iam.config.properties.SnsProperties.WechatMpSocialProperties;
import com.wl4g.devops.iam.sns.wechat.model.WxAccessToken;
import com.wl4g.devops.iam.sns.wechat.model.WxBasedOpenId;
import com.wl4g.devops.iam.sns.wechat.model.WxUserInfo;

/**
 * WeChat platform social networking services template
 *
 * @author wangl.sir
 * @version v1.0 2019年2月18日
 * @since
 */
public class WechatMpOauth2Template
		extends BasedWechatOauth2Template<WechatMpSocialProperties, WxAccessToken, WxBasedOpenId, WxUserInfo> {

	final public static String PROVIDER_ID = "wechatmp";

	/**
	 * QRcode getting authorizing code API URL
	 */
	final public static String URI_AUTH_CODE = "https://open.weixin.qq.com/connect/oauth2/authorize#wechat_redirect";

	/**
	 * WeChat based-platform API: https://api.weixin.qq.com/cgi-bin/token
	 */
	final public static String URI_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

	/**
	 * WeChat based-platform API: https://api.weixin.qq.com/cgi-bin/user/info
	 */
	final public static String URI_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

	public WechatMpOauth2Template(WechatMpSocialProperties config, RestTemplate restTemplate, CacheManager cacheManager) {
		super(config, restTemplate, cacheManager);
	}

	@Override
	public String providerId() {
		return PROVIDER_ID;
	}

	@Override
	public void postGetUserInfoUrl(Map<String, String> parameters) {
		super.postGetUserInfoUrl(parameters);
		parameters.put("lang", "zh_CN");
	}

	/**
	 * Authorized from WeChat public platform, only when
	 * 'socpe=snsapi_userinfo', the parameter of menu connection of public
	 * number, obtains 'unionid' value of access token interface, otherwise,
	 * when 'scope=snsapi_base' only returns 'openid'.
	 */
	@Override
	public String scope() {
		return Scope.snsapi_userinfo.name();
	}

	@Override
	public String getAuthorizationCodeUriEndpoint() {
		return URI_AUTH_CODE;
	}

	@Override
	public String getAccessTokenUriEndpoint() {
		return URI_ACCESS_TOKEN;
	}

	@Override
	public String getUserInfoUriEndpoint() {
		return URI_USER_INFO;
	}

}