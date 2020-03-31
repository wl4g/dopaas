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
package com.wl4g.devops.support.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.annotation.conditional.ConditionalOnJdwpDebug;
import com.wl4g.devops.support.mybatis.loader.SqlSessionMapperHotspotLoader;
import com.wl4g.devops.support.mybatis.loader.SqlSessionMapperHotspotLoader.HotspotLoadProperties;

/**
 * {@link SqlSessionMapperHotspotLoader} auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月14日
 * @since
 */
@Configuration
public class SqlSessionHotspotAutoConfiguration {
	final public static String CONF_P = "spring.cloud.devops.support.devel.mybatis-loader";

	@Bean
	@ConditionalOnJdwpDebug(enableProperty = CONF_P + ".enable")
	@ConfigurationProperties(prefix = CONF_P)
	@ConditionalOnBean(SqlSessionFactoryBean.class)
	public HotspotLoadProperties hotspotLoaderProperties() {
		return new HotspotLoadProperties();
	}

	@Bean
	@ConditionalOnBean(value = { HotspotLoadProperties.class })
	public SqlSessionMapperHotspotLoader sqlSessionMapperHotspotLoader(SqlSessionFactoryBean sessionFactory,
			HotspotLoadProperties config) {
		return new SqlSessionMapperHotspotLoader(sessionFactory, config);
	}

}