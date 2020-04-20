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

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static java.util.Collections.singletonMap;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import static org.apache.shiro.web.util.WebUtils.*;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.sns.OAuth2ApiBindingFactory;

/**
 * SNS client authorizer handler.(e.g: WeChat public account)
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年2月24日
 * @since
 */
public class ClientAuthcSnsHandler extends AbstractSnsHandler {

	public ClientAuthcSnsHandler(IamProperties config, SnsProperties snsConfig, OAuth2ApiBindingFactory connectFactory,
			ServerSecurityConfigurer context) {
		super(config, snsConfig, connectFactory, context);
	}

	@Override
	public Which which() {
		return Which.CLIENT_AUTH;
	}

	@Override
	protected Map<String, String> getAuthorizeUrlQueryParams(Which which, String provider, String state,
			Map<String, String> connectParams) {
		Map<String, String> queryParams = super.getAuthorizeUrlQueryParams(which, provider, state, connectParams);
		/*
		 * For redirect login needs,
		 * <br/><br/>see:i.f.AbstractIamAuthenticationFilter#onLoginSuccess()
		 * <br/><br/>grantTicket by xx.i.h.AuthenticationHandler#loggedin()
		 */
		String appKey = config.getParam().getApplication();
		queryParams.put(appKey, connectParams.get(appKey));
		return queryParams;
	}

	@Override
	protected void checkConnectParameters(String provider, String state, Map<String, String> connectParams) {
		super.checkConnectParameters(provider, state, connectParams);
		// Check application
		hasTextOf(connectParams.get(config.getParam().getApplication()), config.getParam().getApplication());
	}

	@Override
	protected Map<String, String> getOauth2ConnectParameters(String state, HttpServletRequest request) {
		return singletonMap(config.getParam().getApplication(), getCleanParam(request, config.getParam().getApplication()));
	}

	@Override
	protected String postCallbackResponse(String provider, String callbackId, Map<String, String> connectParams,
			HttpServletRequest request) {
		String application = config.getParam().getApplication();
		return getLoginSubmitUrl(provider, callbackId, request) + "&" + application + "=" + connectParams.get(application);
	}

}