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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

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
 * @see <a href= "http://www.doc88.com/p-3922316178617.html"> Resolution
 *      ScheduledThreadPoolExecutor for retry task OOM</a>
 */
class SafeLimitScheduledExecutor extends ScheduledThreadPoolExecutor {

	/**
	 * Maximum allowed waiting execution queue size.
	 */
	final private int accessQueue;

	/**
	 * {@link RejectedExecutionHandler}
	 */
	final private RejectedExecutionHandler rejectHandler;

	public SafeLimitScheduledExecutor(int corePoolSize, ThreadFactory threadFactory, int accessQueue,
			RejectedExecutionHandler handler) {
		super(corePoolSize, threadFactory, handler);
		this.accessQueue = accessQueue;
		this.rejectHandler = handler;
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		if (checkRejectedQueueLimit(command))
			return null;
		return super.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		if (checkRejectedQueueLimit(callable))
			return null;
		return super.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		if (checkRejectedQueueLimit(command))
			return null;
		return super.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	/**
	 * The {@link #invokeAll(Collection, long, TimeUnit)} or
	 * {@link #invokeAny(Collection, long, TimeUnit)} related methods also call
	 * {@link #execute(Runnable)} in the end. For details, see:
	 * 
	 * @see {@link java.util.concurrent.AbstractExecutorService#doInvokeAny()#176}
	 * @see {@link java.util.concurrent.ExecutorCompletionService#submit()}
	 */
	@Override
	public void execute(Runnable command) {
		if (checkRejectedQueueLimit(command))
			return;
		super.execute(command);
	}

	@Override
	public Future<?> submit(Runnable task) {
		if (checkRejectedQueueLimit(task))
			return null;
		return super.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		if (checkRejectedQueueLimit(task))
			return null;
		return super.submit(task, result);
	}

	/**
	 * Check whether the entry queue is rejected
	 * 
	 * @param command
	 * @return
	 */
	private boolean checkRejectedQueueLimit(Callable<?> command) {
		if (getQueue().size() > accessQueue) {
			rejectHandler.rejectedExecution(() -> {
				try {
					command.call();
				} catch (Exception e) {
					throw new IllegalStateException();
				}
			}, this);
			return true;
		}
		return false;
	}

	/**
	 * Check whether the entry queue is rejected
	 * 
	 * @param command
	 * @return
	 */
	private boolean checkRejectedQueueLimit(Runnable command) {
		if (getQueue().size() > accessQueue) {
			rejectHandler.rejectedExecution(command, this);
			// throw new RejectedExecutionException("Rejected execution of " + r
			// + " on " + executor, executor.isShutdown());
			return true;
		}
		return false;
	}

}
