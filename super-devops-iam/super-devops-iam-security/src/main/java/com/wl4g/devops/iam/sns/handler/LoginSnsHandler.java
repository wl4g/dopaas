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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

import static com.wl4g.devops.common.web.BaseController.REDIRECT_PREFIX;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.sns.OAuth2ApiBindingFactory;

/**
 * Login SNS handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年2月24日
 * @since
 */
public class LoginSnsHandler extends AbstractSnsHandler {

	public LoginSnsHandler(IamProperties config, SnsProperties snsConfig, OAuth2ApiBindingFactory connectFactory,
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

		// PC-side browsers use agent redirection(QQ,sina)
		Assert.hasText(connectParams.get(config.getParam().getAgent()),
				String.format("'%s' must not be empty", config.getParam().getAgent()));
	}

	@Override
	protected void checkCallbackParameters(String provider, String state, String code, Map<String, String> connectParams) {
		// Check 'state'
		Assert.notEmpty(connectParams, String.format("State '%s' is invalid or expired", state));
		super.checkCallbackParameters(provider, state, code, connectParams);
	}

	@Override
	protected String postCallbackResponse(String provider, String callbackId, Map<String, String> connectParams,
			HttpServletRequest request) {
		return super.getLoginSubmitUrl(provider, callbackId, request);
	}

	@Override
	public Which which() {
		return Which.LOGIN;
	}

}