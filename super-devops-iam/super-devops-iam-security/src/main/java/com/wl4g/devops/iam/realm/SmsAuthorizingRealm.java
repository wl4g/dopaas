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

import com.wl4g.devops.iam.authc.SmsAuthenticationInfo;
import com.wl4g.devops.iam.authc.SmsAuthenticationToken;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.authz.SmsAuthorizationInfo;
import com.wl4g.devops.iam.common.authc.IamAuthenticationInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.SmsParameter;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.StringUtils;

import static com.wl4g.devops.iam.authc.SmsAuthenticationToken.Action.BIND;
import static com.wl4g.devops.iam.authc.SmsAuthenticationToken.Action.LOGIN;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getPrincipal;

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
public class SmsAuthorizingRealm extends AbstractAuthorizingRealm<SmsAuthenticationToken> {

	public SmsAuthorizingRealm(IamBasedMatcher matcher) {
		super(matcher);
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
	protected IamAuthenticationInfo doAuthenticationInfo(SmsAuthenticationToken token) throws AuthenticationException {
		// Get account by mobile phone
		IamPrincipalInfo info = configurer.getIamAccount(new SmsParameter((String) token.getPrincipal()));
		if (log.isDebugEnabled()) {
			log.debug("Get IamAccountInfo:{} by token:{}", info, token);
		}

		String principal = null; // Current login principal
		// If the operation is not logged-in, Princial is the currently
		// logged-in subject.
		if (LOGIN != token.getAction()) {
			principal = getPrincipal();
		}

		// Actions are unbind or login, account information is required.
		if (BIND != token.getAction()) {
			assertAccountInfo(info, token);
			principal = info.getPrincipal();
		}

		// Authenticate attributes.(roles/permissions/rememberMe)
		PrincipalCollection principals = createPermitPrincipalCollection(principal, info);
		return new SmsAuthenticationInfo(info, principals, getName());
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
		return new SmsAuthorizationInfo();
	}

	/**
	 * Assertion account information
	 *
	 * @param acc
	 * @param token
	 */
	private void assertAccountInfo(IamPrincipalInfo acc, SmsAuthenticationToken token) {
		if (acc == null || !StringUtils.hasText(acc.getPrincipal())) {
			throw new UnknownAccountException(bundle.getMessage("GeneralAuthorizingRealm.notAccount", token.getPrincipal()));
		}
	}

}