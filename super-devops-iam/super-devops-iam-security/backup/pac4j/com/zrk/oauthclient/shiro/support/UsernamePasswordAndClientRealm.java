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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zrk.oauthclient.shiro.support.UsernamePasswordAndClientToken.TokenType;

/**
 * 支持用户名密码登陆和第三方登陆使用
 * 
 * @Description:TODO
 * @author:荣康
 * @time:2016年5月8日 下午7:52:52
 */
public abstract class UsernamePasswordAndClientRealm extends AuthorizingRealm {

	private static Logger log = LoggerFactory.getLogger(UsernamePasswordAndClientRealm.class);

	private Clients clients;

	private String defaultRoles;

	private String defaultPermissions;

	public UsernamePasswordAndClientRealm() {
		setAuthenticationTokenClass(UsernamePasswordAndClientToken.class);
	}

	/**
	 * 身份认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authenticationToken)
			throws AuthenticationException {
		UsernamePasswordAndClientToken passwordAndClientToken = (UsernamePasswordAndClientToken) authenticationToken;
		if (passwordAndClientToken.getTokenType() == TokenType.CLIENT)
			return internalClientGetAuthenticationInfo(authenticationToken); // 第三方登录认证
		else
			return internalUsernamePasswordGetAuthenticationInfo(authenticationToken);// 用户名密码登录认证
	}

	// 第三方登录认证预处理
	@SuppressWarnings("unchecked")
	protected AuthenticationInfo internalClientGetAuthenticationInfo(final AuthenticationToken authenticationToken) {
		final UsernamePasswordAndClientToken clientToken = (UsernamePasswordAndClientToken) authenticationToken;
		log.debug("clientToken : {}", clientToken);
		if (clientToken == null) {
			return null;
		}

		final Credentials credentials = (Credentials) clientToken.getCredentials();
		log.debug("credentials : {}", credentials);

		final Client<Credentials, CommonProfile> client = this.clients.findClient(clientToken.getClientName());
		log.debug("client : {}", client);

		final CommonProfile profile = client.getUserProfile(credentials, clientToken.getContext());
		log.debug("profile : {}", profile);

		if (profile == null) {
			final String message = "No profile retrieved from authentication using client : " + client + " and credentials : "
					+ credentials;
			log.info(message);
			throw new AuthenticationException(message);
		}

		// refresh authentication token with user id
		final String userId = profile.getTypedId();
		clientToken.setUserId(userId);
		// set rememberMe status
		clientToken.setRememberMe(profile.isRemembered());
		return internalClientGetAuthenticationInfo(profile, credentials);
	}

	/**
	 * 用户名密码登录认证
	 * 
	 * @param authenticationToken
	 * @return
	 */
	protected abstract AuthenticationInfo internalUsernamePasswordGetAuthenticationInfo(
			final AuthenticationToken authenticationToken);

	/**
	 * 第三方登录认证数据再处理
	 * 
	 * @return
	 */
	protected abstract AuthenticationInfo internalClientGetAuthenticationInfo(final CommonProfile profile,
			final Credentials credentials);

	/**
	 * 授权信息
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
		Set<String> roles = new HashSet<String>(split(this.defaultRoles));
		Set<String> permissions = new HashSet<String>(split(this.defaultPermissions));
		// get roles and permissions from principals
		Collection<CommonProfile> profiles = principals.byType(CommonProfile.class);
		if (profiles != null) {
			for (CommonProfile profile : profiles) {
				if (profile != null) {
					roles.addAll(profile.getRoles());
					permissions.addAll(profile.getPermissions());
				}
			}
		}
		// create simple authorization info
		final SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addRoles(roles);
		simpleAuthorizationInfo.addStringPermissions(permissions);
		return simpleAuthorizationInfo;
	}

	/**
	 * Split a string into a list of not empty and trimmed strings, delimiter is
	 * a comma.
	 *
	 * @param s
	 *            the input string
	 * @return the list of not empty and trimmed strings
	 */
	protected List<String> split(final String s) {
		final List<String> list = new ArrayList<String>();
		final String[] elements = StringUtils.split(s, ',');
		if (elements != null && elements.length > 0) {
			for (final String element : elements) {
				if (StringUtils.hasText(element)) {
					list.add(element.trim());
				}
			}
		}
		return list;
	}

	public Clients getClients() {
		return this.clients;
	}

	public void setClients(final Clients clients) throws TechnicalException {
		this.clients = clients;
		clients.init();
	}

	public String getDefaultRoles() {
		return this.defaultRoles;
	}

	public void setDefaultRoles(final String defaultRoles) {
		this.defaultRoles = defaultRoles;
	}

	public String getDefaultPermissions() {
		return this.defaultPermissions;
	}

	public void setDefaultPermissions(final String defaultPermissions) {
		this.defaultPermissions = defaultPermissions;
	}
}
