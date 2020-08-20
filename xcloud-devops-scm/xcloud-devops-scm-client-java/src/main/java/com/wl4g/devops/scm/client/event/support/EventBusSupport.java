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
package com.wl4g.devops.scm.client.event.support;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.wl4g.devops.scm.client.config.ScmClientProperties;

/**
 * {@link EventBusSupport}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-18
 * @since
 */
public class EventBusSupport implements Closeable {

	/** {@link ScmClientProperties} */
	protected final ScmClientProperties<?> config;

	/** {@link EventBus} */
	protected final EventBus bus;

	/** {@link ThreadPoolExecutor} */
	protected ThreadPoolExecutor executor;

	private EventBusSupport(ScmClientProperties<?> config) {
		notNullOf(config, "config");
		this.config = config;
		this.bus = initEventBus();
	}

	/**
	 * Gets {@link EventBusSupport} singleton.
	 * 
	 * @param config
	 * @return
	 */
	public static EventBusSupport getDefault(@Nonnull ScmClientProperties<?> config) {
		notNullOf(config, "config");
		if (isNull(DEFAULT)) { // Single checked
			synchronized (EventBusSupport.class) {
				if (isNull(DEFAULT)) { // Double checked
					DEFAULT = new EventBusSupport(config);
				}
			}
		}
		return DEFAULT;
	}

	/**
	 * Gets {@link EventBus} instance.
	 * 
	 * @return
	 */
	public EventBus getBus() {
		return bus;
	}

	@Override
	public void close() throws IOException {
		if (nonNull(executor)) {
			executor.shutdown();
		}
	}

	/**
	 * Init create {@link EventBus}
	 * 
	 * @param config
	 * @return
	 */
	private EventBus initEventBus() {
		final AtomicInteger incr = new AtomicInteger(0);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getAsyncEventThreads(), config.getAsyncEventThreads(), 0,
				MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
					Thread t = new Thread(r, "scm-event-".concat(valueOf(incr.getAndIncrement())));
					if (t.isDaemon())
						t.setDaemon(false);
					if (t.getPriority() != Thread.NORM_PRIORITY)
						t.setPriority(Thread.NORM_PRIORITY);
					return t;
				});
		return new AsyncEventBus("scm-event-bus", executor);
	}

	/** Single default instance of {@link EventBusSupport} */
	private static EventBusSupport DEFAULT;

}
