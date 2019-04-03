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
package com.wl4g.devops.scm.client.configure.refresh;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.scm.client.config.InstanceProperties;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.config.SpringContexts;
import com.wl4g.devops.scm.client.configure.RefreshBeanRegistry;

public class ConfigureBeanRefresher extends AbstractBeanRefresher {

	private SpringContexts context;

	public ConfigureBeanRefresher(String baseUri, RestTemplate restTemplate, RetryProperties retryProps,
			InstanceProperties intanceProps, ConfigurableEnvironment environment, RefreshBeanRegistry registry,
			SpringContexts context) {
		super(baseUri, restTemplate, retryProps, intanceProps, environment, registry);
		this.context = context;
	}

	@Override
	protected synchronized Object doRefreshToTarget(String beanId, Object bean) {
		// Re-initialization of solution to attribute injection.
		this.context.reinitializationBean(bean, beanId);
		return bean;
	}

}