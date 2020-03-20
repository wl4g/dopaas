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
package com.wl4g.devops.coss.config;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.coss.CossEndpoint;
import com.wl4g.devops.coss.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.access.ConsoleCossAccessor;
import com.wl4g.devops.coss.access.CossAccessor;
import com.wl4g.devops.coss.access.HttpCossAccessor;
import com.wl4g.devops.coss.natives.MetadataIndexManager;
import com.wl4g.devops.coss.natives.NativeCossEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * COSS core auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月11日
 * @since
 */
@Configuration
public class CossAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {
	final public static String KEY_PROPERTY_PREFIX = "spring.cloud.devops.coss.native";

	@Bean
	@Validated
	@ConditionalOnProperty(name = KEY_PROPERTY_PREFIX + ".enable", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_PROPERTY_PREFIX)
	public NativeCossProperties nativeCossProperties() {
		return new NativeCossProperties();
	}

	@Bean
	@ConditionalOnBean(NativeCossProperties.class)
	public CossEndpoint nativeCossEndpoint(NativeCossProperties config) {
		return new NativeCossEndpoint(config);
	}

	@Bean
	public GenericOperatorAdapter<CossProvider, CossEndpoint> compositeCossEndpoint(List<CossEndpoint> endpoints) {
		return new GenericOperatorAdapter<CossProvider, CossEndpoint>(endpoints) {
		};
	}

	// --- A C C E S S O R'S. ---

	@Bean
	public CossAccessor consoleCossAccessor(GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter) {
		return new ConsoleCossAccessor(endpointAdapter);
	}

	@Bean
	public HttpCossAccessor httpCossAccessor(GenericOperatorAdapter<CossProvider, CossEndpoint> endpointAdapter) {
		return new HttpCossAccessor(endpointAdapter);
	}

	@Bean
	public PrefixHandlerMapping httpCossAccessorPrefixHandlerMapping(HttpCossAccessor httpCossAccessor) {
		return super.newPrefixHandlerMapping(HttpCossAccessor.URL_BASE, httpCossAccessor);
	}


	// --- MetadataIndexManager ---
	@Bean
	public MetadataIndexManager metadataIndexManager() {
		return new MetadataIndexManager();
	}

}
