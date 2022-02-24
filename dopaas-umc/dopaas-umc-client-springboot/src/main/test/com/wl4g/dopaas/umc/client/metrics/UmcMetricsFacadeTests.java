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

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.wl4g.infra.common.lang.FastTimeClock;
import com.wl4g.infra.common.lang.ThreadUtils2;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusCounter;
import io.micrometer.prometheus.PrometheusMeterRegistry;

/**
 * {@link UmcMetricsFacadeTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-16 v1.0.0
 * @since v1.0.0
 */
public class UmcMetricsFacadeTests {

    // Simulate spring to instantiated bean, the production environment should
    // use spring injection.
    private final UmcMetricsFacade metricsFacade = new UmcMetricsFacade(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));

    @Test
    public void testNewConstructor() {
        Meter.Id id = new Meter.Id("test_metric1", Tags.empty(), "", "Nothing", Type.COUNTER);
        PrometheusCounter counter = UmcMetricsFacade.newConstructor(PrometheusCounter.class, id);
        System.out.println(counter);
    }

    @Test
    public void testUseCounter() {
        // Gets or create counter by metrics name and tags.
        Counter counter = metricsFacade.counter("test_metric2", new ImmutableTag("key1", "value1"));

        // increment by 1
        counter.increment(1);

        System.out.println(counter.count());
        System.out.println(toJSONString(counter));
    }

    @Test
    public void testUseTimer() {
        // Gets or create timer by metrics name and tags.
        Timer timer = metricsFacade.timer("test_metric3", new ImmutableTag("key1", "value1"));

        // statistics cost time
        long begin = FastTimeClock.currentTimeMillis();
        ThreadUtils2.sleep(200L);
        long end = FastTimeClock.currentTimeMillis();

        timer.record((end - begin), TimeUnit.MILLISECONDS);

        System.out.println("count: " + timer.count());
        System.out.println("total: " + timer.totalTime(TimeUnit.MILLISECONDS));
        System.out.println("max: " + timer.max(TimeUnit.MILLISECONDS));
        System.out.println("mean: " + timer.mean(TimeUnit.MILLISECONDS));
        System.out.println(toJSONString(timer));
    }

    @Test
    public void testUseGauge() {
        // Gets or create gauge by metrics name and tags, and record statistics
        // value.
        Number gauge = metricsFacade.gauge("test_metric4", 100.123d, new ImmutableTag("key1", "value1"));

        System.out.println(gauge);
    }

    @Test
    public void testUseSummary() {
        // Gets or create distribution summary by metrics name and tags.
        DistributionSummary summary = metricsFacade.summary("test_metric5", new ImmutableTag("key1", "value1"));

        // statistics cost time
        summary.record(200.123d);

        System.out.println("count: " + summary.count());
        System.out.println("max: " + summary.max());
        System.out.println("mean: " + summary.mean());
        System.out.println(toJSONString(summary));
    }

    @Test
    public void testUseSummary2() {
        // Gets or create distribution summary by configuration.
        Meter.Id id = new Meter.Id("test_metric6", Tags.empty(), "", "Nothing", Type.DISTRIBUTION_SUMMARY);
        DistributionSummary summary = metricsFacade.summary(id, DistributionStatisticConfig.DEFAULT, 0.01d);

        // statistics cost time
        summary.record(300.123d);

        System.out.println("count: " + summary.count());
        System.out.println("max: " + summary.max());
        System.out.println("mean: " + summary.mean());
        System.out.println(toJSONString(summary));
    }

}
