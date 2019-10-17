package com.wl4g.devops.iam.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.configure.SecureConfig;
import com.wl4g.devops.iam.configure.SecureConfigureAdapter;

/**
 * Define security configuration adapter (security signature algorithm
 * configuration, etc.)
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月16日
 * @since
 */
@Component
public class StandardSecureConfigureAdapter implements SecureConfigureAdapter {

	@Autowired
	protected ConfigurableEnvironment environment;

	@Override
	public SecureConfig configure() {
		String active = environment.getProperty("spring.profiles.active");
		Assert.hasText(active, "Please check configure, spring profiles active not be empty.");
		String appName = environment.getProperty("spring.application.name");
		Assert.hasText(appName, "Please check configure, spring application name not be empty.");
		String privateSalt = appName + active;
		return new SecureConfig(new String[] { "MD5", "SHA-256", "SHA-384", "SHA-512" }, privateSalt, 5, 2 * 60 * 60 * 1000L,
				3 * 60 * 1000L);
	}

}
