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
package com.wl4g.dopaas.umc.client.collector;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.common.task.RunnerProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link ScrapeCollectorAutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-30 v1.0.0
 * @since v1.0.0
 */
@Configuration
public class ScrapeCollectorAutoConfiguration {
    protected final SmartLogger log = getLogger(getClass());

    @Bean
    public ScrapeCollectorProperties scrapeCollectorProperties() {
        return new ScrapeCollectorProperties();
    }

    @Bean
    public MetricsCollector emptyMetricsCollector() {
        return new MetricsCollector() {

            @Override
            public void collect() {
            }

        };
    }

    @Bean
    public MetricsCollectorService metricsCollectorService(ScrapeCollectorProperties config, List<MetricsCollector> collectors) {
        return new MetricsCollectorService(config, collectors);
    }

    @Getter
    @Setter
    public static class ScrapeCollectorProperties extends RunnerProperties {
        private static final long serialVersionUID = 3370292085364076405L;

        private int initialDelayMs = 3_000;
        private int delayMs = 15_000;

        public ScrapeCollectorProperties() {
            setStartupMode(StartupMode.NOSTARTUP);
            setConcurrency(1);
        }
    }

}
