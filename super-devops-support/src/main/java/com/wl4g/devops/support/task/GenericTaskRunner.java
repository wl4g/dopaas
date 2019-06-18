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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
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
	public synchronized void run(ApplicationArguments args) throws Exception {
		// Call pre-startup
		preStartupProperties();

		if (bossRunning.compareAndSet(false, true)) {
			final int concurrency = taskProperties.getConcurrency();
			final long keepTime = taskProperties.getKeepAliveTime();
			final int acceptQueue = taskProperties.getAcceptQueue();

			// Create worker(if necessary)
			if (concurrency > 0) {
				final AtomicInteger counter = new AtomicInteger(-1);
				final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(acceptQueue);

				worker = new ThreadPoolExecutor(1, concurrency, keepTime, MICROSECONDS, queue, r -> {
					String name = getClass().getSimpleName() + "-worker-" + counter.incrementAndGet();
					Thread job = new Thread(this, name);
					job.setDaemon(false);
					job.setPriority(Thread.NORM_PRIORITY);
					return job;
				}, taskProperties.getReject());
			}

			// Create boss
			String name = getClass().getSimpleName() + "-boss";
			boss = new Thread(this, name);
			boss.setDaemon(false);
			boss.start();
		} else {
			log.warn("Already runner!, already builders are read-only and do not allow task modification");
		}

		// Call post startup
		postStartupProperties();
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

	@Override
	public void close() throws IOException {
		// Call pre close
		preCloseProperties();

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

		// Call post close
		postCloseProperties();
	}

	/**
	 * Pre startup properties
	 */
	protected void preStartupProperties() throws Exception {

	}

	/**
	 * Post startup properties
	 */
	protected void postStartupProperties() throws Exception {

	}

	/**
	 * Pre close properties
	 */
	protected void preCloseProperties() throws IOException {

	}

	/**
	 * Post close properties
	 */
	protected void postCloseProperties() throws IOException {

	}

	/**
	 * Is the current runner active.
	 * 
	 * @return
	 */
	protected boolean isActive() {
		return boss != null && !boss.isInterrupted() && bossRunning.get();
	}

	protected ThreadPoolExecutor getWorker() {
		Assert.state(worker != null, "Worker thread group is not enabled and can be enabled with concurrency>0");
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

		/**
		 * When the concurrency is less than 0, it means that the worker thread
		 * group is not enabled (only the boss asynchronous thread is started)
		 */
		private int concurrency = -1;

		/** watch dog delay */
		private long keepAliveTime = 0L;

		/**
		 * Consumption receive queue size
		 */
		private int acceptQueue = 8192;

		/** Rejected execution handler. */
		private RejectedExecutionHandler reject = new AbortPolicy();

		public TaskProperties() {
			super();
		}

		public TaskProperties(int concurrency, long keepAliveTime, int acceptQueue) {
			this(concurrency, keepAliveTime, acceptQueue, null);
		}

		public TaskProperties(int concurrency, long keepAliveTime, int acceptQueue, RejectedExecutionHandler reject) {
			super();
			setConcurrency(concurrency);
			setKeepAliveTime(keepAliveTime);
			setAcceptQueue(acceptQueue);
			setReject(reject);
		}

		public int getConcurrency() {
			return concurrency;
		}

		public void setConcurrency(int concurrency) {
			this.concurrency = concurrency;
		}

		public long getKeepAliveTime() {
			return keepAliveTime;
		}

		public void setKeepAliveTime(long keepAliveTime) {
			if (getConcurrency() > 0) {
				Assert.isTrue(keepAliveTime >= 0, "keepAliveTime must be greater than or equal to 0");
			}
			this.keepAliveTime = keepAliveTime;
		}

		public int getAcceptQueue() {
			return acceptQueue;
		}

		public void setAcceptQueue(int acceptQueue) {
			if (getConcurrency() > 0) {
				Assert.isTrue(acceptQueue > 0, "acceptQueue must be greater than 0");
			}
			this.acceptQueue = acceptQueue;
		}

		public RejectedExecutionHandler getReject() {
			return reject;
		}

		public void setReject(RejectedExecutionHandler reject) {
			if (reject != null) {
				this.reject = reject;
			}
		}

		@Override
		public String toString() {
			return "TaskProperties [concurrency=" + concurrency + ", keepAliveTime=" + keepAliveTime + ", acceptQueue="
					+ acceptQueue + ", reject=" + reject + "]";
		}

	}

}
