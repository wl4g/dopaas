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
package com.wl4g.devops.scm.client.configure;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpServerErrorException;
import static org.springframework.util.StringUtils.*;

import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.scm.client.config.InstanceInfo;
import com.wl4g.devops.scm.client.config.RetryProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.enviroment.ScmEnvironment;
import com.wl4g.devops.scm.client.enviroment.ScmPropertySource;

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

	public DefaultBootstrapPropertySourceLocator(ScmClientProperties config, RetryProperties retryConfig, InstanceInfo info) {
		super(config, retryConfig, info);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PropertySource<?> locate(Environment environment) {
		if (log.isInfoEnabled()) {
			log.info("SCM bootstrap config is enabled for environment {}", environment);
		}

		CompositePropertySource composite = new CompositePropertySource("configService");
		ScmClientProperties cloneConfig = config.override(environment);

		Exception error = null;
		String errmsg = EMPTY;
		try {
			String[] labels = new String[] { "" };
			if (hasText(cloneConfig.getLabel())) {
				labels = commaDelimitedListToStringArray(cloneConfig.getLabel());
			}
			String state = ConfigClientStateHolder.getState();
			// Try all the labels until one works
			for (String label : labels) {
				ScmEnvironment result = pullRemoteEnvironment(restTemplate, cloneConfig, label.trim(), state);
				if (result != null) {
					printfLog(result);

					// result.getPropertySources() can be null if using xml
					if (result.getPropertySources() != null) {
						for (ScmPropertySource source : result.getPropertySources()) {
							Map<String, Object> map = (Map) source.getSource();
							composite.addPropertySource(new MapPropertySource(source.getName(), map));
						}
					}

					if (hasText(result.getState()) || hasText(result.getVersion())) {
						HashMap<String, Object> map = new HashMap<>();
						putPropertyValue(map, "config.client.state", result.getState());
						putPropertyValue(map, "config.client.version", result.getVersion());
						composite.addFirstPropertySource(new MapPropertySource("configClient", map));
					}
					return composite;
				}
			}
		} catch (HttpServerErrorException e) {
			error = e;
			if (MediaType.APPLICATION_JSON.includes(e.getResponseHeaders().getContentType())) {
				errmsg = e.getResponseBodyAsString();
			}
		} catch (Exception e) {
			error = e;
		}

		if (cloneConfig.isFailFast()) {
			throw new IllegalStateException(
					String.format("Could not locate PropertySource and the fail fast property is set, failing %s", errmsg),
					error);
		}
		log.warn("Could not locate PropertySource: "
				+ (errmsg == null ? error == null ? "label not found" : error.getMessage() : errmsg));
		return null;

	}

}