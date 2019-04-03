package com.wl4g.devops.iam.client.authc;

import org.apache.shiro.authc.AuthenticationToken;

public class LogoutAuthenticationToken implements AuthenticationToken {

	final private static long serialVersionUID = -7503506620220450148L;

	final public static LogoutAuthenticationToken EMPTY = new LogoutAuthenticationToken();

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

}
