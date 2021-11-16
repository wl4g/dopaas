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

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.umc.client.health.store.MemorySampleStore;
import com.wl4g.dopaas.umc.client.health.store.SampleStore;
import com.wl4g.dopaas.umc.client.health.util.HealthUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * Custom operation system disk space performance indicator.<br/>
 * Support multi partition monitoring.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public abstract class BaseHealthIndicator extends AbstractHealthIndicator implements ApplicationRunner, Comparator<Long> {

    protected final SmartLogger log = getLogger(getClass());
    private final Map<String, SampleStore<Partition>> sampleStores = new ConcurrentHashMap<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AdvancedHealthProperties<? extends Partition> props;
    private @Autowired ExtensionHealthApplicationRunner processor;

    public BaseHealthIndicator(AdvancedHealthProperties<? extends Partition> props) {
        this.props = notNullOf(props, "props");
        log.info("Initializing health properties: {}", props);
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        int index = 0;
        StringBuilder desc = new StringBuilder(64);
        for (String name : sampleStores.keySet()) {
            try {
                SampleStore<Partition> store = sampleStores.get(name);
                Partition latestPart = store.latest();
                log.debug("Health performance：{}", toJSONString(latestPart));
                if (isNull(latestPart)) {
                    builder.up();
                    return;
                }

                // Gets partition configuration by current partition.
                Partition confPart = props.getPartitions().get(name);
                if (confPart == null)
                    throw new Error("It should not come here. " + name);

                // Check threshold.
                long threshold = confPart.getValue();
                long curVal = latestPart.getValue();
                if (compare(curVal, threshold) < 0) { // Trigger event.
                    if (desc.length() > 0) {
                        desc.append("<br/>");
                    }
                    desc.append(name).append("：").append(formatValue(curVal)).append(" exceed the threshold：")
                            .append(formatValue(confPart.getValue()));
                }
                // Meaningful field name prefix.
                String p = compareFieldName();
                builder.withDetail("AcqPosition_" + index, name).withDetail("AcqSamples_" + index, latestPart.getSamples())
                        .withDetail("AcqTime_" + index, latestPart.getFormatTimestamp())
                        .withDetail(p + "Avg_" + index, formatValue(store.average()))
                        .withDetail(p + "Largest_" + index, formatValue(store.largest().getValue()))
                        .withDetail(p + "Least_" + index, formatValue(store.least().getValue()))
                        .withDetail(p + "Latest_" + index, formatValue(store.latest().getValue()))
                        .withDetail(p + "Threshold_" + index, formatValue(threshold));
                // All the checks are normal.
                if (desc.length() > 0) {
                    HealthUtil.down(builder, desc.toString());
                    desc.setLength(0); // Reset.
                } else {
                    builder.up();
                }
                log.debug("Acquisition unhealthy description：{}", desc);
            } catch (Exception e) {
                builder.down(e);
                log.error("Advanced health check failed.", e);
            } finally {
                // Increase by 1
                ++index;
            }
        }

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (initialized.compareAndSet(false, true)) {
            registerToEventStores();
            submit();
        }
    }

    /**
     * Gets the latest monitoring information. (CPU/Memory/Disk etc)
     * 
     * @param name
     * @return
     * @throws Exception
     */
    protected abstract Partition latestPerfInfo(String name) throws Exception;

    /**
     * Collection value formatting
     * 
     * @param value
     * @return
     */
    protected abstract String formatValue(long value);

    /**
     * Name of comparison field for display
     * 
     * @return
     */
    protected abstract String compareFieldName();

    /**
     * Registration of multiple monitoring partitions corresponding to event
     * memory
     */
    private void registerToEventStores() {
        props.getPartitions().forEach((name, confPart) -> {
            if (!sampleStores.containsKey(name)) {
                sampleStores.put(name, new MemorySampleStore(confPart.getSamples(), confPart.getRetainTime()));
            }
        });
    }

    private void submit() {
        processor.submit(() -> {
            props.getPartitions().forEach((name, confPart) -> {
                try {
                    // Get latest performance information.
                    Partition part = latestPerfInfo(name);
                    log.debug("Performance info: part-name={}, {}", name, toJSONString(part));
                    sampleStores.get(name).save(part);
                } catch (Exception e) {
                    log.error("Gets performance failed.", e);
                }
            });
        });
    }

    /**
     * Abstract health indicator attribute configuration
     * 
     * @author Wangl.sir <983708408@qq.com>
     * @version v1.0
     * @date 2018年6月7日
     * @since
     */
    public abstract static class AdvancedHealthProperties<T extends Partition> {

        public abstract Map<String, T> getPartitions();

        public abstract void setPartitions(Map<String, T> partitions);

    }

    /**
     * Base abstract monitoring partition configuration, (example: multi core
     * CPU/ monitoring separately)
     * 
     * @author Wangl.sir <983708408@qq.com>
     * @version v1.0
     * @date 2018年6月7日
     * @since
     */
    @Getter
    @Setter
    public abstract static class Partition implements Comparable<Partition> {
        private int samples = MemorySampleStore.DEFAULT_CAPACITY;
        private long retainTime = MemorySampleStore.DEFAULT_RETAIN;
        private long timestamp;

        public abstract long getValue();

        public abstract void setValue(long value);

        public String getFormatTimestamp() {
            return new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(this.getTimestamp());
        }

        @Override
        public int compareTo(Partition o) {
            return (int) (this.getValue() - o.getValue());
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (int) (prime * result + getValue());
            result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Partition other = (Partition) obj;
            if (getValue() != other.getValue())
                return false;
            if (timestamp != other.getValue())
                return false;
            return true;
        }

    }

}