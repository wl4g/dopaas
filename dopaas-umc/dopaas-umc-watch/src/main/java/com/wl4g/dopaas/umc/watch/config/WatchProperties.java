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
package com.wl4g.dopaas.umc.watch.config;

import com.wl4g.infra.common.task.RunnerProperties;

/**
 * Watch properties .
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2017年11月16日
 * @since
 */
public class WatchProperties extends RunnerProperties {
	private static final long serialVersionUID = 5508561234067054195L;

	private String zkServers = "localhost:2181";

	private String namespace = "umcElasticJobNs";

	private String cron = "0/30 * * * * ?";

	private int totalCount = 1;

	private String itemParams;

	private int fetchCacheSec = -1;

	public String getZkServers() {
		return zkServers;
	}

	public void setZkServers(String zkServers) {
		this.zkServers = zkServers;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getItemParams() {
		return itemParams;
	}

	public void setItemParams(String itemParams) {
		this.itemParams = itemParams;
	}

	/**
	 * Note: <font color=red>Note: Can't use caching? Because data changes in
	 * real time, it is not possible to cache data through fragmented indexing,
	 * Therefore, it may lead to dirty reading and hallucination of data. It is
	 * suggested that the cache time should not be set too long.</font> </br>
	 * {@link com.wl4g.dopaas.umc.watch.IndicatorsStateWatcher#fetchShardingCache}
	 * 
	 * @return
	 */
	public int getFetchCacheSec() {
		return fetchCacheSec;
	}

	public void setFetchCacheSec(int fetchCacheTime) {
		this.fetchCacheSec = fetchCacheTime;
	}

}