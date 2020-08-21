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
package com.wl4g.devops.scm.client.event;

import static com.wl4g.components.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotNull;

import com.github.rholder.retry.Attempt;
import com.wl4g.components.common.eventbus.EventBusSupport;
import com.wl4g.devops.scm.client.event.RefreshConfigEvent.RefreshContext;
import com.wl4g.devops.scm.client.watch.GenericRefreshWatcher;
import com.wl4g.devops.scm.client.watch.RefreshWatcher;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo;

/**
 * {@link ScmEventPublisher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class ScmEventPublisher {

	/** {@link EventBusSupport} */
	protected final EventBusSupport support;

	/** {@link RefreshWatcher} */
	protected final GenericRefreshWatcher watcher;

	public ScmEventPublisher(@NotNull RefreshWatcher watcher) {
		notNullOf(watcher, "watcher");
		this.watcher = (GenericRefreshWatcher) watcher;
		this.support = EventBusSupport.getDefault(this.watcher.getScmConfig().getEventThreads());
	}

	/**
	 * Publishing {@link RefreshConfigEvent}.
	 * 
	 * @param source
	 */
	public void publishRefreshEvent(ReleaseConfigInfo source) {
		support.post(new RefreshConfigEvent(new RefreshContext(source, watcher.getRepository())));
	}

	/**
	 * Publishing {@link CheckpointConfigEvent}.
	 * 
	 * @param source
	 */
	public void publishCheckpointEvent(Object source) {
		support.post(new CheckpointConfigEvent(source));
	}

	/**
	 * Publishing {@link ReportingConfigEvent}.
	 * 
	 * @param source
	 */
	public void publishReportingEvent(Attempt<?> source) {
		support.post(new ReportingConfigEvent(source));
	}

}
