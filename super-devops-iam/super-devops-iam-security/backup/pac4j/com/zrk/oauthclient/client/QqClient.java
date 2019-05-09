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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zrk.oauthclient.api.QqApi20;
import com.zrk.oauthclient.definition.QqAttributesDefinition;
import com.zrk.oauthclient.profile.QqProfile;

/**
 * qq登录client
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:42:35
 */
public class QqClient extends BaseOAuth20Client<QqProfile> {

	private final static QqAttributesDefinition QQ_ATTRIBUTES = new QqAttributesDefinition();

	private static Pattern callbackPattern = Pattern.compile("callback\\((.*)\\)");

	private Token accessToken = null;

	public final static String DEFAULT_SCOPE = "get_user_info";

	protected String scope = DEFAULT_SCOPE;

	public QqClient() {
	}

	public QqClient(final String key, final String secret) {
		setKey(key);
		setSecret(secret);
	}

	@Override
	protected QqClient newClient() {
		final QqClient newClient = new QqClient();
		newClient.setScope(this.scope);
		return newClient;
	}

	@Override
	protected void internalInit(WebContext context) {
		super.internalInit(context);
		QqApi20 api20 = new QqApi20();
		if (StringUtils.isNotBlank(this.scope)) {
			this.service = new ProxyOAuth20ServiceImpl(api20,
					new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, this.scope, null),
					this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort, true, true);
		} else {
			this.service = new ProxyOAuth20ServiceImpl(api20,
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
		this.accessToken = accessToken;
		return "https://graph.qq.com/oauth2.0/me";
	}

	// 处理用户信息
	@Override
	protected QqProfile extractUserProfile(String body) {
		Matcher matcher = callbackPattern.matcher(body);
		String opengId = null;
		String clientId = null;
		if (matcher.find()) {
			final JsonNode json = JsonHelper.getFirstNode(matcher.group(1));
			opengId = (String) JsonHelper.get(json, "openid");
			clientId = (String) JsonHelper.get(json, "client_id");
		}
		if (opengId == null || clientId == null)
			throw new OAuthException("接口返回数据miss openid: " + body);

		String get_user_info_url = "https://graph.qq.com/user/get_user_info?openid=%s&oauth_token=%s&oauth_consumer_key=%s";
		final ProxyOAuthRequest request = createProxyRequest(
				String.format(get_user_info_url, opengId, this.accessToken.getToken(), clientId));
		final Response response = request.send();
		final int code = response.getCode();
		body = response.getBody();
		if (code != 200) {
			throw new HttpCommunicationException(code, body);
		}

		final QqProfile profile = new QqProfile();
		logger.info("========= extractUserProfile Method  body:" + body);
		final JsonNode json = JsonHelper.getFirstNode(body);
		if (null != json) {
			profile.addAttribute(QqAttributesDefinition.OPEN_ID, opengId);
			for (final String attribute : QQ_ATTRIBUTES.getPrincipalAttributes()) {
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