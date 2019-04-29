/*
 * Copyright 2015 the original author or authors.
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
package com.zrk.oauthclient.client;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;

import com.fasterxml.jackson.databind.JsonNode;
import com.zrk.oauthclient.api.WeiXinApi20;
import com.zrk.oauthclient.definition.WeiXinAttributesDefinition;
import com.zrk.oauthclient.profile.WeiXinProfile;
import com.zrk.oauthclient.service.WeiXinOAuth20ServiceImpl;

/**
 * 微信登录client
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:43:06
 */
public class WeiXinClient extends BaseOAuth20Client<WeiXinProfile> {

	private final static WeiXinAttributesDefinition WEIXIN_ATTRIBUTES = new WeiXinAttributesDefinition();

	public final static String DEFAULT_SCOPE = "snsapi_login";

	protected String scope = DEFAULT_SCOPE;

	public WeiXinClient() {
	}

	public WeiXinClient(final String key, final String secret) {
		setKey(key);
		setSecret(secret);
	}

	@Override
	protected WeiXinClient newClient() {
		final WeiXinClient newClient = new WeiXinClient();
		newClient.setScope(this.scope);
		return newClient;
	}

	@Override
	protected void internalInit(WebContext context) {
		super.internalInit(context);
		WeiXinApi20 api20 = new WeiXinApi20();
		if (StringUtils.isNotBlank(this.scope)) {
			this.service = new WeiXinOAuth20ServiceImpl(api20,
					new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, this.scope, null),
					this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort, true, true);
		} else {
			this.service = new WeiXinOAuth20ServiceImpl(api20,
					new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, null, null),
					this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort, true, true);
		}
	};

	// 认证被用户取消
	@Override
	protected boolean hasBeenCancelled(WebContext context) {
		return false;
	}

	// 获取用户信息的URL
	@Override
	protected String getProfileUrl(Token accessToken) {
		return "https://api.weixin.qq.com/sns/userinfo";
	}

	// 处理用户信息
	@Override
	protected WeiXinProfile extractUserProfile(String body) {
		final WeiXinProfile profile = new WeiXinProfile();
		logger.info("========= extractUserProfile Method  body:" + body);
		final JsonNode json = JsonHelper.getFirstNode(body);
		if (null != json) {
			for (final String attribute : WEIXIN_ATTRIBUTES.getPrincipalAttributes()) {
				Object obj = JsonHelper.get(json, attribute);
				if (obj != null)
					profile.addAttribute(attribute, obj.toString());
			}
		}
		return profile;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(final String scope) {
		this.scope = scope;
	}

}