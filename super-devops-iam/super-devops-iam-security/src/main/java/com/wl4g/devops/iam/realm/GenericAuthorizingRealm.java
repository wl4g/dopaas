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

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.wl4g.devops.iam.authc.GenericAuthenticationInfo;
import com.wl4g.devops.iam.authc.GenericAuthenticationToken;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.authz.GenericAuthorizationInfo;
import com.wl4g.devops.iam.common.authc.IamAuthenticationInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.SimpleParameter;

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
public class GenericAuthorizingRealm extends AbstractAuthorizingRealm<GenericAuthenticationToken> {

	public GenericAuthorizingRealm(IamBasedMatcher matcher) {
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
	protected IamAuthenticationInfo doAuthenticationInfo(GenericAuthenticationToken token) throws AuthenticationException {
		// Get account by loginId(user-name)
		IamPrincipalInfo info = configurer.getIamAccount(new SimpleParameter((String) token.getPrincipal()));
		if (log.isDebugEnabled()) {
			log.debug("Get IamPrincipalInfo:{} by token:{}", info, token);
		}

		// To authenticationInfo
		if (isNull(info) || isBlank(info.getPrincipal())) {
			throw new UnknownAccountException(bundle.getMessage("GeneralAuthorizingRealm.notAccount", token.getPrincipal()));
		}

		// Authenticate attributes.(roles/permissions/rememberMe)
		PrincipalCollection principals = createPermitPrincipalCollection(info);
		return new GenericAuthenticationInfo(info, principals, getName());
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
		// Create simple authorization info
		GenericAuthorizationInfo info = new GenericAuthorizationInfo();
		// Merge authorized string(roles/permission)
		mergeAuthorizedString(principals, info);
		return info;
	}

}