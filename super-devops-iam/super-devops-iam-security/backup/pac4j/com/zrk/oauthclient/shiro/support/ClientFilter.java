/*
 * Licensed to the bujiio organization of the Shiro project under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.zrk.oauthclient.shiro.support;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Description:TODO
 * @author:荣康
 * @time:2016年5月8日 下午7:03:50
 */
@SuppressWarnings("unchecked")
public class ClientFilter extends AuthenticatingFilter {
	private static Logger log = LoggerFactory.getLogger(ClientFilter.class);

	// the url where the application is redirected if the authentication fails
	private String failureUrl;

	// the clients definition
	private Clients clients;

	// This flag controls the behaviour of the filter after successful
	// redirection
	private boolean redirectAfterSuccessfulAuthentication = true;

	@Override
	protected boolean onAccessDenied(final ServletRequest request, final ServletResponse response) throws Exception {
		final AuthenticationToken token;
		try {
			token = createToken(request, response);
			if (token == null)
				return onLoginFailure(null, null, request, response);
		} catch (final RequiresHttpAction e) {
			log.debug("requires HTTP action : {}", e);
			return onLoginFailure(null, null, request, response);
		}
		try {
			final Subject subject = getSubject(request, response);
			subject.login(token);
			return onLoginSuccess(token, subject, request, response);
		} catch (final AuthenticationException e) {
			return onLoginFailure(token, e, request, response);
		}
	}

	@Override
	protected AuthenticationToken createToken(final ServletRequest request, final ServletResponse response) throws Exception {

		final J2EContext context = new J2EContext(WebUtils.toHttp(request), WebUtils.toHttp(response), new ShiroSessionStore());
		final Client<Credentials, UserProfile> client = getClients().findClient(context);
		CommonHelper.assertNotNull("client", client);
		CommonHelper.assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");
		log.debug("client : {}", client);
		Credentials credentials = null;
		try {
			credentials = client.getCredentials(context);
			log.debug("credentials : {}", credentials);
		} catch (Exception e) {
			log.info("用户取消登录或第三方回调错误");
		}
		return credentials != null ? new UsernamePasswordAndClientToken(client.getName(), credentials, context) : null;
	}

	@Override
	protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject, final ServletRequest request,
			final ServletResponse response) throws Exception {

		log.info("Login success");
		if (false == getRedirectAfterSuccessfulAuthentication())
			return true;
		else {
			// WebUtils.issueRedirect(request, response, getSuccessUrl());
			issueSuccessRedirect(request, response);
			return false;
		}
	}

	@Override
	protected boolean isAccessAllowed(final ServletRequest request, final ServletResponse response, final Object mappedValue) {
		return false;
	}

	@Override
	protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException ae,
			final ServletRequest request, final ServletResponse response) {
		// is user authenticated ?
		log.warn("Login failure", ae);
		final Subject subject = getSubject(request, response);
		if (subject.isAuthenticated()) {
			try {
				issueSuccessRedirect(request, response);
			} catch (final Exception e) {
				log.error("Cannot redirect to the default success url", e);
			}
		} else {
			try {
				WebUtils.issueRedirect(request, response, getSuccessUrl());
			} catch (final IOException e) {
				log.error("Cannot redirect to failure url : {}", getSuccessUrl(), e);
			}
		}
		return false;
	}

	public String getFailureUrl() {
		return this.failureUrl;
	}

	public void setFailureUrl(final String failureUrl) {
		this.failureUrl = failureUrl;
	}

	public Clients getClients() {
		return this.clients;
	}

	public void setClients(final Clients clients) throws TechnicalException {
		this.clients = clients;
		clients.init();
	}

	/**
	 * This redirectAfterSuccessfulAuthentication property controls the
	 * behaviour of the filter after successful login. If redirection is enabled
	 * (default) the filter will redirect the request to original requested url.
	 *
	 * In case redirection is disabled the filter will allow the request to
	 * passthrough the filter chain. This is useful for cas proxy (proxied
	 * application) where the credential receptor url is same as the resource
	 * url.
	 *
	 * @return current value of the property
	 */
	public boolean getRedirectAfterSuccessfulAuthentication() {
		return redirectAfterSuccessfulAuthentication;
	}

	public void setRedirectAfterSuccessfulAuthentication(boolean casPassThrough) {
		this.redirectAfterSuccessfulAuthentication = casPassThrough;
	}
}
