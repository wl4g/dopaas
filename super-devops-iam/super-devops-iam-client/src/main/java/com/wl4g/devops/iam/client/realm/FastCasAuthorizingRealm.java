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
package com.wl4g.devops.iam.client.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.wl4g.devops.common.exception.iam.TicketValidateException;
import com.wl4g.devops.iam.client.authc.FastAuthenticationInfo;
import com.wl4g.devops.iam.client.authc.FastCasAuthenticationToken;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.validation.IamValidator;
import com.wl4g.devops.iam.common.authc.IamAuthenticationInfo;
import com.wl4g.devops.iam.common.authc.model.TicketValidatedAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidateModel;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_REMEMBERME_NAME;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bind;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getSession;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_LANG_ATTRIBUTE_NAME;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;

import java.util.Date;

/**
 * This realm implementation acts as a CAS client to a CAS server for
 * authentication and basic authorization.
 * <p/>
 * This realm functions by inspecting a submitted
 * {@link org.apache.shiro.cas.CasToken CasToken} (which essentially wraps a CAS
 * service ticket) and validates it against the CAS server using a configured
 * CAS {@link org.jasig.cas.client.validation.TicketValidator TicketValidator}.
 * <p/>
 * The {@link #getValidationProtocol() validationProtocol} is {@code CAS} by
 * default, which indicates that a a
 * {@link org.jasig.cas.client.validation.Cas20ServiceTicketValidator
 * Cas20ServiceTicketValidator} will be used for ticket validation. You can
 * alternatively set or
 * {@link org.jasig.cas.client.validation.Saml11TicketValidator
 * Saml11TicketValidator} of CAS client. It is based on {@link AuthorizingRealm
 * AuthorizingRealm} for both authentication and authorization. User id and
 * attributes are retrieved from the CAS service ticket validation response
 * during authentication phase. Roles and permissions are computed during
 * authorization phase (according to the attributes previously retrieved).
 *
 * @since 1.2
 */
public class FastCasAuthorizingRealm extends AbstractClientAuthorizingRealm {

	public FastCasAuthorizingRealm(IamClientProperties config,
			IamValidator<TicketValidateModel, TicketValidatedAssertModel<IamPrincipalInfo>> validator) {
		super(config, validator);
		super.setAuthenticationTokenClass(FastCasAuthenticationToken.class);
	}

	/**
	 * Authenticates a user and retrieves its information.
	 * 
	 * @param token
	 *            the authentication token
	 * @throws AuthenticationException
	 *             if there is an error during authentication.
	 */
	@Override
	protected IamAuthenticationInfo doAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String granticket = EMPTY;
		try {
			notNull(token, "'authenticationToken' must not be null");
			FastCasAuthenticationToken fctk = (FastCasAuthenticationToken) token;

			// Get request flash grant ticket(May be empty)
			granticket = (String) fctk.getCredentials();

			// Contact CAS remote server to validate ticket
			TicketValidatedAssertModel<IamPrincipalInfo> assertion = doRequestRemoteTicketValidation(granticket);

			// Grant ticket assertion .
			assertTicketValidation(assertion);

			/**
			 * {@link JedisIamSessionDAO#update()} </br>
			 * Update session expire date time.
			 */
			Date validUntilDate = assertion.getValidUntilDate();
			long maxIdleTimeMs = validUntilDate.getTime() - System.currentTimeMillis();
			state(maxIdleTimeMs > 0,
					String.format("Remote authenticated response session expired time: %s invalid, maxIdleTimeMs: %s",
							validUntilDate, maxIdleTimeMs));
			getSession().setTimeout(maxIdleTimeMs);

			IamPrincipalInfo info = assertion.getPrincipalInfo();
			// Attribute of lang
			bind(KEY_LANG_ATTRIBUTE_NAME, info.getAttributes().get(KEY_LANG_ATTRIBUTE_NAME));

			// Update settings grant ticket
			String newGrantTicket = String.valueOf(info.getStoredCredentials());
			fctk.setCredentials(newGrantTicket);

			// Attribute of remember
			String principal = assertion.getPrincipalInfo().getPrincipal();
			fctk.setPrincipal(principal); // MARK1
			fctk.setRememberMe(parseBoolean(info.getAttributes().get(KEY_REMEMBERME_NAME)));
			if (log.isInfoEnabled()) {
				log.info("Validated grantTicket: {}, principal: {}", granticket, principal);
			}

			// Authenticate attributes.(roles/permissions/rememberMe)
			PrincipalCollection principals = createPermitPrincipalCollection(info);

			// You should always use token credentials because the default
			// SimpleCredentialsMatcher checks.
			return new FastAuthenticationInfo(info, principals, getName());
		} catch (Exception e) {
			throw new CredentialsException(String.format("Unable to validate ticket [%s]", granticket), e);
		}
	}

	/**
	 * Retrieves the AuthorizationInfo for the given principals (the CAS
	 * previously authenticated user : id + attributes).
	 * 
	 * @param principals
	 *            the primary identifying principals of the AuthorizationInfo
	 *            that should be retrieved.
	 * @return the AuthorizationInfo associated with this principals.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// Merge authorized string(roles/permission)
		return mergeAuthorizedString(principals, new SimpleAuthorizationInfo());
	}

	/**
	 * Contact fast-CAS remote server to validate ticket.
	 * 
	 * @param ticket
	 * @return
	 */
	private TicketValidatedAssertModel<IamPrincipalInfo> doRequestRemoteTicketValidation(String ticket) {
		return ticketValidator.validate(new TicketValidateModel(ticket, config.getServiceName()));
	}

	/**
	 * Assert ticket validate failure
	 * 
	 * @param assertion
	 * @throws TicketValidateException
	 */
	private void assertTicketValidation(TicketValidatedAssertModel<IamPrincipalInfo> assertion) throws TicketValidateException {
		if (isNull(assertion)) {
			throw new TicketValidateException("ticket assertion must not be null");
		}
		IamPrincipalInfo info = assertion.getPrincipalInfo();
		if (isNull(info)) {
			throw new TicketValidateException("Principal info must not be null");
		}
		if (isNull(info.getStoredCredentials())) {
			throw new TicketValidateException("grant ticket must not be null");
		}
		if (isNull(info.getAttributes()) || info.getAttributes().isEmpty()) {
			throw new TicketValidateException("'principal.attributes' must not be empty");
		}
		if (isBlank((String) info.getRoles())) {
			if (log.isWarnEnabled()) {
				log.warn("Principal '{}' role is empty", info.getPrincipal());
			}
			// throw new TicketValidationException(String.format("Principal '%s'
			// roles must not empty", principal.getName()));
		}
		if (isBlank((String) info.getPermissions())) {
			if (log.isWarnEnabled()) {
				log.warn("Principal '{}' permits is empty", info.getPrincipal());
			}
			// throw new TicketValidationException(String.format("Principal '%s'
			// permits must not empty", principal.getName()));
		}
	}

}