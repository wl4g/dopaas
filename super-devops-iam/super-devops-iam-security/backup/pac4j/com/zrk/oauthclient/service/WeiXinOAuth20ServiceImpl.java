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
package com.zrk.oauthclient.service;

import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 微信的接口与OAuth2.0的默认定义有不同 将请求token的接口的client_secret改为secret
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:44:34
 */
public class WeiXinOAuth20ServiceImpl extends ProxyOAuth20ServiceImpl {

	private static final String SECRET = "secret";
	private static final String APPID = "appid";

	public WeiXinOAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost,
			int proxyPort) {
		super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort);
	}

	public WeiXinOAuth20ServiceImpl(DefaultApi20 api, OAuthConfig config, int connectTimeout, int readTimeout, String proxyHost,
			int proxyPort, final boolean getParameter, final boolean addGrantType) {
		super(api, config, connectTimeout, readTimeout, proxyHost, proxyPort, getParameter, addGrantType);
	}

	@Override
	public void signRequest(Token accessToken, OAuthRequest request) {
		request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
		final JsonNode json = JsonHelper.getFirstNode(accessToken.getRawResponse());
		String openid = (String) JsonHelper.get(json, "openid");
		request.addQuerystringParameter("openid", openid);
	}

	@Override
	public Token getAccessToken(Token requestToken, Verifier verifier) {
		final OAuthRequest request = new ProxyOAuthRequest(this.api.getAccessTokenVerb(), this.api.getAccessTokenEndpoint(),
				this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort);
		if (this.getParameter) {
			request.addQuerystringParameter(APPID, this.config.getApiKey());
			request.addQuerystringParameter(SECRET, this.config.getApiSecret());
			request.addQuerystringParameter(OAuthConstants.CODE, verifier.getValue());
			request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
			if (this.config.hasScope()) {
				request.addQuerystringParameter(OAuthConstants.SCOPE, this.config.getScope());
			}
			if (this.addGrantType) {
				request.addQuerystringParameter("grant_type", "authorization_code");
			}
		} else {
			request.addBodyParameter(APPID, this.config.getApiKey());
			request.addBodyParameter(SECRET, this.config.getApiSecret());
			request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
			request.addBodyParameter(OAuthConstants.REDIRECT_URI, this.config.getCallback());
			if (this.config.hasScope()) {
				request.addBodyParameter(OAuthConstants.SCOPE, this.config.getScope());
			}
			if (this.addGrantType) {
				request.addBodyParameter("grant_type", "authorization_code");
			}
		}
		final Response response = request.send();
		return this.api.getAccessTokenExtractor().extract(response.getBody());
	}

}