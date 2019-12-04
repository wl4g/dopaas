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
package com.wl4g.devops.scm.client.configure;

import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.scm.client.config.InstanceHolder;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import static com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher.SCM_REFRESH_PROPERTY_SOURCE;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

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

	public DefaultBootstrapPropertySourceLocator(ScmClientProperties config, InstanceHolder info) {
		super(config, info);
	}

	/**
	 * Composite property sources.</br>
	 * See:{@link ScmContextRefresher#addConfigToEnvironment}
	 */
	@Override
	public PropertySource<?> locate(Environment environment) {
		if (log.isInfoEnabled()) {
			log.info("SCM locate config is enabled environment for: {}", environment);
		}

		CompositePropertySource composite = new CompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE); // By-default
		if (environment instanceof ConfigurableEnvironment) {
			try {
				// Pull latest propertySources from server.
				ReleaseMessage config = fetchRemoteReleaseConfig();

				// Resolves cipher resource
				resolvesCipherSource(config);

				// Add configuration to environment
				composite = config.convertCompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE);

			} catch (Throwable th) {
				String errtip = "Could not locate remote propertySource! causes by: {}";
				if (log.isDebugEnabled()) {
					log.warn(errtip, getStackTrace(th));
				} else {
					log.warn(errtip, getRootCausesString(th));
				}
			}
		}

		return composite;
	}

}