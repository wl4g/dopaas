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
package com.wl4g.dopaas.umc.example.metrics;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.lang.ThreadUtils2;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.umc.client.metrics.UmcMetricsFacade;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Tag;

/**
 * {@link ExampleMetricsAutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-30 v1.0.0
 * @since v1.0.0
 */
@Configuration
public class ExampleMetricsAutoConfiguration {

    @Bean
    public ExampleMetricsCollector exampleMetricsCollector(UmcMetricsFacade metricsFacade) {
        return new ExampleMetricsCollector(metricsFacade);
    }

    public static class ExampleMetricsCollector implements ApplicationRunner {

        protected final SmartLogger log = getLogger(getClass());
        protected final UmcMetricsFacade metricsFacade;

        public ExampleMetricsCollector(UmcMetricsFacade metricsFacade) {
            this.metricsFacade = notNullOf(metricsFacade, "metricsFacade");
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
            log.info("Custom example metrics collector starting ...");

            new Thread(() -> {
                while (true) {
                    try {
                        log.info("Collection custom example metrics, Please visit to an example of indicators based on "
                                + "prometheus format, such as: curl http://localhost:8081 ...");

                        // ADD example counter metrics
                        Counter counter1 = metricsFacade.counter("example.mymetrics1.mycounter1",
                                Tag.of("instance", getLocalhost()));
                        counter1.increment(1);

                        // ADD example gauge metrics
                        metricsFacade.gauge("example.mymetrics2.mygauge1", 381.51d, Tag.of("instance", getLocalhost()));

                        // ADD example gauge metrics
                        DistributionSummary summary = metricsFacade.summary("example.mymetrics2.mysummary1",
                                new ImmutableTag("instance", getLocalhost()));
                        summary.record(101);

                    } catch (Exception e) {
                        log.error("", e);
                    } finally {
                        // sleep to next
                        ThreadUtils2.sleep(5000L);
                    }
                }
            }).start();
        }

        private static String getLocalhost() {
            try {
                return InetAddress.getLocalHost().toString();
            } catch (UnknownHostException e) {
                return "localhost";
            }
        }

    }
}
