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
package com.wl4g.devops.iam.realm;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Validator;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SESSION_ACCOUNT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SESSION_TOKEN;
import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.iam.common.utils.SessionBindings.bind;
import static com.wl4g.devops.iam.common.utils.SessionBindings.bindKVParameters;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.isTrue;

import com.wl4g.devops.common.exception.iam.IllegalApplicationAccessException;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.configure.ServerSecurityCoprocessor;
import com.wl4g.devops.iam.handler.AuthenticationHandler;

/**
 * Multiple realm routing processing.
 * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator#doMultiRealmAuthentication()}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月27日
 * @since
 */
public abstract class AbstractIamAuthorizingRealm<T extends AuthenticationToken> extends AuthorizingRealm {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Credential matcher
	 */
	final protected IamBasedMatcher matcher;

	/**
	 * Validation
	 */
	@Autowired
	protected Validator validator;

	/**
	 * Rest template
	 */
	@Autowired
	protected RestTemplate restTemplate;

	/**
	 * IAM server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * IAM authentication handler
	 */
	@Autowired
	protected AuthenticationHandler authHandler;

	/**
	 * IAM security configure handler
	 */
	@Autowired
	protected ServerSecurityConfigurer configurer;

	/**
	 * IAM server security processor
	 */
	@Autowired
	protected ServerSecurityCoprocessor coprocessor;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	public AbstractIamAuthorizingRealm(IamBasedMatcher matcher) {
		Assert.notNull(matcher, "'matcher' must not be null");
		this.matcher = matcher;
	}

	/**
	 * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator#doMultiRealmAuthentication}
	 */
	@SuppressWarnings("unchecked")
	@PostConstruct
	@Override
	protected void onInit() {
		// Initialization.
		super.onInit();
		// Credentials matcher set.
		super.setCredentialsMatcher(matcher);
		// AuthenticationTokenClass set.
		ResolvableType resolveType = ResolvableType.forClass(getClass());
		super.setAuthenticationTokenClass(
				(Class<? extends AuthenticationToken>) resolveType.getSuperType().getGeneric(0).resolve());
	}

	/**
	 * Authenticates a user and retrieves its information.
	 * 
	 * @param token
	 *            the authentication token
	 * @throws AuthenticationException
	 *             if there is an error during authentication.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		try {
			// Validation token.
			validator.validate(token);

			/*
			 * Extension Point Tips:: can be used to check the parameter
			 * 'pre-grant-ticket'</br>
			 */

			// Get authentication info and save it(Also include token)
			AuthenticationInfo info = doAuthenticationInfo((T) bind(KEY_SESSION_TOKEN, token));
			return bind(KEY_SESSION_ACCOUNT, info);
		} catch (Throwable e) {
			throw new AuthenticationException(e);
		}
	}

	protected abstract AuthenticationInfo doAuthenticationInfo(T token) throws AuthenticationException;

	@Override
	protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
		IamAuthenticationToken tk = (IamAuthenticationToken) token;

		CredentialsMatcher matcher = getCredentialsMatcher();
		if (nonNull(matcher)) {
			if (!matcher.doCredentialsMatch(tk, info)) {
				throw new IncorrectCredentialsException(bundle.getMessage("AbstractIamAuthorizingRealm.credential.mismatch"));
			}

			// Check whether the login user has access to the target IAM-client
			// application. (Check only when access application).
			String fromAppName = tk.getRedirectInfo().getFromAppName();
			if (!isBlank(fromAppName)) {
				isTrue(!info.getPrincipals().isEmpty(),
						String.format("Authentication info principals is empty, please check the configure. [%s]", info));
				// For example: first login to manager service(mp) with 'admin',
				// then logout, and then login to portal service(portal) with
				// user01. At this time, the check will return that 'user01' has
				// no permission to access manager service(mp).
				// e.g:
				// https://sso.wl4g.com/login.html?service=mp&redirect_url=https%3A%2F%2Fmp.wl4g.com%2Fmp%2Fauthenticator
				String principal = (String) tk.getPrincipal();
				try {
					authHandler.assertApplicationAccessAuthorized(principal, fromAppName);
				} catch (IllegalApplicationAccessException ex) {
					// Fallback determine redirect to application.
					RedirectInfo fallbackRedirect = coprocessor.fallbackAccessUnauthorizedApplication(tk, tk.getRedirectInfo());
					/**
					 * See:{@link AuthenticatorAuthenticationFilter#savedRequestParameters}
					 * See:{@link AbstractIamAuthenticationFilter#getFromAppName}
					 * See:{@link AbstractIamAuthenticationFilter#getFromRedirectUrl}
					 */
					bindKVParameters(KEY_REQ_AUTH_PARAMS, config.getParam().getApplication(), fallbackRedirect.getFromAppName(),
							config.getParam().getRedirectUrl(), fallbackRedirect.getRedirectUrl());
					if (log.isWarnEnabled()) {
						log.warn(
								"The user '{}' is not authorized to access application '{}', and has used the fallback access application: {}@{}, caused by: {}",
								principal, fromAppName, fallbackRedirect, getRootCausesString(ex, false));
					}
				}
			}

		} else {
			throw new AuthenticationException("A CredentialsMatcher must be configured in order to verify "
					+ "credentials during authentication.  If you do not wish for credentials to be examined, you "
					+ "can configure an " + AllowAllCredentialsMatcher.class.getName() + " instance.");
		}
	}

}