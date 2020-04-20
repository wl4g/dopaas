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
package com.wl4g.devops.iam.config.properties;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.correctAuthenticaitorURI;
import static com.wl4g.devops.tool.common.web.WebUtils2.cleanURI;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.hasText;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.config.properties.ServerParamProperties;
import com.wl4g.devops.iam.filter.InternalWhiteListServerAuthenticationFilter;
import com.wl4g.devops.iam.sns.web.DefaultOauth2SnsController;

/**
 * IAM server properties
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月4日
 * @since
 */
@ConfigurationProperties(prefix = "spring.cloud.devops.iam")
public class IamProperties extends AbstractIamProperties<ServerParamProperties> {
	private static final long serialVersionUID = -5858422822181237865L;

	/**
	 * Default view loader path
	 */
	final public static String DEFAULT_VIEW_LOADER_PATH = "/default-webapps";

	/**
	 * Default view login URI.
	 */
	final public static String DEFAULT_VIEW_LOGIN_URI = DEFAULT_VIEW_BASE_URI + "/login.html";

	/**
	 * Login page URI
	 */
	private String loginUri = DEFAULT_VIEW_LOGIN_URI;

	/**
	 * Login success redirection to end-point service name. </br>
	 *
	 * <pre>
	 * umc-manager@http://localhost:14048
	 * </pre>
	 */
	private String successService = EMPTY;

	/**
	 * Login success redirection to end-point.(Must be back-end server URI)
	 * </br>
	 *
	 * <pre>
	 * umc-manager@http://localhost:14048
	 * </pre>
	 */
	private String successUri = "http://localhost:8080";

	/**
	 * Unauthorized(403) page URI
	 */
	private String unauthorizedUri = DEFAULT_VIEW_403_URI;

	/**
	 * Matcher configuration properties.
	 */
	private MatcherProperties matcher = new MatcherProperties();

	/**
	 * Ticket configuration properties.
	 */
	private TicketProperties ticket = new TicketProperties();

	/**
	 * IAM server parameters configuration properties.
	 */
	private ServerParamProperties param = new ServerParamProperties();

	/**
	 * IAM server API configuration properties.
	 */
	private ApiProperties api = new ApiProperties();

	public String getLoginUri() {
		return loginUri;
	}

	public void setLoginUri(String loginUri) {
		this.loginUri = cleanURI(loginUri);
	}

	public void setSuccessEndpoint(String successEndpoint) {
		hasText(successEndpoint, "Success endpoint must not be empty.");
		this.successService = successEndpoint.split("@")[0];
		this.successUri = cleanURI(correctAuthenticaitorURI(successEndpoint.split("@")[1]));
	}

	public String getSuccessService() {
		return successService;
	}

	/**
	 * Situation1: http://myapp.domain.com/myapp/xxx/list?id=1 Situation1:
	 * /view/index.html ===> http://myapp.domain.com/myapp/authenticator?id=1
	 * <p>
	 * Implementing the IAM-CAS protocol: When successful login, you must
	 * redirect to the back-end server URI of IAM-CAS-Client. (Note: URI of
	 * front-end pages can not be used directly).
	 *
	 * @see {@link com.wl4g.devops.iam.client.filter.AuthenticatorAuthenticationFilter}
	 * @see {@link com.wl4g.devops.iam.filter.AuthenticatorAuthenticationFilter#determineSuccessUrl()}
	 */
	@Override
	public String getSuccessUri() {
		return successUri;
	}

	@Override
	public String getUnauthorizedUri() {
		return unauthorizedUri;
	}

	public void setUnauthorizedUri(String unauthorizedUri) {
		this.unauthorizedUri = unauthorizedUri;
	}

	public MatcherProperties getMatcher() {
		return matcher;
	}

	public void setMatcher(MatcherProperties matcher) {
		this.matcher = matcher;
	}

	public TicketProperties getTicket() {
		return ticket;
	}

	public void setTicket(TicketProperties ticket) {
		this.ticket = ticket;
	}

	public ServerParamProperties getParam() {
		return this.param;
	}

	public void setParam(ServerParamProperties param) {
		this.param = param;
	}

	public ApiProperties getApi() {
		return api;
	}

	public void setApi(ApiProperties api) {
		this.api = api;
	}

	@Override
	protected void applyDefaultIfNecessary() {
		super.applyDefaultIfNecessary();

		// Default URL filter chain.
		addDefaultFilterChain();

		// Default success endPoint.
		if (isBlank(getSuccessService())) {
			setSuccessEndpoint(environment.getProperty("spring.application.name") + "@" + DEFAULT_VIEW_INDEX_URI);
		}
	}

	@Override
	protected void validation() {
		hasText(getSuccessService(), "Success service must not be empty.");
		hasText(getSuccessUri(), "SuccessUri must not be empty, e.g. http://localhost:14041");
		super.validation();
	}

	/**
	 * Add default filter chain settings.<br/>
	 * {@link DefaultOauth2SnsController#connect}<br/>
	 */
	final private void addDefaultFilterChain() {
		// Default view access files request rules.
		getFilterChain().put(DEFAULT_VIEW_BASE_URI + "/**", "anon");
		// SNS authenticator controller rules.
		getFilterChain().put(URI_S_SNS_BASE + "/**", "anon");
		// Login authenticator controller rules.
		getFilterChain().put(URI_S_LOGIN_BASE + "/**", "anon");
		// Verify(CAPTCHA/SMS) authenticator controller rules.
		getFilterChain().put(URI_S_VERIFY_BASE + "/**", "anon");
		// RCM(Simple risk control) controller rules.
		getFilterChain().put(URI_S_RCM_BASE + "/**", "anon");
		// API v1 controller rules.
		getFilterChain().put(URI_S_API_V1_BASE + "/**", InternalWhiteListServerAuthenticationFilter.NAME);
	}

}