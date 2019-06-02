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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.bean.scm.model.ReleaseModel;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseMeta;
import com.wl4g.devops.scm.client.config.InstanceConfig;
import com.wl4g.devops.scm.client.config.RetryProperties;

/**
 * ApplicationContextInitializer instructions see:
 * https://blog.csdn.net/leileibest_437147623/article/details/81074174
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月22日
 * @since
 */
public class ScmBootstrapPropertySourceLocator implements PropertySourceLocator {

	final public static String SCM_REFRESH_PROPERTY_SOURCE = "_DevOpsScmPropertySource_";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	final private AbstractBeanRefresher refresher;

	public ScmBootstrapPropertySourceLocator(String baseUri, RestTemplate restTemplate, InstanceConfig instanceProps,
			ConfigurableEnvironment environment, RetryProperties retryProps) {
		super();
		this.refresher = new ConfigureBeanRefresher(baseUri, restTemplate, retryProps, instanceProps, environment, null, null);
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		if (log.isInfoEnabled()) {
			log.info("SCM bootstrap config is enabled for environment {}", environment);
		}

		/*
		 * Define composite property source.
		 * See:configure.refresh.AbstractBeanRefresher#addConfigToEnvironment()
		 */
		CompositePropertySource composite = new CompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE); // By-default
		if (environment instanceof ConfigurableEnvironment) {
			try {
				// 1.1 Get remote latest property-sources(version/releaseId is
				// null).
				ReleaseModel config = refresher.getRemoteReleaseConfig(new ReleaseMeta());

				// 1.2 Resolves cipher resource.
				refresher.resolvesCipherSource(config);

				// 1.3 Add configuration to environment.
				composite = config.convertCompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE);

			} catch (Exception e) {
				log.error("SCM bootstrap config refresh failed. cause by: {}", ExceptionUtils.getRootCauseMessage(e));
			}
		}

		// When you refresh the configuration source, you need to clean it up.
		// See:refresh.AbstractBeanRefresher.addConfigToEnvironment()
		return composite;
	}

}