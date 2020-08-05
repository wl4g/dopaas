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
package com.wl4g.devops.umc.watch;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.components.core.constants.UMCDevOpsConstants.KEY_CACHE_FETCH_META;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.umc.config.WatchProperties;
import com.wl4g.devops.umc.fetch.IndicatorsMetaFetcher;
import com.wl4g.devops.umc.fetch.IndicatorsMetaInfo;

/**
 * Indicators metric state watcher of based.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public abstract class IndicatorsStateWatcher implements DataflowJob<IndicatorsMetaInfo> {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected JedisService jedisService;

	@Autowired
	protected WatchProperties config;

	@Autowired
	protected IndicatorsMetaFetcher fetcher;

	@Override
	public List<IndicatorsMetaInfo> fetchData(ShardingContext sctx) {
		if (log.isInfoEnabled()) {
			log.info("Fetch indicators meta sharding for - {}", toJSONString(sctx));
		}

		List<IndicatorsMetaInfo> dataset = null;
		try {
			// Fetch indicators meta info.
			if (config.getFetchCacheSec() > 0) {
				// Fetch from cache.
				dataset = fetchShardingCache(sctx);
				if (isEmpty(dataset)) {
					dataset = fetcher.fetch(sctx); // Fetch DB
				}
				// Store to cache.
				store2CacheIfNecessary(sctx, dataset);
			} else {
				dataset = fetcher.fetch(sctx); // Fetch DB
			}
		} catch (Exception e) {
			log.error("Failed to fetch sharding indicators meta", e);
		}

		if (log.isInfoEnabled()) {
			log.info("Fetch indicators meta for - size({})", (dataset != null ? dataset.size() : 0));
		}
		if (log.isDebugEnabled()) {
			log.debug("Fetch indicators meta for - {}", dataset);
		}

		// Terminate the current schedule if the return data set is empty.
		return dataset;
	}

	@Override
	public void processData(ShardingContext sctx, List<IndicatorsMetaInfo> data) {
		doWatching(data);
	}

	/**
	 * Fetch meta info from cache.</br>
	 * <font color=red>Note: Can't use caching? Because data changes in real
	 * time, it is not possible to cache data through fragmented indexing,
	 * Therefore, it may lead to dirty reading and hallucination of data. It is
	 * suggested that the cache time should not be set too long.</font>
	 */
	@SuppressWarnings("unchecked")
	protected List<IndicatorsMetaInfo> fetchShardingCache(ShardingContext sctx) {
		return jedisService.getObjectT(getShardingCacheKey(sctx), ArrayList.class);
	}

	/**
	 * Store sharding meta info to cache.
	 * 
	 * @param sctx
	 * @param dataset
	 */
	protected void store2CacheIfNecessary(ShardingContext sctx, List<IndicatorsMetaInfo> dataset) {
		if (isEmpty(dataset)) {
			return;
		}

		jedisService.setObjectT(getShardingCacheKey(sctx), toJSONString(dataset), config.getFetchCacheSec());
	}

	/**
	 * Get sharding cache key.
	 * 
	 * @param sctx
	 * @return
	 */
	protected String getShardingCacheKey(ShardingContext sctx) {
		return KEY_CACHE_FETCH_META + sctx.getShardingItem();
	}

	/**
	 * Data-set processing.
	 * 
	 * @param dataset
	 */
	protected abstract void doWatching(List<IndicatorsMetaInfo> dataset);

}