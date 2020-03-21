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
package com.wl4g.devops.rcm.config;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.rcm.access.ConsoleRcmAccessor;
import com.wl4g.devops.rcm.access.RcmAccessor;
import com.wl4g.devops.rcm.access.HttpRcmAccessor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * COSS API auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月11日
 * @since
 */
@Configuration
public class RcmAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	// --- A C C E S S O R _ E X P O R T'S. ---

	@Bean
	public RcmAccessor consoleCossAccessor() {
		return new ConsoleRcmAccessor();
	}

	@Bean
	public HttpRcmAccessor httpCossAccessor() {
		return new HttpRcmAccessor();
	}

	@Bean
	public PrefixHandlerMapping httpCossAccessorPrefixHandlerMapping(HttpRcmAccessor httpCossAccessor) {
		return super.newPrefixHandlerMapping(HttpRcmAccessor.URL_BASE, httpCossAccessor);
	}

}
