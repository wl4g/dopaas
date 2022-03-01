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
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import javax.annotation.Nullable;

import com.wl4g.dopaas.ucm.client.internal.AbstractRefreshWatcher;
import com.wl4g.dopaas.ucm.client.internal.RefreshWatcher;
import com.wl4g.dopaas.ucm.client.internal.ReportingConfigListener;
import com.wl4g.infra.common.eventbus.EventBusSupport;

/**
 * {@link UcmEventSubscriber}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public class UcmEventSubscriber {

    /** {@link EventBusSupport} */
    protected final EventBusSupport bus;

    /** {@link RefreshWatcher} */
    protected final AbstractRefreshWatcher<?> watcher;

    /** {@link ConfigEventListener} */
    protected final ConfigEventListener[] listeners;

    public UcmEventSubscriber(@NotNull RefreshWatcher watcher, @NotNull EventBusSupport bus,
            @Nullable ConfigEventListener... listeners) {
        this.watcher = (AbstractRefreshWatcher<?>) notNullOf(watcher, "watcher");
        this.bus = notNullOf(bus, "bus");
        this.listeners = wrapEventListeners(listeners);
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

    /**
     * Wrap {@link ConfigEventListener}, insert to first system internal event
     * listeners.
     * 
     * @param listeners
     * @return
     */
    private ConfigEventListener[] wrapEventListeners(ConfigEventListener... listeners) {
        List<ConfigEventListener> _listeners = new ArrayList<>(4);

        // Insert internal listeners to first.
        _listeners.add(new ReportingConfigListener(watcher));

        // After addition listeners.
        if (nonNull(listeners)) {
            for (ConfigEventListener l : listeners) {
                _listeners.add(l);
            }
        }

        return _listeners.toArray(new ConfigEventListener[] {});
    }

    /**
     * Initialization event subscribers.
     */
    private void initEventSubscribers() {
        this.bus.register(this);
    }

}