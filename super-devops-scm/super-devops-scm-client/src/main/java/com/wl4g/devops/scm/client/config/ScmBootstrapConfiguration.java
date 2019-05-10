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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.scm.client.annotation.EnabledScmClient;
import com.wl4g.devops.scm.client.configure.refresh.ScmPropertySourceLocator;

/**
 * DevOps bootstrap configure configuration.<br/>
 * <br/>
 * Spring Cloud loads bootstrap.yml preferentially, which means that other
 * configurationfiles are not # loaded at initialization, so configurations
 * other than bootstrap.yml cannot be used at initialization.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月8日
 * @since {@link de.codecentric.boot.admin.web.PrefixHandlerMapping}
 *        {@link de.codecentric.boot.admin.config.AdminServerWebConfiguration}}
 */
@EnabledScmClient
public class ScmBootstrapConfiguration {
	final public static String BASE_URI = "${spring.cloud.devops.scm.client.base-uri:http://localhost:6400/devops}";

	@Bean
	@ConditionalOnMissingBean
	public ClientHttpRequestFactory netty4ClientHttpRequestFactory() {
		// SimpleClientHttpRequestFactory
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		factory.setReadTimeout(5000);
		factory.setConnectTimeout(5000);
		factory.setMaxResponseSize(1024 * 1024 * 10);
		return factory;
	}

	@Bean
	@ConditionalOnMissingBean
	public RestTemplate devopsRestTemplate(ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	public ScmPropertySourceLocator devOpsPropertySourceLocator(@Value(BASE_URI) String baseUri, RestTemplate restTemplate,
			RetryProperties retryProps, InstanceProperties instanceProps, Environment environment) {
		return new ScmPropertySourceLocator(baseUri, restTemplate, instanceProps, (ConfigurableEnvironment) environment,
				retryProps);
	}

	@Bean
	public InstanceProperties instanceProperties(Environment environment) {
		return new InstanceProperties(environment);
	}

	@Bean
	public RetryProperties retryProperties() {
		return new RetryProperties();
	}

}