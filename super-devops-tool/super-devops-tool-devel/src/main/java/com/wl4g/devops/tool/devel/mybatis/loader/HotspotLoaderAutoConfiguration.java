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
package com.wl4g.devops.tool.devel.mybatis.loader;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.tool.devel.mybatis.loader.SqlSessionMapperHotspotLoader.MapperHotspotLoaderProperties;
import static com.wl4g.devops.tool.devel.mybatis.loader.SqlSessionMapperHotspotLoader.MapperHotspotLoaderProperties.*;

/**
 * {@link SqlSessionMapperHotspotLoader} auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月14日
 * @since
 */
@Configuration
public class HotspotLoaderAutoConfiguration {

	@Bean
	@ConditionalOnProperty(name = CONF_P + ".enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = CONF_P)
	public MapperHotspotLoaderProperties mapperHotspotLoaderProperties() {
		return new MapperHotspotLoaderProperties();
	}

	@Bean
	@ConditionalOnBean({ MapperHotspotLoaderProperties.class })
	public SqlSessionMapperHotspotLoader sqlSessionMapperHotspotLoader(MapperHotspotLoaderProperties config,
			SqlSessionFactoryBean sessionFactory) {
		return new SqlSessionMapperHotspotLoader(sessionFactory, config);
	}

}
