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
