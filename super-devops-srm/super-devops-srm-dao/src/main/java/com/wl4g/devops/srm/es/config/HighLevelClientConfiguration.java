package com.wl4g.devops.srm.es.config;

import com.wl4g.devops.srm.es.RestHighLevelClient;
import com.wl4g.devops.srm.es.pool.ElasticsearchClientFactory;
import com.wl4g.devops.srm.es.pool.ElasticsearchClientPool;

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
			ElasticsearchClientPoolProperties esClientProperties) {
		return new ElasticsearchClientPool(esClientFactory, esClientProperties);
	}

	@Bean
	@ConditionalOnBean({ ElasticsearchClientPool.class })
	@ConditionalOnMissingBean(RestHighLevelClient.class)
	public RestHighLevelClient restHighLevelClient(ElasticsearchClientPool esClientPool) {
		return new RestHighLevelClient(esClientPool);
	}

}
