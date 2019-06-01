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

import java.lang.annotation.Annotation;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.scm.client.config.ScmBootstrapAutoConfiguration.*;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;
import com.wl4g.devops.common.bean.scm.model.BaseModel;
import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.scm.client.annotation.ScmClientController;
import com.wl4g.devops.scm.client.annotation.EnableScmWatchTask;
import com.wl4g.devops.scm.client.annotation.EnableScmWatchZk;
import com.wl4g.devops.scm.client.configure.BeanCurrentlyConfiguringInterceptor;
import com.wl4g.devops.scm.client.configure.DefaultRefreshBeanRegistry;
import com.wl4g.devops.scm.client.configure.RefreshBeanRegistry;
import com.wl4g.devops.scm.client.configure.ContainerContextBeanFactory;
import com.wl4g.devops.scm.client.configure.refresh.ConfigureBeanRefresher;
import com.wl4g.devops.scm.client.configure.watch.TimingRefreshWatcher;
import com.wl4g.devops.scm.client.configure.watch.ZookeeperRefreshWatcher;
import com.wl4g.devops.scm.common.utils.ScmUtils;

/**
 * SCM refresh bootstrap configure configuration.<br/>
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
public class RefresherAutoConfiguration extends AbstractOptionalControllerConfiguration {

	@Autowired
	private InstanceConfig instanceConfig;

	@Bean("taskRefreshWatcher")
	@EnableScmWatchTask
	public TimingRefreshWatcher taskRefreshWatcher() {
		return new TimingRefreshWatcher();
	}

	@Bean("zookeeperRefreshWatcher")
	@EnableScmWatchZk
	public ZookeeperRefreshWatcher zookeeperRefreshWatcher(CuratorFramework curator) {
		String path = ScmUtils.genZkConfigPath(
				new BaseModel(instanceConfig.getApplicationName(), instanceConfig.getProfilesActive()),
				instanceConfig.getBindInstance());
		return new ZookeeperRefreshWatcher(path, curator);
	}

	@Bean
	public ConfigureBeanRefresher configureBeanRefresher(@Value(BASE_URI) String baseUri, RestTemplate restTemplate,
			RetryProperties retryProps, InstanceConfig instanceProps, RefreshBeanRegistry registry,
			ContainerContextBeanFactory context, ConfigurableEnvironment environment) {
		return new ConfigureBeanRefresher(baseUri, restTemplate, retryProps, instanceProps, environment, registry, context);
	}

	@Bean
	public ContainerContextBeanFactory springContextHolder() {
		return new ContainerContextBeanFactory();
	}

	@Bean
	public DefaultRefreshBeanRegistry refreshBeanRegistry(ContainerContextBeanFactory context) {
		return new DefaultRefreshBeanRegistry(context);
	}

	@Bean
	public BeanCurrentlyConfiguringInterceptor beanCurrentlyConfigureAspect(ConfigureBeanRefresher refresher) {
		return new BeanCurrentlyConfiguringInterceptor(refresher);
	}

	@Bean
	public com.wl4g.devops.scm.client.web.ScmClientController scmClientController(ConfigureBeanRefresher refresher) {
		return new com.wl4g.devops.scm.client.web.ScmClientController(refresher);
	}

	@Bean
	public PrefixHandlerMapping scmClientPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Override
	protected String getMappingPrefix() {
		return URI_C_BASE;
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return ScmClientController.class;
	}

}