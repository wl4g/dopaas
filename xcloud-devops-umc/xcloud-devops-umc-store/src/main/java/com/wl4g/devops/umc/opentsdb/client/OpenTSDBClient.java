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
package com.wl4g.devops.umc.opentsdb.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Api;
import com.wl4g.devops.umc.opentsdb.client.bean.request.LastPointQuery;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Query;
import com.wl4g.devops.umc.opentsdb.client.bean.request.SuggestQuery;
import com.wl4g.devops.umc.opentsdb.client.bean.response.LastPointQueryResult;
import com.wl4g.devops.umc.opentsdb.client.bean.response.QueryResult;
import com.wl4g.devops.umc.opentsdb.client.common.Json;
import com.wl4g.devops.umc.opentsdb.client.http.HttpClient;
import com.wl4g.devops.umc.opentsdb.client.http.HttpClientFactory;
import com.wl4g.devops.umc.opentsdb.client.http.callback.QueryHttpResponseCallback;
import com.wl4g.devops.umc.opentsdb.client.sender.consumer.Consumer;
import com.wl4g.devops.umc.opentsdb.client.sender.consumer.ConsumerImpl;
import com.wl4g.devops.umc.opentsdb.client.sender.producer.Producer;
import com.wl4g.devops.umc.opentsdb.client.sender.producer.ProducerImpl;
import com.wl4g.devops.umc.opentsdb.client.util.ResponseUtil;

/**
 * @Description: opentsdb客户端
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:16
 * @Version: 1.0
 */
public class OpenTSDBClient {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private final OpenTSDBConfig config;

	private final HttpClient httpClient;

	private Producer producer;

	private Consumer consumer;

	private BlockingQueue<Point> queue;

	/***
	 * 通过反射来允许删除
	 */
	private static Field queryDeleteField;

	public OpenTSDBClient(OpenTSDBConfig config) throws IOReactorException {
		this.config = config;
		this.httpClient = HttpClientFactory.createHttpClient(config);
		this.httpClient.start();

		if (!config.isReadonly()) {
			this.queue = new ArrayBlockingQueue<>(config.getBatchPutBufferSize());
			this.producer = new ProducerImpl(queue);
			this.consumer = new ConsumerImpl(queue, httpClient, config);
			this.consumer.start();

			try {
				queryDeleteField = Query.class.getDeclaredField("delete");
				queryDeleteField.setAccessible(true);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}

		log.debug("the httpclient has started");
	}

	/***
	 * 查询数据
	 * 
	 * @param query
	 *            查询对象
	 * @return
	 */
	public List<QueryResult> query(Query query) throws IOException, ExecutionException, InterruptedException {
		Future<HttpResponse> future = httpClient.post(Api.QUERY.getPath(), Json.writeValueAsString(query));
		HttpResponse response = future.get();
		List<QueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class, QueryResult.class);
		return results;
	}

	/***
	 * 异步查询
	 * 
	 * @param query
	 *            查询对象
	 * @param callback
	 *            回调
	 */
	public void query(Query query, QueryHttpResponseCallback.QueryCallback callback) throws JsonProcessingException {
		QueryHttpResponseCallback queryHttpResponseCallback = new QueryHttpResponseCallback(callback, query);
		httpClient.post(Api.QUERY.getPath(), Json.writeValueAsString(query), queryHttpResponseCallback);
	}

	/***
	 * 查询最新的数据
	 * 
	 * @param query
	 *            查询对象
	 * @return
	 */
	public List<LastPointQueryResult> queryLast(LastPointQuery query)
			throws IOException, ExecutionException, InterruptedException {
		Future<HttpResponse> future = httpClient.post(Api.LAST.getPath(), Json.writeValueAsString(query));
		HttpResponse response = future.get();
		List<LastPointQueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class,
				LastPointQueryResult.class);
		return results;
	}

	/***
	 * 写入数据
	 * 
	 * @param point
	 *            数据点
	 */
	public void put(Point point) {
		if (config.isReadonly()) {
			throw new IllegalArgumentException("this client is readonly,can't put point");
		}
		producer.send(point);
	}

	/***
	 * 删除数据，返回删除的数据
	 * 
	 * @param query
	 *            查询对象
	 */
	public List<QueryResult> delete(Query query)
			throws IllegalAccessException, ExecutionException, InterruptedException, IOException {
		if (config.isReadonly()) {
			throw new IllegalArgumentException("this client is readonly,can't delete data");
		}
		queryDeleteField.set(query, true);
		Future<HttpResponse> future = httpClient.post(Api.QUERY.getPath(), Json.writeValueAsString(query));
		HttpResponse response = future.get();
		List<QueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class, QueryResult.class);
		return results;
	}

	/***
	 * 查询metric、tag_key、tag_value的信息
	 * 
	 * @param query
	 * @return
	 */
	public List<String> querySuggest(SuggestQuery query) throws ExecutionException, InterruptedException, IOException {
		Future<HttpResponse> future = httpClient.post(Api.SUGGEST.getPath(), Json.writeValueAsString(query));
		HttpResponse response = future.get();
		List<String> results = Json.readValue(ResponseUtil.getContent(response), List.class, String.class);
		return results;
	}

	/***
	 * 优雅关闭链接，会等待所有消费者线程结束
	 */
	public void gracefulClose() throws IOException {
		if (!config.isReadonly()) {
			// 先停止写入
			this.producer.forbiddenSend();
			// 等待队列被消费空
			this.waitEmpty();
			// 关闭消费者
			this.consumer.gracefulStop();
		}
		this.httpClient.gracefulClose();
	}

	/***
	 * 等待队列被消费空
	 */
	private void waitEmpty() {
		while (!queue.isEmpty()) {
			try {
				TimeUnit.MILLISECONDS.sleep(config.getBatchPutTimeLimit());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 强行关闭
	 */
	public void forceClose() throws IOException {
		if (!config.isReadonly()) {
			this.consumer.forceStop();
		}
		this.httpClient.forceClose();
	}

}