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
package com.wl4g.devops.uos.config;

import com.wl4g.components.core.config.mapping.AbstractHandlerMappingSupport;
import com.wl4g.components.core.config.mapping.PrefixHandlerMapping;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.uos.ServerCossEndpoint;
import com.wl4g.devops.uos.access.ConsoleCossAccessor;
import com.wl4g.devops.uos.access.HttpCossAccessor;
import com.wl4g.devops.uos.access.SftpCossAccessor;
import com.wl4g.devops.uos.common.CossEndpoint.CossProvider;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UOS API auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月11日
 * @since
 */
@Configuration
public class CossAccessAutoConfiguration extends AbstractHandlerMappingSupport {

	// --- A C C E S S O R _ E X P O R T'S. ---

	@Bean
	@ConfigurationProperties(prefix = CossAccessProperties.KEY_ACCESS_PREFIX)
	public CossAccessProperties uosAccessProperties() {
		return new CossAccessProperties();
	}

	@Bean
	public ConsoleCossAccessor consoleCossAccessor(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
		return new ConsoleCossAccessor(endpointAdapter);
	}

	@Bean
	public HttpCossAccessor httpCossAccessor(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
		return new HttpCossAccessor(endpointAdapter);
	}

	@Bean
	public SftpCossAccessor sftpCossAccessor(GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter) {
		return new SftpCossAccessor(endpointAdapter);
	}

	@Bean
	public PrefixHandlerMapping httpCossAccessorPrefixHandlerMapping(HttpCossAccessor httpCossAccessor) {
		return super.newPrefixHandlerMapping(HttpCossAccessor.URL_BASE, httpCossAccessor);
	}

}