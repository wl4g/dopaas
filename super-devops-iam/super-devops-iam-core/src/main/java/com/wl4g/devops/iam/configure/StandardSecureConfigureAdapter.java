/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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