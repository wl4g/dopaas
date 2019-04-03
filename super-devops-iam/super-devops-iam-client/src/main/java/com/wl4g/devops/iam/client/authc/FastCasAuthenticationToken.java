package com.wl4g.devops.iam.client.authc;

import org.apache.shiro.authc.RememberMeAuthenticationToken;
import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents a token for a CAS authentication (service ticket + user
 * id + remember me).
 *
 * @since 1.2
 */
public class FastCasAuthenticationToken implements RememberMeAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	/*
	 * the service ticket returned by the CAS server
	 */
	private String ticket;

	/*
	 * the user identifier
	 */
	private String userId;

	/*
	 * is the user in a remember me mode ?
	 */
	private boolean isRememberMe = false;

	public FastCasAuthenticationToken(String ticket) {
		this.ticket = ticket;
	}

	@Override
	public Object getPrincipal() {
		return userId;
	}

	public void setPrincipal(String principal) {
		this.userId = principal;
	}

	@NotBlank
	@Override
	public Object getCredentials() {
		return ticket;
	}

	public void setCredentials(String credentials) {
		this.ticket = credentials;
	}

	@Override
	public boolean isRememberMe() {
		return isRememberMe;
	}

	public void setRememberMe(boolean isRememberMe) {
		this.isRememberMe = isRememberMe;
	}
}
