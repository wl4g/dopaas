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
package com.wl4g.devops.umc.opentsdb.client.http.callback;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定义一个FutureCallBack，用来对任务完成、异常、取消后进行减数
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午10:03
 * @Version: 1.0
 */
public class GracefulCloseFutureCallBack implements FutureCallback<HttpResponse> {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private final AtomicInteger unCompletedTaskNum;
	private final FutureCallback<HttpResponse> futureCallback;

	public GracefulCloseFutureCallBack(AtomicInteger unCompletedTaskNum, FutureCallback<HttpResponse> futureCallback) {
		super();
		this.unCompletedTaskNum = unCompletedTaskNum;
		this.futureCallback = futureCallback;
	}

	@Override
	public void completed(HttpResponse result) {
		futureCallback.completed(result);
		// 任务处理完毕，再减数
		log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
	}

	@Override
	public void failed(Exception ex) {
		futureCallback.failed(ex);
		// 任务处理完毕，再减数
		log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
	}

	@Override
	public void cancelled() {
		futureCallback.cancelled();
		// 任务处理完毕，再减数
		log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
	}

}