package com.wl4g.devops.support.task;

import java.io.Serializable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import org.springframework.util.Assert;

/**
 * Generic task runner properties
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月8日
 * @since
 */
public class RunnerProperties implements Serializable {

	private static final long serialVersionUID = -1996272636830701232L;

	/** Whether to start the boss thread asynchronously. */
	private boolean startup = false;

	/**
	 * When the concurrency is less than 0, it means that the worker thread
	 * group is not enabled (only the boss asynchronous thread is started)
	 */
	private int concurrency = -1;

	/** Watch dog delay */
	private long keepAliveTime = 0L;

	/**
	 * Consumption receive queue size
	 */
	private int acceptQueue = 2;

	/** Rejected execution handler. */
	private RejectedExecutionHandler reject = new AbortPolicy();

	public RunnerProperties() {
		super();
	}

	public RunnerProperties(boolean startup) {
		this(-1);
	}

	public RunnerProperties(int concurrency) {
		this(concurrency, 0L, 2, null);
	}

	public RunnerProperties(int concurrency, long keepAliveTime, int acceptQueue) {
		this(concurrency, keepAliveTime, acceptQueue, null);
	}

	public RunnerProperties(int concurrency, long keepAliveTime, int acceptQueue, RejectedExecutionHandler reject) {
		this(true, concurrency, keepAliveTime, acceptQueue, reject);
	}

	public RunnerProperties(boolean startup, int concurrency, long keepAliveTime, int acceptQueue,
			RejectedExecutionHandler reject) {
		setStartup(startup);
		setConcurrency(concurrency);
		setKeepAliveTime(keepAliveTime);
		setAcceptQueue(acceptQueue);
		setReject(reject);
	}

	public boolean isStartup() {
		return startup;
	}

	public void setStartup(boolean async) {
		this.startup = async;
	}

	public int getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(int concurrency) {
		Assert.isTrue(concurrency > 0, "Concurrency must be greater than 0");
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
		return "TaskProperties [concurrency=" + concurrency + ", keepAliveTime=" + keepAliveTime + ", acceptQueue=" + acceptQueue
				+ ", reject=" + reject + "]";
	}

}
