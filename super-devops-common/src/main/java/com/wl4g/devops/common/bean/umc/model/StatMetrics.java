package com.wl4g.devops.common.bean.umc.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-27 18:31:00
 */
public class StatMetrics implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    private StatMetric[] statMetrics;

    private Long timestamp;


    public static class StatMetric{

        private String metric;

        private Double value;

        private Map<String,String> tags;

        private long timestamp;

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Map<String, String> getTags() {
            return tags;
        }

        public void setTags(Map<String, String> tags) {
            this.tags = tags;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public StatMetric[] getStatMetrics() {
        return statMetrics;
    }

    public void setStatMetrics(StatMetric[] statMetrics) {
        this.statMetrics = statMetrics;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
