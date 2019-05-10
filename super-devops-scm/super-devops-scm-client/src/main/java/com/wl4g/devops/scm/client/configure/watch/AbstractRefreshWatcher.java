/*
 * Copyright 2015 the original author or authors.
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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseMeta;
import com.wl4g.devops.scm.client.configure.refresh.BeanRefresher;

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
	final private ExecutorService executor;

	@Autowired
	private BeanRefresher refresher;

	public AbstractRefreshWatcher() {
		this.executor = Executors.newFixedThreadPool(1, new ThreadFactory() {
			final private AtomicInteger counter = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				String name = "devops-refresher-" + this.counter.incrementAndGet();
				return new Thread(r, name);
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.running.compareAndSet(false, true)) {
			this.doStart();
		} else {
			throw new IllegalStateException("Already started watcher.");
		}
	}

	protected abstract void doStart();

	@Override
	public void destroy() throws Exception {
		if (this.running.compareAndSet(true, false)) {
			this.close();
		}
	}

	protected void doExecute(Object source, byte value[], String eventDesc) {
		this.executor.submit(() -> {
			try {
				if (this.isPayload(value)) {
					String releaseMeta = new String(value, Charsets.UTF_8);
					// Do refresh.
					this.refresher.refresh(ReleaseMeta.of(releaseMeta));
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