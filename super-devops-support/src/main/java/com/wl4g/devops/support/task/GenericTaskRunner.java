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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.devops.tool.common.log.SmartLogger;
import com.wl4g.devops.tool.common.task.SafeEnhancedScheduledTaskExecutor;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.lang.Assert2.state;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Generic local scheduler & task runner.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 * @see {@link org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
 * @see <a href=
 *      "http://www.doc88.com/p-3922316178617.html">ScheduledThreadPoolExecutor
 *      Retry task OOM resolution</a>
 */
public abstract class GenericTaskRunner<C extends RunnerProperties>
		implements Closeable, Runnable, ApplicationRunner, DisposableBean {

	final protected SmartLogger log = getLogger(getClass());

	/** Running state. */
	final private AtomicBoolean running = new AtomicBoolean(false);

	/** Runner task properties configuration. */
	final C config;

	/** Runner boss thread. */
	private Thread boss;

	/** Runner worker thread group pool. */
	private SafeEnhancedScheduledTaskExecutor worker;

	@SuppressWarnings("unchecked")
	public GenericTaskRunner() {
		this((C) new RunnerProperties());
	}

	public GenericTaskRunner(C config) {
		notNull(config, "GenericTaskRunner properties can't null");
		this.config = config;
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

	/**
	 * Note: It is recommended to use the {@link AtomicBoolean} mechanism to
	 * avoid using synchronized. </br>
	 * Error example:
	 * 
	 * <pre>
	 * public abstract class ParentClass implements Closeable, Runnable {
	 * 	public synchronized void close() {
	 * 		// Some close or release ...
	 * 	}
	 * }
	 * 
	 * public class SubClass extends ParentClass {
	 * 	public synchronized void run() {
	 * 		// Long-time jobs ...
	 * 
	 * 		// For example:
	 * 		// while(true) {
	 * 		// ...
	 * 		// }
	 * 	}
	 * }
	 * </pre>
	 * 
	 * At this time, it may lead to deadlock, because SubClass.run() has not
	 * been executed and is not locked, resulting in the call to
	 * ParentClass.close() always waiting. </br>
	 * </br>
	 */
	@Override
	public void close() throws IOException {
		if (running.compareAndSet(true, false)) {
			// Call pre close
			preCloseProperties();

			// Close for thread pool worker.
			if (!isNull(worker)) {
				try {
					worker.shutdown();
					worker = null;
				} catch (Exception e) {
					log.error("Runner worker shutdown failed!", e);
				}
			}

			// Close for thread-boss.
			try {
				if (!isNull(boss)) {
					boss.interrupt();
					boss = null;
				}
			} catch (Exception e) {
				log.error("Runner boss interrupt failed!", e);
			}

			// Call post close
			postCloseProperties();
		}
	}

	/**
	 * Auto initialization on startup.
	 * 
	 * @throws Exception
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (running.compareAndSet(false, true)) {
			// Call PreStartup
			preStartupProperties();

			// Create worker(if necessary)
			if (config.getConcurrency() > 0) {
				// See:https://www.jianshu.com/p/e7ab1ac8eb4c
				ThreadFactory tf = new NamedThreadFactory(getClass().getSimpleName() + "-worker");
				worker = new SafeEnhancedScheduledTaskExecutor(config.getConcurrency(), config.getKeepAliveTime(), tf,
						config.getAcceptQueue(), config.getReject());
			} else {
				log.warn("No start threads worker, because the number of workthreads is less than 0");
			}

			// Boss asynchronously execution.(if necessary)
			if (config.isAsyncStartup()) {
				boss = new NamedThreadFactory(getClass().getSimpleName() + "-boss").newThread(this);
				boss.start();
			} else {
				run(); // Sync execution.
			}

			// Call post startup
			postStartupProperties();
		} else {
			log.warn("Could not startup runner! because already builders are read-only and do not allow task modification");
		}
	}

	/**
	 * Pre startup properties
	 */
	protected void preStartupProperties() throws Exception {
		// Ignore
	}

	/**
	 * Post startup properties
	 */
	protected void postStartupProperties() throws Exception {
		// Ignore
	}

	/**
	 * Pre close properties
	 */
	protected void preCloseProperties() throws IOException {
		// Ignore
	}

	/**
	 * Post close properties
	 */
	protected void postCloseProperties() throws IOException {
		// Ignore
	}

	/**
	 * Is the current runner active.
	 * 
	 * @return
	 */
	protected boolean isActive() {
		return nonNull(boss) && !boss.isInterrupted() && running.get();
	}

	/**
	 * Get configuration properties.
	 * 
	 * @return
	 */
	protected C getConfig() {
		return config;
	}

	@Override
	public void run() {
		// Ignore
	}

	/**
	 * Thread pool executor worker.
	 * 
	 * @return
	 */
	public SafeEnhancedScheduledTaskExecutor getWorker() {
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
				prefix = GenericTaskRunner.class.getSimpleName() + "-default";
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

}