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

import static java.lang.String.valueOf;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

/**
 * {@link EventBusSupport}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-18
 * @since
 */
abstract class EventBusSupport {

	/** {@link EventBus} */
	protected final EventBus bus;

	private EventBusSupport() {
		this.bus = initEventBus();
	}

	/**
	 * Gets {@link EventBusSupport} singleton.
	 * 
	 * @return
	 */
	public static EventBusSupport getDefault() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Gets {@link EventBus} instance.
	 * 
	 * @return
	 */
	public EventBus getBus() {
		return bus;
	}

	/**
	 * Init create {@link EventBus}
	 * 
	 * @return
	 */
	private EventBus initEventBus() {
		final AtomicInteger incr = new AtomicInteger(1);
		return new AsyncEventBus("scm-event-bus", Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "scm-event-".concat(valueOf(incr.getAndIncrement())));
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}));
	}

	/**
	 * {@link SingletonHolder}
	 *
	 * @since
	 */
	private static class SingletonHolder {
		private static final EventBusSupport INSTANCE = new EventBusSupport() {
		};
	}

}
