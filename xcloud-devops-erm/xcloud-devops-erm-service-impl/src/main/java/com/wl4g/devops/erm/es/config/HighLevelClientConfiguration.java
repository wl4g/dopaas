/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.erm.es.config;

import com.wl4g.devops.erm.es.EnhancedRestHighLevelClient;
import com.wl4g.devops.erm.es.config.ElasticsearchClientProperties.ElasticsearchClientPoolProperties;
import com.wl4g.devops.erm.es.pool.ElasticsearchClientFactory;
import com.wl4g.devops.erm.es.pool.ElasticsearchClientPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ ElasticsearchClientPool.class, GenericObjectPool.class,
		org.elasticsearch.client.RestHighLevelClient.class })
public class HighLevelClientConfiguration {

	@Bean
	@ConfigurationProperties(prefix = ElasticsearchClientProperties.PREFIX)
	@ConditionalOnProperty(prefix = ElasticsearchClientProperties.PREFIX, value = { "hosts" })
	@ConditionalOnMissingBean(ElasticsearchClientProperties.class)
	public ElasticsearchClientProperties elasticsearchClientProperties() {
		return new ElasticsearchClientProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = ElasticsearchClientPoolProperties.PREFIX)
	@ConditionalOnMissingBean(ElasticsearchClientPoolProperties.class)
	public ElasticsearchClientPoolProperties elasticsearchClientPoolProperties() {
		ElasticsearchClientPoolProperties elasticsearchClientPoolConfigure = new ElasticsearchClientPoolProperties();
		// 开启jmx 导致 springboot（version:2.0.1，内嵌tomcat，和euraka集成），启动后(erueka
		// 注册日志:registration status: 404)立即关闭servlet 容器，暂时默认设置jmx为关闭状态，再寻找原因
		elasticsearchClientPoolConfigure.setJmxEnabled(false);

		return elasticsearchClientPoolConfigure;
	}

	@Bean
	@ConditionalOnBean(ElasticsearchClientProperties.class)
	@ConditionalOnSingleCandidate(ElasticsearchClientProperties.class)
	@ConditionalOnMissingBean(ElasticsearchClientFactory.class)
	public ElasticsearchClientFactory elasticsearchClientFactory(
			@Autowired ElasticsearchClientProperties elasticsearchClientConfigure) {
		return new ElasticsearchClientFactory(elasticsearchClientConfigure);
	}

	@Bean
	@ConditionalOnBean({ ElasticsearchClientFactory.class, ElasticsearchClientPoolProperties.class })
	@ConditionalOnMissingBean(ElasticsearchClientPool.class)
	public ElasticsearchClientPool remoteExecConnectionPool(ElasticsearchClientFactory esClientFactory,
			ElasticsearchClientPoolProperties config) {
		return new ElasticsearchClientPool(esClientFactory, config);
	}

	@Bean
	@ConditionalOnBean({ ElasticsearchClientPool.class })
	@ConditionalOnMissingBean(EnhancedRestHighLevelClient.class)
	public EnhancedRestHighLevelClient restHighLevelClient(ElasticsearchClientPool esClientPool) {
		return new EnhancedRestHighLevelClient(esClientPool);
	}

}