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

import com.github.rholder.retry.Attempt;
import com.google.common.eventbus.EventBus;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.CheckpointConfigEvent;
import com.wl4g.devops.scm.client.event.RefreshConfigEvent;
import com.wl4g.devops.scm.client.event.RefreshNextEvent;
import com.wl4g.devops.scm.client.event.ReportingConfigEvent;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo;

/**
 * {@link ScmEventPublisher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class ScmEventPublisher {

	/** {@link EventBus} */
	protected final EventBus bus;

	public ScmEventPublisher(@NotNull ScmClientProperties<?> config) {
		notNullOf(config, "config");
		this.bus = EventBusSupport.getDefault(config).getBus();
	}

	/**
	 * Publishing {@link RefreshConfigEvent}.
	 * 
	 * @param source
	 */
	public void publishRefreshEvent(ReleaseConfigInfo source) {
		bus.post(new RefreshConfigEvent(source));
	}

	/**
	 * Publishing {@link CheckpointConfigEvent}.
	 * 
	 * @param source
	 */
	public void publishCheckpointEvent(Object source) {
		bus.post(new CheckpointConfigEvent(source));
	}

	/**
	 * Publishing {@link ReportingConfigEvent}.
	 * 
	 * @param source
	 */
	public void publishReportingEvent(Attempt<?> source) {
		bus.post(new ReportingConfigEvent(source));
	}

	/**
	 * Publishing {@link RefreshNextEvent}.
	 * 
	 * @param source
	 */
	public void publishNextEvent(Object source) {
		bus.post(new RefreshNextEvent(source));
	}

}
