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
package com.wl4g.devops.cmdb.es.handler;

import org.elasticsearch.action.search.SearchRequest;

import java.io.Serializable;
import java.util.List;

public interface ElasticsearchBasedHandler<T> {
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