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
package com.wl4g.devops.scm.client.locator;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.scm.client.GenericScmClient;
import com.wl4g.devops.scm.client.ScmClient;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.repository.RefreshConfigRepository;
import com.wl4g.devops.scm.client.utils.NodeHolder;
import com.wl4g.devops.scm.client.watch.GenericRefreshWatcher;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo.IniPropertySource;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo.PlaintextPropertySource;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.scm.client.refresh.ScmContextRefresher.SCM_REFRESH_PROPERTY_SOURCE;
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
public class BootstrapScmPropertySourceLocator implements PropertySourceLocator {

	final protected SmartLogger log = getLogger(getClass());

	/** {@link ScmClient} */
	protected final ScmClient client;

	public BootstrapScmPropertySourceLocator(ScmClient client) {
		notNullOf(client, "scmClient");
		this.client = client;
	}

	/**
	 * Composite property sources.</br>
	 * See:{@link ScmContextRefresher#addConfigToEnvironment}
	 */
	@Override
	public PropertySource<?> locate(Environment environment) {
		log.info("SCM locate config is enabled environment for: {}", environment);

		CompositePropertySource composite = new CompositePropertySource(SCM_REFRESH_PROPERTY_SOURCE); // By-default
		if (environment instanceof ConfigurableEnvironment) {
			try {
				// Gets current refresh config source
				ReleaseConfigInfo source = getRefreshRepository().getCurrentReleaseConfig();

				// Conversion configuration to spring property source.
				composite = convertToCompositePropertySource(source, SCM_REFRESH_PROPERTY_SOURCE);

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

	/**
	 * Gets {@link RefreshConfigRepository}
	 * 
	 * @return
	 */
	protected RefreshConfigRepository getRefreshRepository() {
		return ((GenericRefreshWatcher) ((GenericScmClient) client).getWatcher()).getRepository();
	}

	/**
	 * Conversion {@link ReleaseConfigInfo} to spring
	 * {@link CompositePropertySource}
	 * 
	 * @param source
	 * @param compositeSourceName
	 * @return
	 */
	protected CompositePropertySource convertToCompositePropertySource(ReleaseConfigInfo source, String compositeSourceName) {
		CompositePropertySource composite = new CompositePropertySource(compositeSourceName);
		for (PlaintextPropertySource ps : source.getReleaseSources()) {
			// See:org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration
			composite.addFirstPropertySource(ps.convertMapPropertySource());
		}
		return composite;
	}

}