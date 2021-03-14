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
package com.wl4g.dopaas.cmdb.es.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticsearchClientProperties {
	public static final String PREFIX = "spring.es";

	private String[] hosts;
	private int port = 9200;
	private String schema = "http";
	private int connectTimeOut;
	private int socketTimeOut;
	private int connectionRequestTimeOut;
	private int maxConnectNum;
	private int maxConnectPerRoute;

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public int getSocketTimeOut() {
		return socketTimeOut;
	}

	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}

	public int getConnectionRequestTimeOut() {
		return connectionRequestTimeOut;
	}

	public void setConnectionRequestTimeOut(int connectionRequestTimeOut) {
		this.connectionRequestTimeOut = connectionRequestTimeOut;
	}

	public int getMaxConnectNum() {
		return maxConnectNum;
	}

	public void setMaxConnectNum(int maxConnectNum) {
		this.maxConnectNum = maxConnectNum;
	}

	public int getMaxConnectPerRoute() {
		return maxConnectPerRoute;
	}

	public void setMaxConnectPerRoute(int maxConnectPerRoute) {
		this.maxConnectPerRoute = maxConnectPerRoute;
	}

	/**
	 * Elasticsearch client pool properties
	 * 
	 * @author wanglsir@gmail.com, 983708408@qq.com
	 * @version 2019年12月31日 v1.0.0
	 * @see
	 */
	public static class ElasticsearchClientPoolProperties extends GenericObjectPoolConfig<RestHighLevelClient> {
		public static final String PREFIX = "spring.es.pool";

	}

}