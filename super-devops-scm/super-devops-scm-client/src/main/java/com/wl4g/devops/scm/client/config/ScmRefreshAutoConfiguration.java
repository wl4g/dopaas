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
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.*;
import com.wl4g.devops.common.bean.scm.model.GenericInfo;
import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.scm.annotation.ScmEndpoint;
import com.wl4g.devops.scm.client.annotation.EnableScmWatchTask;
import com.wl4g.devops.scm.client.annotation.EnableScmWatchZk;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;
import com.wl4g.devops.scm.client.configure.refresh.ScmLoggingRebinder;
import com.wl4g.devops.scm.client.configure.watch.TimingRefreshWatcher;
import com.wl4g.devops.scm.client.configure.watch.ZookeeperRefreshWatcher;
import com.wl4g.devops.scm.common.utils.ScmUtils;

/**
 * SCM refresh auto configuration.</br>
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
public class ScmRefreshAutoConfiguration extends AbstractOptionalControllerConfiguration {

	//
	// Refresher's
	//

	/**
	 * See:{@link RefreshAutoConfiguration#contextRefresher()}
	 * 
	 * @param context
	 * @param scope
	 * @return
	 */
	@Bean
	public ContextRefresher contextRefresher(ConfigurableApplicationContext context, RefreshScope scope) {
		return new ScmContextRefresher(context, scope);
	}

	/**
	 * See:{@link RefreshAutoConfiguration#loggingRebinder()}
	 * 
	 * @param context
	 * @param scope
	 * @return
	 */
	@Bean
	public LoggingRebinder loggingRebinder() {
		return new ScmLoggingRebinder();
	}

	@Bean("taskRefreshWatcher")
	@EnableScmWatchTask
	public TimingRefreshWatcher timingRefreshWatcher(ScmContextRefresher refresher) {
		return new TimingRefreshWatcher(refresher);
	}

	@Bean("zookeeperRefreshWatcher")
	@EnableScmWatchZk
	public ZookeeperRefreshWatcher zookeeperRefreshWatcher(CuratorFramework curator, ScmContextRefresher refresher,
			InstanceInfo config) {
		String path = ScmUtils.genZkConfigPath(new GenericInfo(config.getAppName(), config.getProfilesActive()),
				config.getBindInstance());
		return new ZookeeperRefreshWatcher(refresher, path, curator);
	}

	//
	// Endpoint's
	//

	@Bean
	public com.wl4g.devops.scm.client.endpoint.ScmClientEndpoint scmClientController(ScmContextRefresher refresher) {
		return new com.wl4g.devops.scm.client.endpoint.ScmClientEndpoint(refresher);
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
		return ScmEndpoint.class;
	}

}