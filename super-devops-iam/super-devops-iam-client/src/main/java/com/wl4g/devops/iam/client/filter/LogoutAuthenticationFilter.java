/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.client.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Assert;
import org.apache.shiro.util.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.bean.iam.model.LogoutModel;
import com.wl4g.devops.common.exception.iam.GrantTicketNullException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.client.authc.LogoutAuthenticationToken;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.configure.ClientSecurityConfigurer;
import com.wl4g.devops.iam.client.configure.ClientSecurityCoprocessor;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_LOGOUT_INFO;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_LOGOUT;
import static com.wl4g.devops.common.utils.web.WebUtils2.isTrue;
import static com.wl4g.devops.iam.common.utils.Sessions.getSessionId;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_LOGOUT;

/**
 * Logout authentication filter
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月6日
 * @since
 */
@IamFilter
public class LogoutAuthenticationFilter extends AbstractAuthenticationFilter<AuthenticationToken>
		implements IamAuthenticationFilter {
	final public static String NAME = "logoutFilter";

	final protected RestTemplate restTemplate;

	public LogoutAuthenticationFilter(IamClientProperties config, ClientSecurityConfigurer context,
			ClientSecurityCoprocessor coprocessor, JedisCacheManager cacheManager, RestTemplate restTemplate) {
		super(config, context, coprocessor, cacheManager);
		Assert.notNull(restTemplate, "'restTemplate' must not be null");
		this.restTemplate = restTemplate;
	}

	@Override
	protected AuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		// Using coercion ignores remote exit failures
		boolean forced = isTrue(request, config.getParam().getLogoutForced(), true);
		// Current session-id
		Serializable sessionId = getSessionId();
		if (log.isInfoEnabled()) {
			log.info("Logout of forced[{}], sessionId[{}]", forced, sessionId);
		}

		// Callback logout
		coprocessor.preLogout(forced, request, response);

		/*
		 * Post to remote logout
		 */
		LogoutModel logout = null;
		try {
			logout = doRequestRemoteLogout(forced);
		} catch (Exception e) {
			if (e instanceof IamException)
				log.warn("Failed to remote logout. {}", ExceptionUtils.getRootCauseMessage(e));
			else
				log.warn("Failed to remote logout.", e);
		}

		/*
		 * Check remote logout
		 */
		if (forced || checkLogout(logout)) {
			// Local session logout
			try {
				// try/catch added for SHIRO-298:
				getSubject(request, response).logout();

				if (log.isInfoEnabled()) {
					log.info("Local logout finished. sessionId[{}]", sessionId);
				}
			} catch (SessionException e) {
				log.warn("Logout exception. This can generally safely be ignored.", e);
			}
		}

		/*
		 * Redirection processing
		 */
		onLoginFailure(LogoutAuthenticationToken.EMPTY, null, request, response);
		return false;
	}

	/**
	 * Request logout fast-CAS server
	 * 
	 * @param forced
	 * @return
	 */
	private LogoutModel doRequestRemoteLogout(boolean forced) {
		Subject subject = SecurityUtils.getSubject();

		// Get grantTicket
		String grantTicket = (String) subject.getSession().getAttribute(SAVE_GRANT_TICKET);

		// Post server logout URL by grantTicket
		String url = this.buildRemoteLogoutUrl(grantTicket, forced);

		RespBase<LogoutModel> resp = this.restTemplate
				.exchange(url, HttpMethod.POST, null, new ParameterizedTypeReference<RespBase<LogoutModel>>() {
				}).getBody();

		if (!RespBase.isSuccess(resp)) {
			throw new IamException(resp.getMessage());
		}
		return resp.getData().get(KEY_LOGOUT_INFO);
	}

	/**
	 * Check remote logged-out success
	 * 
	 * @param logout
	 * @return
	 */
	private boolean checkLogout(LogoutModel logout) {
		return (logout != null && config.getServiceName().equals(String.valueOf(logout.getApplication())));
	}

	/**
	 * Build remote logout URL
	 * 
	 * @param forced
	 * @param grantTicket
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String buildRemoteLogoutUrl(String grantTicket, boolean forced) {
		if (!StringUtils.hasText(grantTicket)) {
			throw new GrantTicketNullException(
					"Logout failed, because grant Ticket could not be empty, it may have logged out, do not need to repeat logout");
		}

		/*
		 * Synchronize with xx.xx.session.mgt.IamSessionManager#getSessionId
		 */
		StringBuffer uri = new StringBuffer(config.getServerUri()).append(URI_S_BASE).append("/").append(URI_S_LOGOUT);
		Map queryParams = new LinkedHashMap<>();
		queryParams.put(config.getParam().getApplication(), config.getServiceName());
		queryParams.put(config.getParam().getGrantTicket(), grantTicket);
		queryParams.put(config.getParam().getLogoutForced(), forced);

		// Full URL
		return WebUtils2.applyQueryURL(uri.toString(), queryParams);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return "/" + URI_C_LOGOUT;
	}

}