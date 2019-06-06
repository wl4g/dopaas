/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.client.configure;

import com.wl4g.devops.common.bean.scm.model.GenericInfo;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.scm.client.config.InstanceInfo;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import static com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher.SCM_REFRESH_PROPERTY_SOURCE;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

/**
 * SCM application context initializer instructions.</br>
 * See:https://blog.csdn.net/leileibest_437147623/article/details/81074174
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月22日
 * @since
 */
@Order(0)
public class DefaultBootstrapPropertySourceLocator extends ScmPropertySourceLocator {

	final public static String devOpsPropertySource = "_devOpsPropertySource";

	public DefaultBootstrapPropertySourceLocator(ScmClientProperties config, RetryProperties retryConfig, InstanceInfo info) {
		super(config, retryConfig, info);
		//get token from server
		receiveToken();
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		if (log.isInfoEnabled()) {
			log.info("SCM bootstrap config is enabled for environment {}", environment);
		}

		/*
		 * Define composite property source. {@link
		 * com.wl4g.devops.scm.client.configure.refresh.AbstractBeanRefresher#
		 * addConfigToEnvironment()}
		 */
		CompositePropertySource composite = new CompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE); // By-default
		if (environment instanceof ConfigurableEnvironment) {
			try {
				// 1.1 Get remote latest property-sources(version/releaseId is
				// null).
				ReleaseMessage config = getRemoteReleaseConfig(new GenericInfo.ReleaseMeta());

				// 1.2 Resolves cipher resource.
				resolvesCipherSource(config);

				// 1.3 Add configuration to environment.
				composite = config.convertCompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE);

			} catch (Exception e) {
				log.warn("-----------------------------------------");
				log.warn("Could not locate remote PropertySource! {} causes by:{}", getRootCauseMessage(e));
				log.warn("-----------------------------------------");
			}
		}

		// When you refresh the configuration source, you need to clean it up.
		// See:refresh.ScmContextRefresher#addScmConfigToEnvironment()
		return composite;
	}

}