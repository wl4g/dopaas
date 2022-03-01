/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.ucm.client.event;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotNull;

import com.github.rholder.retry.Attempt;
import com.wl4g.infra.common.eventbus.EventBusSupport;
import com.wl4g.dopaas.ucm.client.event.RefreshConfigEvent.RefreshContext;
import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.client.internal.RefreshWatcher;
import com.wl4g.dopaas.ucm.client.recorder.ReleaseConfigSourceWrapper;

/**
 * {@link UcmEventPublisher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class UcmEventPublisher {

    /** {@link EventBusSupport} */
    protected final EventBusSupport bus;

    /** {@link RefreshWatcher} */
    protected final AbstractRefreshWatcher<?> watcher;

    public UcmEventPublisher(@NotNull RefreshWatcher watcher, @NotNull EventBusSupport bus) {
        this.watcher = (AbstractRefreshWatcher<?>) notNullOf(watcher, "watcher");
        this.bus = notNullOf(bus, "bus");
    }

    /**
     * Publishing {@link RefreshConfigEvent}.
     * 
     * @param release
     */
    public void publishRefreshEvent(ReleaseConfigSourceWrapper release) {
        bus.post(new RefreshConfigEvent(new RefreshContext(release, watcher.getRecorder())));
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

}