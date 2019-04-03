package com.wl4g.devops.srm.dao;

import org.elasticsearch.action.search.SearchRequest;

import java.io.Serializable;
import java.util.List;

public interface ElasticsearchBasedDao<T> {
	// 添加
	public void add(T t);

	// 删除
	public void delete(T t);

	// 更新
	public void update(T t);

	// 根据id查询
	public T findOne(Serializable id);

	// 查询所有
	public List<T> findAll(SearchRequest searchRequest) throws Exception;
}
