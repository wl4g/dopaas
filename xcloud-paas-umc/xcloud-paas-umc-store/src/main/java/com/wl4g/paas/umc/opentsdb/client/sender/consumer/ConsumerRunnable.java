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
package com.wl4g.paas.umc.opentsdb.client.sender.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.paas.umc.opentsdb.client.OpenTSDBConfig;
import com.wl4g.paas.umc.opentsdb.client.bean.request.Api;
import com.wl4g.paas.umc.opentsdb.client.bean.request.Point;
import com.wl4g.paas.umc.opentsdb.client.common.Json;
import com.wl4g.paas.umc.opentsdb.client.http.HttpClient;
import com.wl4g.paas.umc.opentsdb.client.http.callback.BatchPutHttpResponseCallback;

/**
 * 消费者线程具体的消费逻辑
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午5:14
 * @Version: 1.0
 */
public class ConsumerRunnable implements Runnable {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private final BlockingQueue<Point> queue;

	private final HttpClient httpClient;

	@SuppressWarnings("unused")
	private final OpenTSDBConfig config;

	private final CountDownLatch countDownLatch;

	private BatchPutHttpResponseCallback.BatchPutCallBack callBack;

	/**
	 * 每批次数据点个数
	 */
	private int batchSize;

	/***
	 * 每次提交等待的时间间隔，单位ms
	 */
	private int batchPutTimeLimit;

	public ConsumerRunnable(BlockingQueue<Point> queue, HttpClient httpClient, OpenTSDBConfig config,
			CountDownLatch countDownLatch) {
		this.queue = queue;
		this.httpClient = httpClient;
		this.config = config;
		this.countDownLatch = countDownLatch;
		this.batchSize = config.getBatchPutSize();
		this.batchPutTimeLimit = config.getBatchPutTimeLimit();
		this.callBack = config.getBatchPutCallBack();
	}

	/***
	 * 设计原则是接收满${batchSize}个元素就提交，或者达到时间${batchPutTimeLimit}
	 * 当线程被打断说明cosumer执行了stop
	 */
	@Override
	public void run() {
		log.debug("thread:{} has started take point from queue", Thread.currentThread().getName());
		Point waitPoint = null;
		boolean readyClose = false;
		int waitTimeLimit = batchPutTimeLimit / 3;

		while (!readyClose) {
			long t0 = System.currentTimeMillis();
			List<Point> pointList = new ArrayList<>(batchSize);
			if (waitPoint != null) {
				pointList.add(waitPoint);
				waitPoint = null;
			}

			for (int i = pointList.size(); i < batchSize; i++) {
				try {
					Point point = queue.poll(waitTimeLimit, TimeUnit.MILLISECONDS);
					if (point != null) {
						pointList.add(point);
					}
					long t1 = System.currentTimeMillis();
					if (t1 - t0 > batchPutTimeLimit) {
						break;
					}
				} catch (InterruptedException e) {
					readyClose = true;
					log.info("The thread {} is interrupted", Thread.currentThread().getName());
					break;
				}
			}

			if (pointList.size() == 0 && !readyClose) {
				try {
					waitPoint = queue.take();
				} catch (InterruptedException e) {
					readyClose = true;
					log.info("The thread {} is interrupted", Thread.currentThread().getName());
				}
				continue;
			}

			if (pointList.size() == 0) {
				continue;
			}

			sendHttp(pointList);

		}

		this.countDownLatch.countDown();
	}

	/***
	 * 发送请求写入数据
	 * 
	 * @param points
	 *            数据点
	 */
	private void sendHttp(List<Point> points) {
		try {
			if (callBack == null) {
				httpClient.post(Api.PUT.getPath(), Json.writeValueAsString(points), new BatchPutHttpResponseCallback());
			} else {
				httpClient.post(Api.PUT_DETAIL.getPath(), Json.writeValueAsString(points),
						new BatchPutHttpResponseCallback(callBack, points));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}