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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.ApplicationArguments;

import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.infra.core.task.ApplicationTaskRunner;
import com.wl4g.dopaas.umc.client.collector.ScrapeCollectorAutoConfiguration.ScrapeCollectorProperties;
import com.wl4g.dopaas.umc.client.metrics.UmcMetricsFacade;

/**
 * {@link MetricsCollectorRunner}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-30 v1.0.0
 * @since v1.0.0
 */
public class MetricsCollectorRunner extends ApplicationTaskRunner<ScrapeCollectorProperties> {
    protected final SmartLogger log = getLogger(getClass());

    private final List<MetricsCollector> collectors;
    private final UmcMetricsFacade metricsFacade;

    public MetricsCollectorRunner(ScrapeCollectorProperties config, List<MetricsCollector> collectors,
            UmcMetricsFacade metricsFacade) {
        super(config);
        this.collectors = notNullOf(collectors, "collectors");
        this.metricsFacade = notNullOf(metricsFacade, "metricsFacade");
    }

    @Override
    public void onApplicationStarted(ApplicationArguments args, SafeScheduledTaskPoolExecutor worker) throws Exception {
        log.info("Initializating metrics collectors ... ", collectors);
        safeList(collectors).forEach(c -> worker.scheduleWithFixedDelay(() -> {
            try {
                log.debug("Collecting metrics for {} ... ", c);
                c.collect(metricsFacade);
            } catch (Exception e) {
                c.logger().warn(format("Failed to collect metrics for - %", c), e);
            }
        }, getConfig().getInitialDelayMs(), getConfig().getDelayMs(), TimeUnit.MILLISECONDS));
    }

}
