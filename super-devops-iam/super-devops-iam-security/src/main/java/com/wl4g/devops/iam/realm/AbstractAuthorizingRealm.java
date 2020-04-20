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
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter.*;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.*;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import com.wl4g.devops.common.exception.iam.AccessPermissionDeniedException;
import com.wl4g.devops.common.exception.iam.IllegalApplicationAccessException;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.common.authc.IamAuthenticationInfo;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.common.realm.AbstractPermittingAuthorizingRealm;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
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
public abstract class AbstractAuthorizingRealm<T extends AuthenticationToken> extends AbstractPermittingAuthorizingRealm {

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

	public AbstractAuthorizingRealm(IamBasedMatcher matcher) {
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
			 * [Extension]: Can be used to check the parameter
			 * 'pre-grant-ticket'</br>
			 */

			/**
			 * [Extension]: Save authenticate token, For example, for online
			 * session management and analysis.
			 * See:{@link com.wl4g.devops.iam.common.web.GenericApiController#wrapSessionAttribute(IamSession)}
			 */
			// Obtain authentication info.
			IamAuthenticationInfo info = doAuthenticationInfo((T) bind(KEY_AUTHC_TOKEN, token));
			notNull(info, "Authentication info can't be empty. refer to: o.a.s.a.ModularRealmAuthorizer.isPermitted()");

			/**
			 * [Extension]: Save authenticate info, For example, for online
			 * session management and analysis. *
			 * See:{@link com.wl4g.devops.iam.common.web.GenericApiController#wrapSessionAttribute(IamSession)}
			 */
			IamPrincipalInfo principal = info.getAccountInfo();
			principal.validate();
			bind(KEY_AUTHC_ACCOUNT_INFO, principal);

			return info;
		} catch (Throwable e) {
			throw new AuthenticationException(e);
		}
	}

	/**
	 * Get current authenticating principal {@link IamAuthenticationInfo}.</br>
	 *
	 * <font style='color:red'>Note: At least empty authentication information
	 * should be returned. Reason reference:
	 * {@link org.apache.shiro.authz.ModularRealmAuthorizer.isPermitted()}</font>
	 *
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 * @see {@link org.apache.shiro.authz.ModularRealmAuthorizer#isPermitted()}
	 */
	protected abstract IamAuthenticationInfo doAuthenticationInfo(T token) throws AuthenticationException;

	@Override
	protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
		AbstractIamAuthenticationToken tk = (AbstractIamAuthenticationToken) token;
		IamAuthenticationInfo info0 = (IamAuthenticationInfo) info;

		CredentialsMatcher matcher = getCredentialsMatcher();
		if (isNull(matcher)) {
			throw new AuthenticationException("A CredentialsMatcher must be configured in order to verify "
					+ "credentials during authentication.  If you do not wish for credentials to be examined, you "
					+ "can configure an " + AllowAllCredentialsMatcher.class.getName() + " instance.");
		}

		// Assert credentials match.
		if (!matcher.doCredentialsMatch(tk, info)) {
			throw new IncorrectCredentialsException(bundle.getMessage("AbstractIamAuthorizingRealm.credential.mismatch"));
		}

		// Assert when that no permissions are configured, forbid login.
		if (isBlank(info0.getAccountInfo().getPermissions())) {
			throw new AccessPermissionDeniedException(bundle.getMessage("AbstractIamAuthorizingRealm.permission.denied"));
		}

		// Check if have access to the client application.
		String fromAppName = tk.getRedirectInfo().getFromAppName();
		if (!isBlank(fromAppName)) {
			isTrue(!info.getPrincipals().isEmpty(),
					format("Authentication info principals is empty, please check the configure. [%s]", info));

			// For example: when using wechat scanning code (oauth2)
			// to log in, token.getPrincipal() is empty,
			// info.getPrimaryPrincipal() will not be empty.
			String principal = (String) info.getPrincipals().getPrimaryPrincipal();
			try {
				authHandler.assertApplicationAccessAuthorized(principal, fromAppName);
			} catch (IllegalApplicationAccessException ex) {
				// Disable fallback redirect?
				if (!tk.getRedirectInfo().isFallbackRedirect()) {
					throw ex;
				}

				// For example: first login to manager service(mp) with
				// 'admin', then logout, and then login to portal
				// service(portal) with user01. At this time, the check will
				// return that 'user01' has no permission to access manager
				// service(mp).
				// e.g.->https://sso.wl4g.com/login.html?service=mp&redirect_url=https%3A%2F%2Fmp.wl4g.com%2Fmp%2Fauthenticator

				// Fallback determine redirect to application.
				RedirectInfo fallbackRedirect = configurer.getFallbackRedirectInfo(tk,
						new RedirectInfo(config.getSuccessService(), config.getSuccessUri()));
				notNull(fallbackRedirect, "Fallback redirect info cannot be null");

				/**
				 * See:{@link AuthenticatorAuthenticationFilter#savedRequestParameters()}
				 * See:{@link AbstractIamAuthenticationFilter#getRedirectInfo()}
				 */
				bindKVParameters(KEY_REQ_AUTH_PARAMS, KEY_REQ_AUTH_REDIRECT, fallbackRedirect);
				log.warn("The principal({}) no access to '{}', fallback redirect to:{}, caused by: {}", principal, fromAppName,
						fallbackRedirect, getRootCausesString(ex));
			}
		}

	}

}