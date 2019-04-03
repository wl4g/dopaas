package com.wl4g.devops.srm.es.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;

public class ElasticsearchClientPool extends GenericObjectPool<RestHighLevelClient> implements DisposableBean {

	public ElasticsearchClientPool(PooledObjectFactory factory) {
		super(factory);
	}

	public ElasticsearchClientPool(PooledObjectFactory factory, GenericObjectPoolConfig config) {
		super(factory, config);
	}

	public ElasticsearchClientPool(PooledObjectFactory factory, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
		super(factory, config, abandonedConfig);
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

}
