package com.wl4g.devops.umc.store.opentsdb.client.bean.request;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 构建数据点对象
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午1:52
 * @Version: 1.0
 */
public class Point {

    private String metric;

    private Map<String, String> tags = new HashMap<>();

    private Number value;

    private long timestamp;

    public static MetricBuilder metric(String metric) {
        return new MetricBuilder(metric);
    }

    public static class MetricBuilder {

        private String metric;

        private Map<String, String> tags = new HashMap<>();

        private Number value;

        private long timestamp;

        public MetricBuilder(String metric) {
            if (StringUtils.isBlank(metric)) {
                throw new IllegalArgumentException("The metric can't be empty");
            }
            this.metric = metric;
        }

        public MetricBuilder value(long timestamp, Number value) {
            if (timestamp == 0) {
                throw new IllegalArgumentException("timestamp must gt 0");
            }
            Objects.requireNonNull(value);
            this.timestamp = timestamp;
            this.value = value;
            return this;
        }

        public MetricBuilder tag(final String tagName, final String value) {
            if (StringUtils.isNoneBlank(tagName) && StringUtils.isNoneBlank(value)) {
                tags.put(tagName, value);
            }
            return this;
        }

        public MetricBuilder tag(final Map<String, String> tags) {
            if (!MapUtils.isEmpty(tags)) {
                this.tags.putAll(tags);
            }
            return this;
        }

        public Point build() {
            Point point = new Point();
            point.metric = this.metric;

            if (MapUtils.isEmpty(tags)) {
                throw new IllegalArgumentException("tags can't be empty");
            }
            point.tags = this.tags;

            point.timestamp = this.timestamp;
            point.value = this.value;
            return point;
        }

    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
