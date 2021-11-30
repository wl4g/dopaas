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
package com.wl4g.dopaas.umc.client.health.timeout;

import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.umc.client.health.util.HealthUtil;

/**
 * Analysis and statistical call time dimension related health messages
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public class TimeoutMethodHealthIndicator extends AbstractHealthIndicator {
    private final SmartLogger log = getLogger(getClass());

    private final Map<String, Deque<Long>> records = new ConcurrentHashMap<>(64);
    private TimeoutMethodProperties config;

    public TimeoutMethodHealthIndicator(TimeoutMethodProperties config) {
        this.config = notNullOf(config, "simpleTimingMetricsConfig");
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        try {
            // Gets the statistical (MAX/MIN/AVG/LATEST/..).
            TimeStat stat = getLargestStat();
            log.debug("Times stat: {}", toJSONString(stat));
            if (isNull(stat)) {
                HealthUtil.down(builder, "No telemetry data");
                return;
            } else if (stat.getMax() < config.getTimeoutThresholdMs()) {
                HealthUtil.up(builder, "Healthy");
            } else {
                HealthUtil.down(builder,
                        new StringBuilder("Method ").append(stat.getMetricsName()).append(" executes ").append(stat.getLatest())
                                .append("ms with a response exceeding the threshold value of ")
                                .append(config.getTimeoutThresholdMs()).append("ms.").toString());
                // When the timeout exception is detected, the exception record
                // is cleared.
                resetIfNecessary(stat);
            }
            builder.withDetail("Method", stat.getMetricsName()).withDetail("Least", stat.getMin())
                    .withDetail("Largest", stat.getMax()).withDetail("Avg", stat.getAvg()).withDetail("Latest", stat.getLatest())
                    .withDetail("Samples", stat.getSamples()).withDetail("Threshold", config.getTimeoutThresholdMs() + "ms");
        } catch (Exception ex) {
            HealthUtil.down(builder, "UnHealthy", ex);
            log.error("Failed to detected timeout.method.calling", ex);
        }
    }

    public void record(String metricName, long time) {
        int latestCount = config.getSamples();
        log.debug("Add times metric: {}, latestCount: {}, time={}", metricName, latestCount, time);

        Deque<Long> deque = records.getOrDefault(metricName, new ConcurrentLinkedDeque<>());
        // Overflow check
        if (deque.size() >= (latestCount - 1)) {
            deque.poll(); // Remove first
        }
        deque.offer(time);
        records.put(metricName, deque);
    }

    /**
     * Gets the most statistics largest time out messages (including average
     * time, maximum duration, and longest time) of `latestMeasureCount` call
     * records.
     * 
     * @return
     */
    protected TimeStat getLargestStat() {
        List<TimeStat> stats = new ArrayList<>();
        // Calculate the maximum value of each execution record queue.
        for (Entry<String, Deque<Long>> ent : records.entrySet()) {
            Deque<Long> deque = ent.getValue();
            if (!deque.isEmpty()) {
                long queueMax = deque.stream().reduce(Long::max).get();
                stats.add(new TimeStat(ent.getKey(), queueMax));
            }
        }
        if (stats.isEmpty()) {
            return null;
        }

        // Gets the maximum value in the list of the maximum values in all
        // queues.
        TimeStat statMax = Collections.max(stats, MAX_COMPARATOR);
        Deque<Long> deque = records.get(statMax.getMetricsName());

        statMax.setSamples(deque.size());
        statMax.setMin(deque.stream().reduce(Long::min).get());
        statMax.setLatest(deque.peekLast());
        // Average length of time.
        long totalTime = deque.stream().collect(Collectors.summingLong(Long::longValue));
        long count = deque.stream().count();
        statMax.setAvg(BigDecimal.valueOf(totalTime / count).setScale(2, RoundingMode.HALF_EVEN).longValue());

        // Remove current-largest metrics(timeouts record).
        deque.remove(statMax.getMax());
        // Deque.clear();
        return statMax;
    }

    protected void resetIfNecessary(TimeStat stat) {
        Deque<Long> deque = records.get(stat.getMetricsName());
        if (deque != null && !deque.isEmpty()) {
            deque.remove(stat.getMax());
        }
    }

    private static final Comparator<TimeStat> MAX_COMPARATOR = new Comparator<TimeStat>() {
        @Override
        public int compare(TimeStat o1, TimeStat o2) {
            return (int) (o1.getMax() - o2.getMax());
        }
    };

}