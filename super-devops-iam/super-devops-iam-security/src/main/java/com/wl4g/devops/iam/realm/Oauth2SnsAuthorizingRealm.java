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

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.iam.authc.EmptyOauth2AuthenicationInfo;
import com.wl4g.devops.iam.authc.Oauth2SnsAuthenticationInfo;
import com.wl4g.devops.iam.authc.Oauth2SnsAuthenticationToken;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.common.authc.IamAuthenticationInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.Parameter;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.SnsAuthorizingParameter;
import com.wl4g.devops.iam.filter.ProviderSupport;
import com.wl4g.devops.iam.sns.OAuth2ApiBindingFactory;

/**
 * Default SNS oauth2 authorizing realm
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月8日
 * @since
 */
public abstract class Oauth2SnsAuthorizingRealm<T extends Oauth2SnsAuthenticationToken> extends AbstractAuthorizingRealm<T> {

	/**
	 * IAM Social connection factory
	 */
	@Autowired
	protected OAuth2ApiBindingFactory connectFactory;

	public Oauth2SnsAuthorizingRealm(IamBasedMatcher matcher) {
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
	protected IamAuthenticationInfo doAuthenticationInfo(Oauth2SnsAuthenticationToken token) throws AuthenticationException {
		ProviderSupport.checkSupport(token.getSocial().getProvider());

		/**
		 * Obtain the account information bound by openId.
		 * {@link Oauth2AuthorizingBoundMatcher#doCredentialsMatch()}
		 */
		Parameter parameter = new SnsAuthorizingParameter(token.getSocial().getProvider(), token.getSocial().getOpenId(),
				token.getSocial().getUnionId());
		IamPrincipalInfo info = configurer.getIamAccount(parameter);
		log.info("Get authenticate accountInfo: {}, by sns parameter: {}", toJSONString(info), toJSONString(parameter));

		if (nonNull(info) && !isBlank(info.getPrincipal())) {
			// Authenticate attributes.(roles/permissions/rememberMe)
			PrincipalCollection principals = createPermitPrincipalCollection(info);
			return new Oauth2SnsAuthenticationInfo(info, principals, getName());
		}
		return EmptyOauth2AuthenicationInfo.EMPTY;
	}

	/**
	 * Retrieves the AuthorizationInfo for the given principals (the CAS
	 * previously authenticated user : id + attributes).</br>
	 *
	 * @param principals
	 *            the primary identifying principals of the AuthorizationInfo
	 *            that should be retrieved.
	 * @return the AuthorizationInfo associated with this principals.
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return new SimpleAuthorizationInfo();
	}

}