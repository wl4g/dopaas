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
package com.wl4g.devops.coss.aws.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.coss.aws.S3CossEndpoint;

@Configuration
public class S3CossAutoConfiguration {
	final public static String KEY_PROPERTY_PREFIX = "spring.cloud.devops.coss.s3";

	@Bean
	@ConditionalOnProperty(name = KEY_PROPERTY_PREFIX + ".enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_PROPERTY_PREFIX)
	public S3CossProperties s3CossProperties() {
		return new S3CossProperties();
	}

	@Bean
	@ConditionalOnBean(S3CossProperties.class)
	public S3CossEndpoint s3CossEndpoint(S3CossProperties config) {
		return new S3CossEndpoint(config);
	}

}
