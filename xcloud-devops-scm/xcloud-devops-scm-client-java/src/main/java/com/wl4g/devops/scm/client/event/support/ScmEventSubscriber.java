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

import javax.validation.constraints.NotNull;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.CheckpointConfigEvent;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.event.RefreshConfigEvent;
import com.wl4g.devops.scm.client.event.RefreshNextEvent;
import com.wl4g.devops.scm.client.event.ReportingConfigEvent;

/**
 * {@link ScmEventSubscriber}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class ScmEventSubscriber {

	/** {@link EventBus} */
	protected final EventBus bus;

	/** {@link ConfigEventListener} */
	protected final ConfigEventListener[] listeners;

	public ScmEventSubscriber(@NotNull ScmClientProperties<?> config, @Nullable ConfigEventListener... listeners) {
		notNullOf(config, "config");
		// notEmpty(listeners, "listeners");
		this.bus = EventBusSupport.getDefault(config).getBus();
		this.listeners = listeners;
		// Init subscribers
		initEventSubscribers();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onRefresh(RefreshConfigEvent event) {
		for (ConfigEventListener l : listeners) {
			l.onRefresh(event);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onReporting(ReportingConfigEvent event) {
		for (ConfigEventListener l : listeners) {
			l.onReporting(event);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onCheckpoint(CheckpointConfigEvent event) {
		for (ConfigEventListener l : listeners) {
			l.onCheckpoint(event);
		}
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onNext(RefreshNextEvent event) {
		for (ConfigEventListener l : listeners) {
			l.onNext(event);
		}
	}

	/**
	 * Initialization event subscribers.
	 */
	private void initEventSubscribers() {
		this.bus.register(this);
	}

}
