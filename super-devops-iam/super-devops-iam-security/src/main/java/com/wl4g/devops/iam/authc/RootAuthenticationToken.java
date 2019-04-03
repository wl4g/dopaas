package com.wl4g.devops.iam.authc;

import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken;

/**
 * Authenticator authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public class RootAuthenticationToken extends AbstractIamAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	public RootAuthenticationToken(String fromAppName, String redirectUrl) {
		super(fromAppName, redirectUrl);
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public String getHost() {
		return null;
	}

}
