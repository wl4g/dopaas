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
package com.wl4g.devops.iam.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.kit.access.IPAccessControl;
import com.wl4g.devops.common.kit.access.IPAccessControl.IPAccessProperties;

/**
 * IP access configuration processor.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月24日
 * @since
 */
@Configuration
public class AccessControlConfiguration {
	final static String IP_ACCESS_PREFIX = "spring.cloud.devops.iam.acl";

	@Bean
	public IPAccessControl ipAccessControl(IPAccessProperties properties) {
		return new IPAccessControl(properties);
	}

	@Bean
	@ConfigurationProperties(prefix = IP_ACCESS_PREFIX)
	public IPAccessProperties ipAccessProperties() {
		return new IPAccessProperties();
	}

}