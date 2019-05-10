package com.wl4g.devops.iam.authc.credential.secure;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authc.CredentialsException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Credentials securer adapter
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月6日
 * @since
 */
public abstract class CredentialsSecurerAdapter implements CredentialsSecurer {

	@Autowired
	private IamCredentialsSecurer securer;

	@Override
	public String signature(@NotNull CredentialsToken token) {
		return securer.signature(token);
	}

	@Override
	public boolean validate(@NotNull CredentialsToken token, String storedCredentials)
			throws CredentialsException, RuntimeException {
		return securer.validate(token, storedCredentials);
	}

}
