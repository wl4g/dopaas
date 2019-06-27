package com.wl4g.devops.common.bean.umc.model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-27 18:31:00
 */
public class StatInfos implements Serializable {

    private static final long serialVersionUID = 381411777614066880L;

    private StatInfo[] statInfos;

    private Long timestamp;


    public static class StatInfo{

        private String metric;

        private Double value;

        private Map<String,String> tags;

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

    }

    public StatInfo[] getStatInfos() {
        return statInfos;
    }

    public void setStatInfos(StatInfo[] statInfos) {
        this.statInfos = statInfos;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
