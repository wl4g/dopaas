package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmRule;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-08-20 15:19:00
 */
public class AlarmNote {

    private String collectorAddr;

    private String metricName;

    private Map<String, String> matchedTag;

    private List<AlarmRule> matchedRules;

    public String getCollectorAddr() {
        return collectorAddr;
    }

    public void setCollectorAddr(String collectorAddr) {
        this.collectorAddr = collectorAddr;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Map<String, String> getMatchedTag() {
        return matchedTag;
    }

    public void setMatchedTag(Map<String, String> matchedTag) {
        this.matchedTag = matchedTag;
    }

    public List<AlarmRule> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(List<AlarmRule> matchedRules) {
        this.matchedRules = matchedRules;
    }
}
