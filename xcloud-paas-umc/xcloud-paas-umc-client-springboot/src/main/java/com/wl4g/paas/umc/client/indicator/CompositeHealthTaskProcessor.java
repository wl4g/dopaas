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
package com.wl4g.paas.umc.client.indicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Compound health monitoring processor.<br/>
 * Note: if you change it into an internal class `@Component`, it doesn't seem
 * to work.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
public class CompositeHealthTaskProcessor implements InitializingBean, DisposableBean {
	final private static Logger logger = LoggerFactory.getLogger(CompositeHealthTaskProcessor.class);

	final private AtomicBoolean running = new AtomicBoolean(false);
	private long acq = 4_000L;
	private List<Runnable> tasks = new ArrayList<>();
	private ExecutorService executor;

	public void submit(Runnable task) {
		if (!this.tasks.contains(task)) {
			this.tasks.add(task);
		}
	}

	private void doStart() {
		if (!this.running.compareAndSet(false, true)) {
			throw new IllegalStateException("Already started health indicator executor.");
		}
		if (logger.isInfoEnabled())
			logger.info("Starting health indicator executor...");

		this.executor.submit(() -> {
			while (true) {
				try {
					this.tasks.forEach((task) -> {
						try {
							task.run();
						} catch (Exception e) {
							logger.error("Execution error.", e);
						}
					});
					Thread.sleep(this.acq);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void destroy() throws Exception {
		if (logger.isInfoEnabled())
			logger.info("Destroy health indicator executor...");

		if (this.running.compareAndSet(true, false))
			this.executor.shutdownNow();
		else
			logger.warn("Non startup health indicator executor.");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.executor = Executors.newFixedThreadPool(2, (r) -> {
			Thread t = new Thread(r, CompositeHealthTaskProcessor.class.getSimpleName() + "-" + System.currentTimeMillis());
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		});

		this.doStart();
	}

}