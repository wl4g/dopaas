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
package com.wl4g.dopaas.umc.client.health;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.umc.client.health.advice.TimingMetricsAdvice.TimingMetricsProperties;
import com.wl4g.dopaas.umc.client.health.util.HealthUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * Analysis and statistical call time dimension related health messages
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public class TimingMethodHealthIndicator extends AbstractHealthIndicator {

    private final SmartLogger log = getLogger(getClass());
    private final Map<String, Deque<Long>> records = new ConcurrentHashMap<>(64);
    private TimingMetricsProperties config;

    public TimingMethodHealthIndicator(TimingMetricsProperties config) {
        this.config = notNullOf(config, "timeoutMetricsConfig");
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        try {
            // Gets the statistical (MAX/MIN/AVG/LATEST/..).
            TimesStat stat = getLargestStat();
            log.debug("Times stat: {}", toJSONString(stat));
            if (isNull(stat)) {
                HealthUtil.up(builder, "Healthy");
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
                this.postPropertiesReset(stat);
            }
            builder.withDetail("Method", stat.getMetricsName()).withDetail("Least", stat.getMin())
                    .withDetail("Largest", stat.getMax()).withDetail("Avg", stat.getAvg()).withDetail("Latest", stat.getLatest())
                    .withDetail("Samples", stat.getSamples()).withDetail("Threshold", config.getTimeoutThresholdMs() + "ms");

        } catch (Exception e) {
            builder.down(e);
            log.error("Analysis timeouts message failed.", e);
        }

    }

    public void addTimes(String metricName, long time) {
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
    private TimesStat getLargestStat() {
        List<TimesStat> stats = new ArrayList<>();
        // Calculate the maximum value of each execution record queue.
        for (Entry<String, Deque<Long>> ent : records.entrySet()) {
            Deque<Long> deque = ent.getValue();
            if (!deque.isEmpty()) {
                long queueMax = deque.stream().reduce(Long::max).get();
                stats.add(new TimesStat(ent.getKey(), queueMax));
            }
        }
        if (stats.isEmpty()) {
            return null;
        }

        // Gets the maximum value in the list of the maximum values in all
        // queues.
        TimesStat statMax = Collections.max(stats, MAX_COMPARATOR);
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

    private void postPropertiesReset(TimesStat stat) {
        Deque<Long> deque = records.get(stat.getMetricsName());
        if (deque != null && !deque.isEmpty()) {
            deque.remove(stat.getMax());
        }
    }

    @Configuration
    @ConditionalOnBean(TimingMetricsProperties.class)
    @AutoConfigureBefore({ TimingMetricsProperties.class })
    public static class TimeoutMethodHealthIndicatorAutoConfiguration {
        private final SmartLogger log = getLogger(getClass());

        @Bean
        public HealthIndicator timeoutMethodHealthIndicator(TimingMetricsProperties props) {
            log.info("Initializing timeout-methods healthIndicator. {}", props);
            if (props.getSamples() == 0) {
                throw new IllegalArgumentException("Latest measure count is 0.");
            }
            TimingMethodHealthIndicator indicator = new TimingMethodHealthIndicator(props);
            Map<String, Health> healths = new LinkedHashMap<String, Health>();
            healths.put(TimingMethodHealthIndicator.class.getSimpleName(), indicator.health());
            return indicator;
        }
    }

    @Getter
    @Setter
    public static class TimesStat {
        private String metricsName;
        private long max;
        private long min;
        private long avg;
        private int samples;
        private long latest;

        public TimesStat() {
            super();
        }

        public TimesStat(int samples, String metricsName, long max, long min, long avg, long latest) {
            super();
            this.samples = samples;
            this.metricsName = metricsName;
            this.max = max;
            this.min = min;
            this.avg = avg;
            this.latest = latest;
        }

        public TimesStat(String metricsName, long max) {
            super();
            this.metricsName = metricsName;
            this.max = max;
        }
    }

    private static final Comparator<TimesStat> MAX_COMPARATOR = new Comparator<TimesStat>() {
        @Override
        public int compare(TimesStat o1, TimesStat o2) {
            return (int) (o1.getMax() - o2.getMax());
        }
    };

}