package com.wl4g.devops.iam.authc;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class GeneralAuthenticationInfo extends SimpleAuthenticationInfo {
	private static final long serialVersionUID = 1558934819432102687L;

	public GeneralAuthenticationInfo(Object principal, Object credentials, String realmName) {
		this(principal, credentials, null, realmName);
	}

	public GeneralAuthenticationInfo(Object principal, Object credentials, ByteSource credentialsSalt, String realmName) {
		this.principals = new SimplePrincipalCollection(principal, realmName);
		this.credentials = credentials;
		this.credentialsSalt = credentialsSalt;
	}

}
