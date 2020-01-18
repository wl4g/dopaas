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
package com.wl4g.devops.support.task;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.wl4g.devops.tool.common.lang.Assert2;

/**
 * As the default {@link java.util.concurrent.ScheduledThreadPoolExecutor} and
 * {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler} of
 * JDK do not limit the maximum task waiting queue, the problem of oom may
 * occur, which is designed to solve this problem.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年1月18日
 * @since
 * @see {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
 * @see <a href=
 *      "http://www.doc88.com/p-3922316178617.html">ScheduledThreadPoolExecutor
 *      Retry task OOM resolution</a>
 */
class LimitScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

	/**
	 * Maximum allowed waiting execution queue size.
	 */
	final private int accessQueue;

	/**
	 * .{@link RejectedExecutionHandler}
	 */
	final private RejectedExecutionHandler rejectHandler;

	public LimitScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, int accessQueue,
			RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
		this.accessQueue = accessQueue;
		this.rejectHandler = handler;
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		checkAccessQueueLimit(command);
		return super.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		checkAccessQueueLimit(callable);
		return super.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		checkAccessQueueLimit(command);
		return super.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public void execute(Runnable command) {
		checkAccessQueueLimit(command);
		super.execute(command);
	}

	@Override
	public Future<?> submit(Runnable task) {
		checkAccessQueueLimit(task);
		return super.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		checkAccessQueueLimit(task);
		return super.submit(task, result);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		checkAccessQueueLimit(task);
		return super.submit(task);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		checkAccessQueueLimit(tasks);
		return super.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		checkAccessQueueLimit(tasks);
		return super.invokeAny(tasks, timeout, unit);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		checkAccessQueueLimit(tasks);
		return super.invokeAll(tasks);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		checkAccessQueueLimit(tasks);
		return super.invokeAll(tasks, timeout, unit);
	}

	private void checkAccessQueueLimit(Collection<? extends Callable<?>> tasks) {
		// TODO 使用异常处理器
		Assert2.state(getQueue().size() > accessQueue, "");
	}

	private void checkAccessQueueLimit(Callable<?> command) {
		// TODO 使用异常处理器
		Assert2.state(getQueue().size() > accessQueue, "");
	}

	private void checkAccessQueueLimit(Runnable command) {
		// TODO 使用异常处理器
		rejectHandler.rejectedExecution(command, this);
		Assert2.state(getQueue().size() > accessQueue, "");
	}

}
