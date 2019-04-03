package com.wl4g.devops.srm.dao;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.srm.es.RestHighLevelClient;
import com.wl4g.devops.srm.listener.Listener;
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

public class ElasticsearchSupportDao<T> implements ElasticsearchBasedDao<T> {

	@Resource
	private RestHighLevelClient restHighLevelClient;

	private Class<T> clazzP;

	@Resource
	private Listener listener;

	@SuppressWarnings("unchecked")
	public ElasticsearchSupportDao() {
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
