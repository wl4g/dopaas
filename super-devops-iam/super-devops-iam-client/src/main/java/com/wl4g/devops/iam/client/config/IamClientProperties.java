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
package com.wl4g.devops.iam.client.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.client.config.IamClientProperties.ClientParamProperties;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.*;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;

@ConfigurationProperties(prefix = "spring.cloud.devops.iam.client")
public class IamClientProperties extends AbstractIamProperties<ClientParamProperties> implements InitializingBean {
	private static final long serialVersionUID = -8848998112902613969L;

	/**
	 * Default IAM server URI.
	 */
	final public static String DEFAULT_VIEW_SERV_URI = DEFAULT_VIEW_BASE_URI + "/login.html";

	/**
	 * IAM server base URI (public network)
	 */
	private String serverUri = "http://localhost:14040/iam-server";

	/**
	 * This configuration item is used to specify a custom login page, default
	 * to {spring.cloud.devops.iam.client.server-uri}, that is, the login page
	 * controlled by iam-server.
	 */
	private String loginUri;

	/**
	 * This success(index) page URI
	 */
	private String successUri = "http://localhost:8080/index.html";

	/**
	 * IAM server unauthorized(403) page URI
	 */
	private String unauthorizedUri = "http://localhost:14040/iam-server" + DEFAULT_VIEW_403_URI;

	/**
	 * Re-login callback URL, whether to use the previously remembered URL.
	 * {@link com.wl4g.devops.iam.client.filter.ROOTAuthenticationFilter#isAccessAllowed()}
	 */
	private boolean useRememberRedirect = true;

	/**
	 * Secondary authenticator provider name.
	 */
	private String secondAuthenticatorProvider = "wechat";

	/**
	 * IAM client parameters configuration.
	 */
	private ClientParamProperties param = new ClientParamProperties();

	/**
	 * Implementing the IAM-CAS protocol: When successful login, you must
	 * redirect to the back-end server URI of IAM-CAS-Client. (Note: URI of
	 * front-end pages can not be used directly).
	 * See:{@link com.wl4g.devops.iam.client.filter.AuthenticatorAuthenticationFilter}
	 * </br>
	 * </br>
	 * e.g. </br>
	 * Situation1: http://myapp.domain.com/myapp/xxx/list?id=1</br>
	 * Situation1: /view/index.html</br>
	 * => http://myapp.domain.com/myapp/authenticator?id=1</br>
	 * See:{@link com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter#determineSuccessUrl}
	 */
	@Override
	public String getLoginUri() {
		return loginUri;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = loginUri;
	}

	public String getServerUri() {
		return serverUri;
	}

	public void setServerUri(String baseUri) {
		this.serverUri = baseUri;
	}

	@Override
	public String getSuccessUri() {
		return successUri;
	}

	public void setSuccessUri(String successUri) {
		this.successUri = successUri;
	}

	@Override
	public String getUnauthorizedUri() {
		return unauthorizedUri;
	}

	public void setUnauthorizedUri(String unauthorizedUri) {
		this.unauthorizedUri = unauthorizedUri;
	}

	public boolean isUseRememberRedirect() {
		return useRememberRedirect;
	}

	public void setUseRememberRedirect(boolean useRememberRedirect) {
		this.useRememberRedirect = useRememberRedirect;
	}

	public String getSecondAuthenticatorProvider() {
		return secondAuthenticatorProvider;
	}

	public void setSecondAuthenticatorProvider(String secondAuthcProvider) {
		this.secondAuthenticatorProvider = secondAuthcProvider;
	}

	@Override
	public ClientParamProperties getParam() {
		return param;
	}

	@Override
	public void setParam(ClientParamProperties param) {
		this.param = param;
	}

	@Override
	protected void validation() {
		super.validation();
		hasTextOf(getServerUri(), "serverBaseUri");
		notNullOf(getFilterChain(), "filterChain");
	}

	@Override
	protected void applyDefaultIfNecessary() {
		super.applyDefaultIfNecessary();

		// Login page URI.
		if (isBlank(getLoginUri())) {
			setLoginUri(correctAuthenticaitorURI(getServerUri()));
		}
	}

	/**
	 * IAM client parameters configuration properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年11月29日
	 * @since
	 */
	public static class ClientParamProperties extends ParamProperties {
		private static final long serialVersionUID = 3258460473777285504L;

	}

}