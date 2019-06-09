/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.support.task;

import static java.util.concurrent.TimeUnit.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.Assert;

/**
 * Generic task schedule runner.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 */
public abstract class GenericTaskRunner implements DisposableBean, ApplicationRunner, Closeable, Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	final private AtomicBoolean bossRunning = new AtomicBoolean(false);
	final private TaskProperties taskProperties;

	/** Runner boss thread. */
	private Thread boss;

	/** Runner worker thread group pool. */
	private ThreadPoolExecutor worker;

	public GenericTaskRunner(TaskProperties taskProperties) {
		Assert.notNull(taskProperties, "Task properties must not be null");
		this.taskProperties = taskProperties;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (bossRunning.compareAndSet(false, true)) {
			// Create worker
			final AtomicInteger counter = new AtomicInteger(-1);
			worker = new ThreadPoolExecutor(1, taskProperties.getConcurrency(), taskProperties.getKeepAliveTime(), MICROSECONDS,
					new LinkedBlockingQueue<>(taskProperties.getAcceptQueue()), r -> {
						String name = getClass().getSimpleName() + "-worker-" + counter.incrementAndGet();
						Thread job = new Thread(this, name);
						job.setDaemon(false);
						job.setPriority(Thread.NORM_PRIORITY);
						return job;
					});

			// Create boss
			String name = getClass().getSimpleName() + "-boos";
			boss = new Thread(this, name);
			boss.setDaemon(false);
			boss.start();

			// Call post
			postStartupProperties();
		} else {
			log.warn("Already runner!, already builders are read-only and do not allow task modification");
		}
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

	@Override
	public void close() throws IOException {
		if (bossRunning.compareAndSet(true, false)) {
			if (worker != null) {
				try {
					worker.shutdown();
					worker = null;
				} catch (Exception e) {
					log.error("Runner worker shutdown failed!", e);
				}
			}
			try {
				boss.interrupt();
				boss = null;
			} catch (Exception e) {
				log.error("Runner boss interrupt failed!", e);
			}
		}

		// Call post closed
		postCloseProperties();
	}

	protected void postStartupProperties() {

	}

	protected void postCloseProperties() {

	}

	protected ThreadPoolExecutor getWorker() {
		return worker;
	}

	/**
	 * Generic task runner properties
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年6月8日
	 * @since
	 */
	public static class TaskProperties implements Serializable {

		private static final long serialVersionUID = -1996272636830701232L;

		/** concurrency */
		private Integer concurrency = 3;

		/** watch dog delay */
		private Long keepAliveTime = 0L;

		/**
		 * Consumption rReceive queue size
		 */
		private Integer acceptQueue = 65535;

		public TaskProperties() {
			super();
		}

		public TaskProperties(Integer concurrency, Long keepAliveTime, Integer acceptQueue) {
			super();
			setConcurrency(concurrency);
			setKeepAliveTime(keepAliveTime);
			setAcceptQueue(acceptQueue);
		}

		public Integer getConcurrency() {
			return concurrency;
		}

		public void setConcurrency(Integer concurrency) {
			Assert.isTrue(concurrency > 0, "Concurrency must be greater than 0");
			this.concurrency = concurrency;
		}

		public Long getKeepAliveTime() {
			Assert.isTrue(concurrency >= 0, "Concurrency must be greater or eq than 0");
			return keepAliveTime;
		}

		public void setKeepAliveTime(Long keepAliveTime) {
			Assert.isTrue(keepAliveTime >= 0, "keepAliveTime must be greater than or equal to 0");
			this.keepAliveTime = keepAliveTime;
		}

		public Integer getAcceptQueue() {
			return acceptQueue;
		}

		public void setAcceptQueue(Integer acceptQueue) {
			Assert.isTrue(acceptQueue > 0, "acceptQueue must be greater than 0");
			this.acceptQueue = acceptQueue;
		}

	}

}
