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

import java.util.Arrays;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;

/**
 * 定义支持密码登陆和第三方登陆的Token
 * 
 * @Description:TODO
 * @author:荣康
 * @time:2016年5月8日 下午6:57:33
 */
public final class UsernamePasswordAndClientToken implements HostAuthenticationToken, RememberMeAuthenticationToken {

	private static final long serialVersionUID = 3141878022445836151L;

	enum TokenType {
		USERNAME_PASSWOR, CLIENT
	}

	private String clientName;

	private Credentials credentials;

	private WebContext context;

	private String userId;

	private String username;

	private char[] password;

	private boolean rememberMe = false;

	private String host;

	private TokenType tokenType;

	public UsernamePasswordAndClientToken(final String clientName, final Credentials credentials, final WebContext context) {
		this.clientName = clientName;
		this.credentials = credentials;
		this.context = context;
		this.tokenType = TokenType.CLIENT;
	}

	public UsernamePasswordAndClientToken(final String username, final char[] password) {
		this(username, password, false, null);
	}

	public UsernamePasswordAndClientToken(final String username, final String password) {
		this(username, password != null ? password.toCharArray() : null, false, null);
	}

	public UsernamePasswordAndClientToken(final String username, final char[] password, final String host) {
		this(username, password, false, host);
	}

	public UsernamePasswordAndClientToken(final String username, final String password, final String host) {
		this(username, password != null ? password.toCharArray() : null, false, host);
	}

	public UsernamePasswordAndClientToken(final String username, final char[] password, final boolean rememberMe) {
		this(username, password, rememberMe, null);
	}

	public UsernamePasswordAndClientToken(final String username, final String password, final boolean rememberMe) {
		this(username, password != null ? password.toCharArray() : null, rememberMe, null);
	}

	public UsernamePasswordAndClientToken(final String username, final char[] password, final boolean rememberMe,
			final String host) {
		this.username = username;
		this.password = password;
		this.rememberMe = rememberMe;
		this.host = host;
		this.tokenType = TokenType.USERNAME_PASSWOR;
	}

	public UsernamePasswordAndClientToken(final String username, final String password, final boolean rememberMe,
			final String host) {
		this(username, password != null ? password.toCharArray() : null, rememberMe, host);
	}

	public void clear() {
		this.username = null;
		this.host = null;
		this.rememberMe = false;

		if (this.password != null) {
			for (int i = 0; i < password.length; i++) {
				this.password[i] = 0x00;
			}
			this.password = null;
		}

		this.clientName = null;

		this.credentials = null;

		this.context = null;

		this.userId = null;

	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getClientName() {
		return this.clientName;
	}

	public Object getCredentials() {
		if (this.tokenType == TokenType.CLIENT)
			return this.credentials;
		else
			return this.getPassword();
	}

	public Object getPrincipal() {
		if (this.tokenType == TokenType.CLIENT)
			return this.userId;
		else
			return getUsername();
	}

	public WebContext getContext() {
		return context;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public void setContext(WebContext context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "UsernamePasswordAndClientToken [clientName=" + clientName + ", credentials=" + credentials + ", context="
				+ context + ", userId=" + userId + ", username=" + username + ", password=" + Arrays.toString(password)
				+ ", rememberMe=" + rememberMe + ", host=" + host + ", tokenType=" + tokenType + "]";
	}

}
