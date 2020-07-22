/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.components.tools.common.task;

import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;

import java.io.Serializable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/**
 * Generic task runner properties
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月8日
 * @since
 */
public class RunnerProperties implements Serializable {
	final private static long serialVersionUID = -1996272636830701232L;
	final private static int DEFAULT_CONCURRENCY = -1;
	final private static long DEFAULT_KEEP_ALIVE_TIME = 0L;
	final private static int DEFAULT_ACCEPT_QUEUE = 2;

	/** Whether to start the boss thread asynchronously. */
	private boolean asyncStartup = false;

	/**
	 * When the concurrency is less than 0, it means that the worker thread
	 * group is not enabled (only the boss asynchronous thread is started)
	 */
	private int concurrency = DEFAULT_CONCURRENCY;

	/** Watch dog delay */
	private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

	/**
	 * When all threads are busy, consumption receive queue count.
	 */
	private int acceptQueue = DEFAULT_ACCEPT_QUEUE;

	/** Rejected execution handler. */
	private RejectedExecutionHandler reject = new AbortPolicy();

	public RunnerProperties() {
		super();
	}

	public RunnerProperties(boolean asyncStartup) {
		this(asyncStartup, DEFAULT_CONCURRENCY);
	}

	public RunnerProperties(boolean asyncStartup, int concurrency) {
		this(concurrency, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_ACCEPT_QUEUE, null);
	}

	public RunnerProperties(int concurrency, long keepAliveTime, int acceptQueue) {
		this(concurrency, keepAliveTime, acceptQueue, null);
	}

	public RunnerProperties(int concurrency, long keepAliveTime, int acceptQueue, RejectedExecutionHandler reject) {
		this(true, concurrency, keepAliveTime, acceptQueue, reject);
	}

	public RunnerProperties(boolean asyncStartup, int concurrency, long keepAliveTime, int acceptQueue,
			RejectedExecutionHandler reject) {
		setAsyncStartup(asyncStartup);
		setConcurrency(concurrency);
		setKeepAliveTime(keepAliveTime);
		setAcceptQueue(acceptQueue);
		setReject(reject);
	}

	public boolean isAsyncStartup() {
		return asyncStartup;
	}

	public void setAsyncStartup(boolean asyncStartup) {
		this.asyncStartup = asyncStartup;
	}

	public RunnerProperties withAsyncStartup(boolean asyncStartup) {
		setAsyncStartup(asyncStartup);
		return this;
	}

	public int getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(int concurrency) {
		// Assert.isTrue(concurrency > 0, "Concurrency must be greater than 0");
		this.concurrency = concurrency;
	}

	public RunnerProperties withConcurrency(int concurrency) {
		setConcurrency(concurrency);
		return this;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		if (getConcurrency() > 0) {
			isTrue(keepAliveTime >= 0, "keepAliveTime must be greater than or equal to 0");
		}
		this.keepAliveTime = keepAliveTime;
	}

	public RunnerProperties withKeepAliveTime(long keepAliveTime) {
		setKeepAliveTime(keepAliveTime);
		return this;
	}

	public int getAcceptQueue() {
		return acceptQueue;
	}

	public void setAcceptQueue(int acceptQueue) {
		if (getConcurrency() > 0) {
			isTrue(acceptQueue > 0, "acceptQueue must be greater than 0");
		}
		this.acceptQueue = acceptQueue;
	}

	public RunnerProperties withAcceptQueue(int acceptQueue) {
		setAcceptQueue(acceptQueue);
		return this;
	}

	public RejectedExecutionHandler getReject() {
		return reject;
	}

	public void setReject(RejectedExecutionHandler reject) {
		if (reject != null) {
			this.reject = reject;
		}
	}

	public RunnerProperties withReject(RejectedExecutionHandler reject) {
		setReject(reject);
		return this;
	}

	@Override
	public String toString() {
		return "TaskProperties [concurrency=" + concurrency + ", keepAliveTime=" + keepAliveTime + ", acceptQueue=" + acceptQueue
				+ ", reject=" + reject + "]";
	}

}