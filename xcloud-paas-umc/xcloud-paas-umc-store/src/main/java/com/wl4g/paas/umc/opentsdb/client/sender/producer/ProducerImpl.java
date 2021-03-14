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
package com.wl4g.paas.umc.opentsdb.client.sender.producer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.paas.umc.opentsdb.client.bean.request.Point;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午4:20
 * @Version: 1.0
 */

public class ProducerImpl implements Producer {

	final private Logger log = LoggerFactory.getLogger(getClass());

	private final BlockingQueue<Point> queue;

	private final AtomicBoolean forbiddenWrite = new AtomicBoolean(false);

	public ProducerImpl(BlockingQueue<Point> queue) {
		this.queue = queue;
		log.debug("the producer has started");
	}

	@Override
	public void send(Point point) {
		if (forbiddenWrite.get()) {
			throw new IllegalStateException("client has been closed.");
		}
		try {
			// 队列满时，put方法会被阻塞
			queue.put(point);
		} catch (InterruptedException e) {
			log.error("Client Thread been Interrupted.", e);
			e.printStackTrace();
		}
	}

	@Override
	public void forbiddenSend() {
		forbiddenWrite.compareAndSet(false, true);
	}

}