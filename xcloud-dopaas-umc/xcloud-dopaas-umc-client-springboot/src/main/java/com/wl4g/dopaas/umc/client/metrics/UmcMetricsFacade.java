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
package com.wl4g.dopaas.umc.client.metrics;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusCounter;
import io.micrometer.prometheus.PrometheusDistributionSummary;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.prometheus.PrometheusTimer;
import lombok.Getter;

/**
 * {@link UmcMetricsFacade}, Tends to use Prometheus standards.
 * 
 * Counter, Timer, Gauge, DistributionSummary, etc.
 * 
 * {@link PrometheusMeterRegistry}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-16 v1.0.0
 * @since v1.0.0
 */
@Getter
public class UmcMetricsFacade {

    private final PrometheusMeterRegistry registry;

    public UmcMetricsFacade(PrometheusMeterRegistry registry) {
        this.registry = notNullOf(registry, "registry");
    }

    public Counter counter(String name, Tag... tags) {
        return registry.counter(name, asList(tags));
    }

    public Timer timer(String name, Tag... tags) {
        return registry.timer(name, asList(tags));
    }

    public double gauge(String name, double number, Tag... tags) {
        return registry.gauge(name, asList(tags), number);
    }

    public long gauge(String name, long number, Tag... tags) {
        return registry.gauge(name, asList(tags), number);
    }

    public int gauge(String name, int number, Tag... tags) {
        return registry.gauge(name, asList(tags), number);
    }

    /**
     * Used default configuration refer to:
     * {@link DistributionStatisticConfig#DEFAULT}
     * 
     * @param name
     * @param tags
     * @return
     */
    public DistributionSummary summary(String name, Tag... tags) {
        return registry.summary(name, asList(tags));
    }

    public DistributionSummary summary(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
        return registry.newDistributionSummary(id, distributionStatisticConfig, scale);
    }

    public static Counter newPrometheusCounter(Meter.Id id) {
        return newConstructor(PrometheusCounter.class, new Object[] { id });
    }

    public static Timer newPrometheusTimer(Meter.Id id) {
        return newConstructor(PrometheusTimer.class, new Object[] { id });
    }

    public static DistributionSummary newPrometheusDistributionSummary(Meter.Id id) {
        return newConstructor(PrometheusDistributionSummary.class, new Object[] { id });
    }

    private static final Map<Class<?>, Constructor<?>> CONSTRUCTOR_MAP = new ConcurrentHashMap<>(8);

    @SuppressWarnings("unchecked")
    static <T> T newConstructor(Class<T> clazz, Object... args) {
        try {
            Constructor<T> constructor = (Constructor<T>) CONSTRUCTOR_MAP.get(clazz);
            if (isNull(constructor)) {
                synchronized (UmcMetricsFacade.class) {
                    if (isNull(constructor)) {
                        CONSTRUCTOR_MAP.put(clazz, constructor = getConstructor0(clazz));
                    }
                }
            }
            return (T) constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> Constructor<T> getConstructor0(Class<T> clazz) throws NoSuchMethodException, SecurityException {
        Constructor<T> constructor = clazz.getDeclaredConstructor(Meter.Id.class);
        constructor.setAccessible(true);
        return constructor;
    }

}
