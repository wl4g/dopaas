package com.wl4g.devops.iam.authc;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalMap;

public class EmptyOauth2AuthorizationInfo implements AuthenticationInfo {
	private static final long serialVersionUID = -1824494219125412412L;

	/**
	 * Default only instance
	 */
	final public static AuthenticationInfo EMPTY = new EmptyOauth2AuthorizationInfo();

	/**
	 * Empty principal collection
	 */
	final private static PrincipalCollection emptyPrincipalCollection = new SimplePrincipalMap();

	@Override
	public PrincipalCollection getPrincipals() {
		return emptyPrincipalCollection;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

}
