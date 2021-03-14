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
package com.wl4g.dopaas.cmdb.es.handler;

import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.dopaas.cmdb.es.EnhancedRestHighLevelClient;
import com.wl4g.dopaas.cmdb.es.listener.Listener;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.core.ResolvableType;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElasticsearchSupportHandler<T> implements ElasticsearchBasedHandler<T> {

	@Resource
	private EnhancedRestHighLevelClient restHighLevelClient;

	private Class<T> clazzP;

	@Resource
	private Listener listener;

	@SuppressWarnings("unchecked")
	public ElasticsearchSupportHandler() {
		ResolvableType resolveType = ResolvableType.forClass(getClass());
		this.clazzP = (Class<T>) resolveType.getSuperType().getGeneric(0).resolve();
	}

	@Override
	public void add(T t) {

	}

	@Override
	public void delete(T t) {

	}

	@Override
	public void update(T t) {

	}

	@Override
	public T findOne(Serializable id) {
		return null;
	}

	@Override
	public List<T> findAll(SearchRequest searchRequest) throws Exception {
		SearchResponse searchResp = this.restHighLevelClient.search(searchRequest);
		for (ShardSearchFailure failure : searchResp.getShardFailures()) {
			listener.onFailure(failure);
		}
		SearchHits hits = searchResp.getHits();
		SearchHit[] searchHits = hits.getHits();
		List<T> list = new ArrayList<>();
		for (SearchHit hit : searchHits) {
			String sourceAsString = hit.getSourceAsString();
			T t = JacksonUtils.parseJSON(sourceAsString, clazzP);
			list.add(t);
		}
		Collections.reverse(list);
		return list;
	}

}