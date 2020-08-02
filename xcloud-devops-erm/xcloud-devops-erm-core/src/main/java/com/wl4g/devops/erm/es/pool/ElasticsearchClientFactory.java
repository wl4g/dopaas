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
package com.wl4g.devops.erm.es.pool;

import com.wl4g.devops.erm.es.config.ElasticsearchClientProperties;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ElasticsearchClientFactory implements PooledObjectFactory<RestHighLevelClient> {
	private Logger log = LoggerFactory.getLogger(getClass());

	private ElasticsearchClientProperties elasticsearchClientConfigure;

	public ElasticsearchClientFactory(ElasticsearchClientProperties elasticsearchClientConfigure) {
		this.elasticsearchClientConfigure = elasticsearchClientConfigure;
	}

	@Override
	public PooledObject<RestHighLevelClient> makeObject() throws Exception {
		Set<String> hostSet = new HashSet<String>(
				this.elasticsearchClientConfigure.getHosts().length + this.elasticsearchClientConfigure.getHosts().length / 3);
		for (String h : this.elasticsearchClientConfigure.getHosts()) {
			hostSet.add(h);
		}
		HttpHost[] httpHosts = hostSet.stream()
				.map(host -> new HttpHost(host, elasticsearchClientConfigure.getPort(), elasticsearchClientConfigure.getSchema()))
				.toArray(len -> new HttpHost[len]);
		RestClientBuilder clientBuilder = RestClient.builder(httpHosts);
		RestHighLevelClient client = new RestHighLevelClient(clientBuilder);
		return new DefaultPooledObject<RestHighLevelClient>(client);
	}

	@Override
	public void destroyObject(PooledObject<RestHighLevelClient> p) throws Exception {
		if (p.getObject() != null) {
			try {
				if (p.getObject().ping()) {
					p.getObject().close();
				}
			} catch (IOException e) {
				log.debug("es http client close exception:{}", e.getMessage());
			}
		}

	}

	@Override
	public boolean validateObject(PooledObject<RestHighLevelClient> p) {
		try {
			if (p.getObject() != null && p.getObject().ping()) {
				return true;
			}
		} catch (IOException e) {
			log.debug("es http client ping exception:{}", e.getMessage());
		}
		return false;
	}

	@Override
	public void activateObject(PooledObject<RestHighLevelClient> p) throws Exception {
		boolean result = false;
		try {
			result = p.getObject().ping();
		} catch (IOException e) {
			log.debug("http pool active client ,ping result :{}", result);
		}

	}

	@Override
	public void passivateObject(PooledObject<RestHighLevelClient> p) throws Exception {
		// nothing
	}

}