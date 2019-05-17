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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.wl4g.devops.common.bean.scm.model.BaseModel;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;
import com.wl4g.devops.scm.client.annotation.ScmController;
import com.wl4g.devops.scm.client.annotation.EnabledScmClient;
import com.wl4g.devops.scm.client.annotation.EnabledScmWatchTask;
import com.wl4g.devops.scm.client.annotation.EnabledScmWatchZk;
import com.wl4g.devops.scm.client.configure.BeanCurrentlyConfigureAspect;
import com.wl4g.devops.scm.client.configure.DefaultRefreshBeanRegistry;
import com.wl4g.devops.scm.client.configure.RefreshBeanRegistry;
import com.wl4g.devops.scm.client.configure.refresh.ConfigureBeanRefresher;
import com.wl4g.devops.scm.client.configure.watch.TaskRefreshWatcher;
import com.wl4g.devops.scm.client.configure.watch.ZookeeperRefreshWatcher;
import com.wl4g.devops.scm.client.web.SCMClientController;
import com.wl4g.devops.scm.common.utils.ScmUtils;

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
@Configuration
@EnabledScmClient
public class RefresherCoreConfiguration {

	@Autowired
	private InstanceProperties instanceProperties;

	@Bean("taskRefreshWatcher")
	@EnabledScmWatchTask
	public TaskRefreshWatcher taskRefreshWatcher() {
		return new TaskRefreshWatcher();
	}

	@Bean("zookeeperRefreshWatcher")
	@EnabledScmWatchZk
	public ZookeeperRefreshWatcher zookeeperRefreshWatcher(CuratorFramework curator) {
		String path = ScmUtils.genZkConfigPath(
				new BaseModel(instanceProperties.getApplicationName(), instanceProperties.getProfilesActive()),
				instanceProperties.getBindInstance());
		return new ZookeeperRefreshWatcher(path, curator);
	}

	@Bean
	public ConfigureBeanRefresher configureBeanRefresher(@Value(ScmBootstrapConfiguration.BASE_URI) String baseUri,
			RestTemplate restTemplate, RetryProperties retryProps, InstanceProperties instanceProps, RefreshBeanRegistry registry,
			SpringContexts context, ConfigurableEnvironment environment) {
		return new ConfigureBeanRefresher(baseUri, restTemplate, retryProps, instanceProps, environment, registry, context);
	}

	@Bean
	public SpringContexts springContexts() {
		return new SpringContexts();
	}

	@Bean
	public DefaultRefreshBeanRegistry defaultRefreshBeanRegistry(SpringContexts context) {
		return new DefaultRefreshBeanRegistry(context);
	}

	@Bean
	public PrefixHandlerMapping prefixHandlerMapping(SpringContexts context) {
		Map<String, Object> beans = context.getApplicationContext().getBeansWithAnnotation(ScmController.class);
		PrefixHandlerMapping mapping = new PrefixHandlerMapping(beans.values().toArray(new Object[beans.size()]));
		mapping.setPrefix(SCMDevOpsConstants.URI_C_BASE);
		return mapping;
	}

	@Bean
	public SCMClientController scmClientController(ConfigureBeanRefresher refresher) {
		return new SCMClientController(refresher);
	}

	@Bean
	public BeanCurrentlyConfigureAspect beanCurrentlyConfigureAspect(ConfigureBeanRefresher refresher) {
		return new BeanCurrentlyConfigureAspect(refresher);
	}

	/**
	 * {@link HandlerMapping} to map {@code @RequestMapping} on objects and
	 * prefixes them. The semantics of {@code @RequestMapping} should be
	 * identical to a normal {@code @Controller}, but the Objects should not be
	 * annotated as {@code @Controller} (otherwise they will be mapped by the
	 * normal MVC mechanisms).
	 *
	 * @author Johannes Edmeier
	 */
	public static class PrefixHandlerMapping extends RequestMappingHandlerMapping {
		private String prefix = "";
		private final Object handlers[];

		public PrefixHandlerMapping(Object... handlers) {
			this.handlers = handlers.clone();
			setOrder(-50);
		}

		@Override
		public void afterPropertiesSet() {
			super.afterPropertiesSet();
			for (Object handler : handlers) {
				detectHandlerMethods(handler);
			}
		}

		@Override
		protected boolean isHandler(Class<?> beanType) {
			return false;
		}

		@Override
		protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
			if (mapping == null) {
				return;
			}
			super.registerHandlerMethod(handler, method, withPrefix(mapping));
		}

		private RequestMappingInfo withPrefix(RequestMappingInfo mapping) {
			List<String> newPatterns = getPatterns(mapping);

			PatternsRequestCondition patterns = new PatternsRequestCondition(newPatterns.toArray(new String[newPatterns.size()]));
			return new RequestMappingInfo(patterns, mapping.getMethodsCondition(), mapping.getParamsCondition(),
					mapping.getHeadersCondition(), mapping.getConsumesCondition(), mapping.getProducesCondition(),
					mapping.getCustomCondition());
		}

		private List<String> getPatterns(RequestMappingInfo mapping) {
			List<String> newPatterns = new ArrayList<String>(mapping.getPatternsCondition().getPatterns().size());
			for (String pattern : mapping.getPatternsCondition().getPatterns()) {
				newPatterns.add(prefix + pattern);
			}
			return newPatterns;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}

	}

}