/*
 * Copyright 2015 the original author or authors.
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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.wl4g.devops.common.bean.iam.IamAccountInfo;
import com.wl4g.devops.common.bean.iam.IamAccountInfo.Parameter;
import com.wl4g.devops.common.bean.iam.IamAccountInfo.SnsAuthorizingParameter;
import com.wl4g.devops.iam.authc.EmptyOauth2AuthorizationInfo;
import com.wl4g.devops.iam.authc.Oauth2SnsAuthenticationToken;
import com.wl4g.devops.iam.authc.credential.IamBasedMatcher;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.filter.ProviderSupports;
import com.wl4g.devops.iam.sns.SocialConnectionFactory;

/**
 * Default SNS oauth2 authorizing realm
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月8日
 * @since
 */
public abstract class Oauth2SnsAuthorizingRealm<T extends Oauth2SnsAuthenticationToken> extends AbstractIamAuthorizingRealm<T> {

	/**
	 * IAM Social connection factory
	 */
	@Autowired
	protected SocialConnectionFactory connectFactory;

	public Oauth2SnsAuthorizingRealm(IamBasedMatcher matcher, IamContextManager manager) {
		super(matcher, manager);
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
	protected AuthenticationInfo doAuthenticationInfo(Oauth2SnsAuthenticationToken token) throws AuthenticationException {
		// Check provider
		ProviderSupports.checkSupport(token.getSocial().getProvider());

		// Get account information
		/*
		 * The corresponding account ID (userId or accountId or principal) for
		 * the openId binding, validate in
		 * com.wl4g.devops.iam.matcher.Oauth2Matcher#doMatch
		 */
		Parameter parameter = new SnsAuthorizingParameter(token.getSocial().getProvider(), token.getSocial().getOpenId(),
				token.getSocial().getUnionId());
		IamAccountInfo account = this.context.getIamAccount(parameter);
		if (account != null && !StringUtils.isEmpty(account.getPrincipal())) {
			return new SimpleAuthenticationInfo(account.getPrincipal(), null, this.getName());
		}
		return EmptyOauth2AuthorizationInfo.EMPTY;
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
		return new SimpleAuthorizationInfo();
	}

}