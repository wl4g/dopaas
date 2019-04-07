package com.wl4g.devops.iam.authc.credential.secure;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.Assert;

/**
 * Final credentials token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月6日
 * @since
 */
public final class CredentialsToken {

	@NotBlank
	final private String principal;

	@NotBlank
	final private String credentials;

	final private boolean resolved;

	public CredentialsToken(CredentialsToken token, boolean solved) {
		this(token.getPrincipal(), token.getCredentials(), solved);
	}

	public CredentialsToken(String principal, String credentials) {
		this(principal, credentials, false);
	}

	public CredentialsToken(String principal, String credentials, boolean solved) {
		Assert.hasText(principal, "Principal must not be empty");
		Assert.hasText(credentials, "Credentials must not be empty");
		this.principal = principal;
		this.credentials = credentials;
		this.resolved = solved;
	}

	final public String getPrincipal() {
		return principal;
	}

	final public String getCredentials() {
		return credentials;
	}

	final public boolean isResolved() {
		return resolved;
	}

	@Override
	public String toString() {
		return "CredentialsToken [principal=" + principal + ", credentials=" + credentials + ", resolved=" + resolved + "]";
	}

}
