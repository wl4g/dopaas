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

import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.devops.tool.common.collection.CollectionUtils2;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.lang.Assert2.state;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Generic local scheduler & task runner.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 * @see {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
 */
public abstract class GenericTaskRunner<C extends RunnerProperties> implements Closeable, Runnable, ApplicationRunner {
	final protected Logger log = getLogger(getClass());

	/** Boss running. */
	final private AtomicBoolean bossState = new AtomicBoolean(false);

	/** Runner task properties configuration. */
	final C config;

	/** Runner boss thread. */
	private Thread boss;

	/** Runner worker thread group pool. */
	private ThreadPoolExecutor worker;

	@SuppressWarnings("unchecked")
	public GenericTaskRunner() {
		this((C) new RunnerProperties());
	}

	public GenericTaskRunner(C config) {
		notNull(config, "GenericTaskRunner properties can't null");
		this.config = config;
	}

	@Override
	public synchronized void close() throws IOException {
		// Call pre close
		preCloseProperties();

		if (bossState.compareAndSet(true, false)) {
			if (worker != null) {
				try {
					worker.shutdown();
					worker = null;
				} catch (Exception e) {
					log.error("Runner worker shutdown failed!", e);
				}
			}
			try {
				if (boss != null) {
					boss.interrupt();
					boss = null;
				}
			} catch (Exception e) {
				log.error("Runner boss interrupt failed!", e);
			}
		}

		// Call post close
		postCloseProperties();
	}

	/**
	 * Auto initialization on startup.
	 * 
	 * @throws Exception
	 */
	@Override
	public synchronized void run(ApplicationArguments args) throws Exception {
		// Call PreStartup
		preStartupProperties();

		// Create worker(if necessary)
		if (bossState.compareAndSet(false, true)) {
			if (config.getConcurrency() > 0) {
				// See:https://www.jianshu.com/p/e7ab1ac8eb4c
				worker = new ThreadPoolExecutor(config.getConcurrency(), config.getConcurrency(), config.getKeepAliveTime(),
						MICROSECONDS, new LinkedBlockingQueue<>(config.getAcceptQueue()),
						new NamedThreadFactory(getClass().getSimpleName()), config.getReject());
			} else {
				log.warn("No workthread pool for started, because the number of workthread is less than 0");
			}

			// Boss asynchronously execution.(if necessary)
			if (config.isAsyncStartup()) {
				String name = getClass().getSimpleName() + "-boss";
				boss = new Thread(this, name);
				boss.setDaemon(false);
				boss.start();
			} else {
				run(); // Sync execution.
			}
		} else {
			log.warn("Already runner!, already builders are read-only and do not allow task modification");
		}

		// Call post startup
		postStartupProperties();
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
		return boss != null && !boss.isInterrupted() && bossState.get();
	}

	/**
	 * Get configuration properties.
	 * 
	 * @return
	 */
	public C getConfig() {
		return config;
	}

	@Override
	public void run() {
		// Ignore
	}

	/**
	 * Submitted job wait for completed.
	 * 
	 * @param jobs
	 * @param timeoutMs
	 * @throws IllegalStateException
	 */
	public void submitForComplete(List<Runnable> jobs, long timeoutMs) throws IllegalStateException {
		submitForComplete(jobs, (ex, completed, uncompleted) -> {
			if (nonNull(ex)) {
				throw ex;
			}
		}, timeoutMs);
	}

	/**
	 * Submitted job wait for completed.
	 * 
	 * @param jobs
	 * @param listener
	 * @param timeoutMs
	 * @throws IllegalStateException
	 */
	public void submitForComplete(List<Runnable> jobs, CompleteTaskListener listener, long timeoutMs)
			throws IllegalStateException {
		if (!CollectionUtils2.isEmpty(jobs)) {
			int total = jobs.size();
			// Future jobs.
			Map<Future<?>, Runnable> fjobs = new HashMap<Future<?>, Runnable>(total);
			try {
				CountDownLatch latch = new CountDownLatch(total); // Submit.
				jobs.stream().forEach(job -> fjobs.put(getWorker().submit(new FutureDoneTask(latch, job)), job));

				if (!latch.await(timeoutMs, MILLISECONDS)) { // Timeout?
					Iterator<Entry<Future<?>, Runnable>> it = fjobs.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Future<?>, Runnable> entry = it.next();
						if (!entry.getKey().isCancelled() && !entry.getKey().isDone()) {
							entry.getKey().cancel(true);
						} else {
							it.remove(); // Cleanup cancelled or isDone
						}
					}

					TimeoutException ex = new TimeoutException(
							String.format("Failed to job execution timeout, %s -> completed(%s)/total(%s)",
									jobs.get(0).getClass().getName(), (total - latch.getCount()), total));
					listener.onComplete(ex, (total - latch.getCount()), fjobs.values());
				} else {
					listener.onComplete(null, total, emptyList());
				}
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Thread pool executor worker.
	 * 
	 * @return
	 */
	protected ThreadPoolExecutor getWorker() {
		state(nonNull(worker), "Worker thread group is not enabled and can be enabled with concurrency >0");
		return worker;
	}

	/**
	 * The named thread factory
	 */
	private class NamedThreadFactory implements ThreadFactory {
		private final AtomicInteger threads = new AtomicInteger(1);
		private final ThreadGroup group;
		private final String prefix;

		NamedThreadFactory(String prefix) {
			SecurityManager s = System.getSecurityManager();
			this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			if (isBlank(prefix)) {
				prefix = GenericTaskRunner.class.getSimpleName() + "-Default";
			}
			this.prefix = prefix;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, prefix + "-" + threads.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

	/**
	 * Future done runnable wrapper.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月17日
	 * @since
	 */
	private class FutureDoneTask implements Runnable {

		/** {@link CountDownLatch} */
		final private CountDownLatch latch;

		/** Real runner job. */
		final private Runnable job;

		public FutureDoneTask(CountDownLatch latch, Runnable job) {
			notNull(latch, "Job runable latch must not be null.");
			notNull(job, "Job runable must not be null.");
			this.latch = latch;
			this.job = job;
		}

		@Override
		public void run() {
			try {
				job.run();
			} catch (Exception e) {
				log.error("Execution failure task", e);
			} finally {
				latch.countDown();
			}
		}

	}

	/**
	 * Wait completion task listener.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月17日
	 * @since
	 */
	public static interface CompleteTaskListener {

		/**
		 * Call-back completion listener.
		 * 
		 * @param ex
		 * @param completed
		 * @param uncompleted
		 * @throws Exception
		 */
		void onComplete(TimeoutException ex, long completed, Collection<Runnable> uncompleted) throws Exception;
	}

}