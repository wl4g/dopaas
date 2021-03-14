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
package com.wl4g.paas.umc.opentsdb.client.http.callback;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.paas.umc.opentsdb.client.bean.request.Query;
import com.wl4g.paas.umc.opentsdb.client.bean.response.QueryResult;
import com.wl4g.paas.umc.opentsdb.client.common.Json;
import com.wl4g.paas.umc.opentsdb.client.exception.OpenTSDBHttpException;
import com.wl4g.paas.umc.opentsdb.client.util.ResponseUtil;

/**
 * 异步查询回调
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午4:14
 * @Version: 1.0
 */

public class QueryHttpResponseCallback implements FutureCallback<HttpResponse> {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private final QueryCallback callback;

	private final Query query;

	public QueryHttpResponseCallback(QueryCallback callback, Query query) {
		this.callback = callback;
		this.query = query;
	}

	@Override
	public void completed(HttpResponse response) {
		try {
			List<QueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class, QueryResult.class);
			log.debug("请求成功");
			this.callback.response(query, results);
		} catch (IOException e) {
			e.printStackTrace();
			this.callback.failed(query, e);
		} catch (OpenTSDBHttpException e) {
			log.error("请求失败，query:{},error:{}", query, e.getMessage());
			e.printStackTrace();
			this.callback.responseError(query, e);
		}
	}

	@Override
	public void failed(Exception e) {
		log.error("请求失败，query:{},error:{}", query, e.getMessage());
		this.callback.failed(query, e);
	}

	@Override
	public void cancelled() {

	}

	/***
	 * 定义查询callback，需要用户自己实现逻辑
	 */
	public interface QueryCallback {

		/***
		 * 在请求完成并且response code成功时回调
		 * 
		 * @param query
		 *            查询对象
		 * @param queryResults
		 *            查询结果
		 */
		void response(Query query, List<QueryResult> queryResults);

		/***
		 * 在response code失败时回调
		 * 
		 * @param query
		 *            查询对象
		 * @param e
		 *            异常
		 */
		void responseError(Query query, OpenTSDBHttpException e);

		/***
		 * 在发生错误是回调，如果http成功complete，但response code大于400，也会调用这个方法
		 * 
		 * @param query
		 *            查询对象
		 * @param e
		 *            异常
		 */
		void failed(Query query, Exception e);

	}

}