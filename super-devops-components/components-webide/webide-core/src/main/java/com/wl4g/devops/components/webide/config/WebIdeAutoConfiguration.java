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
package com.wl4g.devops.components.webide.config;

import static com.wl4g.devops.components.webide.config.WebIdeProperties.*;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.components.webide.DefaultWebIdeCompleter;
import com.wl4g.devops.components.webide.WebIdeCompleter;
import com.wl4g.devops.components.webide.WebIdeEndpoint;

/**
 * WebIDE auto configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Configuration
public class WebIdeAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	@Bean
	@ConditionalOnProperty(name = KEY_WEBIDE_PREFIX + ".enable", matchIfMissing = true)
	public WebIdeProperties webIdeProperties() {
		return new WebIdeProperties();
	}

	@Bean
	@ConditionalOnBean(WebIdeProperties.class)
	@ConditionalOnMissingBean
	public WebIdeCompleter defaultWebIdeCompleter() {
		return new DefaultWebIdeCompleter();
	}

	@Bean
	@ConditionalOnBean(WebIdeProperties.class)
	public WebIdeEndpoint webIdeEndpoint(WebIdeProperties config) {
		return new WebIdeEndpoint(config);
	}

	@Bean
	@ConditionalOnBean(WebIdeEndpoint.class)
	public PrefixHandlerMapping webIdeEndpointPrefixHandlerMapping(WebIdeEndpoint webIde) {
		return super.newPrefixHandlerMapping(URI_WEBIDE_BASE, webIde);
	}

}