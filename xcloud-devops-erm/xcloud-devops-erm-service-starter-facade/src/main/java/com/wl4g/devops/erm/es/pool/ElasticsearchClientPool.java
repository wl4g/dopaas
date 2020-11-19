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
package com.wl4g.devops.erm.es.pool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;

public class ElasticsearchClientPool extends GenericObjectPool<RestHighLevelClient> implements DisposableBean {

	public ElasticsearchClientPool(PooledObjectFactory<RestHighLevelClient> factory) {
		super(factory);
	}

	public ElasticsearchClientPool(PooledObjectFactory<RestHighLevelClient> factory,
			GenericObjectPoolConfig<RestHighLevelClient> config) {
		super(factory, config);
	}

	public ElasticsearchClientPool(PooledObjectFactory<RestHighLevelClient> factory,
			GenericObjectPoolConfig<RestHighLevelClient> config, AbandonedConfig abandonedConfig) {
		super(factory, config, abandonedConfig);
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

}