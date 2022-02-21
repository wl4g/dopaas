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
package com.wl4g.dopaas.umc.opentsdb.client.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.dopaas.umc.opentsdb.client.OpenTSDBConfig;
import com.wl4g.dopaas.umc.opentsdb.client.http.callback.GracefulCloseFutureCallBack;

/**
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午1:29
 * @Version: 1.0
 */

public class HttpClient {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private String host;

	private int port;

	/**
	 * 通过这个client来完成请求
	 */
	private final CloseableHttpAsyncClient client;

	/**
	 * 未完成任务数 for graceful close.
	 */
	private final AtomicInteger unCompletedTaskNum;

	/**
	 * 空闲连接清理服务
	 */
	private ScheduledExecutorService connectionGcService;

	HttpClient(OpenTSDBConfig config, CloseableHttpAsyncClient client, ScheduledExecutorService connectionGcService) {
		this.host = config.getHost();
		this.port = config.getPort();
		this.client = client;
		this.connectionGcService = connectionGcService;
		this.unCompletedTaskNum = new AtomicInteger(0);
	}

	/***
	 * post请求
	 * 
	 * @param path
	 *            请求路径
	 * @param json
	 *            json格式参数
	 * @return
	 */
	public Future<HttpResponse> post(String path, String json) {
		return this.post(path, json, null);
	}

	/***
	 * post请求
	 * 
	 * @param path
	 *            请求路径
	 * @param json
	 *            请求内容，json格式z
	 * @param httpCallback
	 *            回调
	 * @return
	 */
	public Future<HttpResponse> post(String path, String json, FutureCallback<HttpResponse> httpCallback) {
		log.debug("发送post请求，路径:{}，请求内容:{}", path, json);
		HttpPost httpPost = new HttpPost(getUrl(path));
		if (StringUtils.isNoneBlank(json)) {
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setEntity(generateStringEntity(json));
		}

		FutureCallback<HttpResponse> responseCallback = null;
		if (httpCallback != null) {
			log.debug("等待完成的任务数:{}", unCompletedTaskNum.incrementAndGet());
			responseCallback = new GracefulCloseFutureCallBack(unCompletedTaskNum, httpCallback);
		}

		return client.execute(httpPost, responseCallback);
	}

	private String getUrl(String path) {
		return host + ":" + port + path;
	}

	private StringEntity generateStringEntity(String json) {
		StringEntity stringEntity = new StringEntity(json, Charset.forName("UTF-8"));
		return stringEntity;
	}

	public void start() {
		this.client.start();
	}

	public void gracefulClose() throws IOException {
		this.close(false);
	}

	public void forceClose() throws IOException {
		this.close(true);
	}

	private void close(boolean force) throws IOException {
		// 关闭等待
		if (!force) {
			// 优雅关闭
			while (client.isRunning()) {
				int i = this.unCompletedTaskNum.get();
				if (i == 0) {
					break;
				} else {
					try {
						// 轮询检查优雅关闭
						Thread.sleep(50);
					} catch (InterruptedException e) {
						log.warn("The thread {} is Interrupted", Thread.currentThread().getName());
					}
				}
			}
		}
		connectionGcService.shutdownNow();

		// 关闭
		client.close();
	}

}