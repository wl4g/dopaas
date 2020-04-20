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
package com.wl4g.devops.iam.sns.handler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.sns.OAuth2ApiBinding;
import com.wl4g.devops.iam.sns.OAuth2ApiBindingFactory;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;
import com.wl4g.devops.tool.common.web.WebUtils2;

import static com.wl4g.devops.common.web.BaseController.REDIRECT_PREFIX;

/**
 * Abstract based binding or UnBinding SNS handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年2月24日
 * @since
 */
public abstract class BasedBindSnsHandler extends AbstractSnsHandler {

	public BasedBindSnsHandler(IamProperties config, SnsProperties snsConfig, OAuth2ApiBindingFactory connectFactory,
			ServerSecurityConfigurer context) {
		super(config, snsConfig, connectFactory, context);
	}

	@Override
	public String doOAuth2GetAuthorizingUrl(Which which, String provider, String state, Map<String, String> connectParams) {
		// Connecting
		String authorizingUrl = super.doOAuth2GetAuthorizingUrl(which, provider, state, connectParams);

		// Save connect parameters
		saveOauth2ConnectParameters(provider, state, connectParams);

		return REDIRECT_PREFIX + authorizingUrl;
	}

	@Override
	protected void checkConnectParameters(String provider, String state, Map<String, String> connectParams) {
		super.checkConnectParameters(provider, state, connectParams);

		// Check connect parameters
		Assert.notEmpty(connectParams, "Connect parameters must not be empty");

		// Check principal
		String principalKey = config.getParam().getPrincipalName();
		Assert.hasText(connectParams.get(principalKey), String.format("'%s' must not be empty", principalKey));

		// PC-side browsers use agent redirection(QQ,sina)
		Assert.hasText(connectParams.get(config.getParam().getAgent()),
				String.format("'%s' must not be empty", config.getParam().getAgent()));

		// Check refreshUrl
		String refreshUrl = connectParams.get(config.getParam().getRefreshUrl());
		Assert.hasText(refreshUrl, String.format("'%s' must not be empty", config.getParam().getRefreshUrl()));
		try {
			new URI(refreshUrl);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(String.format("Error syntax %s", config.getParam().getRefreshUrl()), e);
		}
	}

	@Override
	protected void checkCallbackParameters(String provider, String state, String code, Map<String, String> connectParams) {
		// Check 'state'
		Assert.notEmpty(connectParams, String.format("State '%s' is invalid or expired", state));
		super.checkCallbackParameters(provider, state, code, connectParams);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected String doHandleOAuth2Callback(String provider, String code, OAuth2ApiBinding connect, Map<String, String> connectParams,
			HttpServletRequest request) {
		// Access token
		Oauth2AccessToken ast = connect.getAccessToken(code);
		// User openId
		Oauth2OpenId openId = connect.getUserOpenId(ast);
		// User info
		Oauth2UserProfile profile = connect.getUserInfo(ast.accessToken(), openId.openId());

		// Binding principal id
		String principal = connectParams.get(config.getParam().getPrincipalName());

		// To social connection info
		SocialConnectInfo info = new SocialConnectInfo(provider, principal, openId.openId(), openId.unionId());
		// Extra info
		info.setClientIp(WebUtils2.getHttpRemoteAddr(request));
		info.setReferer(request.getHeader("Referer"));
		info.setUserAgent(request.getHeader("User-Agent"));
		info.setUserProfile(BeanMap.create(profile)); // User info

		// Binding or UnBinding process
		postBindingProcess(info);
		return null;
	}

	@Override
	protected String postCallbackResponse(String provider, String result, Map<String, String> connectParams,
			HttpServletRequest request) {
		return connectParams.get(config.getParam().getRefreshUrl());
	}

	/**
	 * Follow-up actions related to binding or UnBinding.
	 *
	 * @param info
	 */
	protected abstract void postBindingProcess(SocialConnectInfo info);
}