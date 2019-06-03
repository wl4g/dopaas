/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.wl4g.devops.scm.client.configure.DefaultBootstrapPropertySourceLocator;
import com.wl4g.devops.scm.client.configure.ScmPropertySourceLocator;

/**
 * SCM bootstrap configuration.</br>
 * Note: Spring Cloud loads bootstrap.yml preferentially, which means that other
 * configurationfiles are not # loaded at initialization, so configurations
 * other than bootstrap.yml cannot be used at initialization.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月8日
 * @since {@link de.codecentric.boot.admin.web.PrefixHandlerMapping}
 *        {@link de.codecentric.boot.admin.config.AdminServerWebConfiguration}}
 */
public class ScmBootstrapConfiguration {

	final public static String BASE_URI = "${spring.cloud.devops.scm.client.base-uri:http://localhost:6400/scm}";

	//
	// SCM foundation's
	//

	@Bean
	public InstanceInfo instanceConfig(Environment environment) {
		return new InstanceInfo(environment);
	}

	@Bean
	public ScmClientProperties scmClientProperties(Environment environment) {
		return new ScmClientProperties(environment);
	}

	@Bean
	public ScmPropertySourceLocator scmPropertySourceLocator(ScmClientProperties config, RetryProperties retryConfig,
			InstanceInfo info) {
		return new DefaultBootstrapPropertySourceLocator(config, retryConfig, info);
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.cloud.devops.scm.client.retry")
	public RetryProperties retryProperties() {
		return new RetryProperties();
	}

}