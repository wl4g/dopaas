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

import com.wl4g.devops.uos.minio.MinioEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Default uos implements auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月11日
 * @since
 */
@Configuration
public class MinioCossAutoConfiguration {

	final public static String KEY_PROPERTY_PREFIX_MINIO = "spring.cloud.devops.uos.minio";


	@Bean
	@Validated
	@ConditionalOnProperty(name = KEY_PROPERTY_PREFIX_MINIO + ".enable", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_PROPERTY_PREFIX_MINIO)
	public MinioFsCossProperties minioFsCossProperties() {
		return new MinioFsCossProperties();
	}

	@Bean
	@ConditionalOnBean(MinioFsCossProperties.class)
	public MinioEndpoint minioEndpoint(MinioFsCossProperties config) {
		return new MinioEndpoint(config);
	}




}