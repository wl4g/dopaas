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
package com.wl4g.devops.scm.client.configure.watch;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;

/**
 * Abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class AbstractRefreshWatcher implements InitializingBean, DisposableBean, Closeable {
	final protected Logger log = LoggerFactory.getLogger(getClass());
	final protected AtomicBoolean running = new AtomicBoolean(false);
	final protected ExecutorService worker;
	final protected ScmContextRefresher refresher;

	public AbstractRefreshWatcher(ScmContextRefresher refresher) {
		Assert.notNull(refresher, "Refresher must not be null");
		this.refresher = refresher;

		// Initialize executor
		final AtomicInteger counter = new AtomicInteger(0);
		this.worker = new ThreadPoolExecutor(1, 2, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(16), r -> {
			String name = "scmRefreshWatch-" + counter.incrementAndGet();
			Thread t = new Thread(r, name);
			t.setDaemon(true);
			return t;
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (running.compareAndSet(false, true)) {
			// Do actual
			doStart();
		} else {
			throw new IllegalStateException("Already started watcher.");
		}
	}

	protected abstract void doStart();

	@Override
	public void destroy() throws Exception {
		if (running.compareAndSet(true, false)) {
			close();
		}
	}

	protected void doExecute(Object source, byte data[], String eventDesc) {
		worker.execute(() -> {
			try {
				if (isPayload(data)) {
					// ReleaseMeta meta = ReleaseMeta.of(new String(data,
					// Charsets.UTF_8));
					// Do refresh.
					refresher.refresh();
				} else {
					if (log.isInfoEnabled()) {
						log.info("Zk listening empty payload. source: {}", source);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		});
	}

	private boolean isPayload(byte[] value) {
		return value != null && value.length > 0;
	}

}