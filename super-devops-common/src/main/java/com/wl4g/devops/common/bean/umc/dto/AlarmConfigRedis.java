package com.wl4g.devops.common.bean.umc.dto;

import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vjay
 * @date 2019-07-04 16:54:00
 */
public class AlarmConfigRedis {


    private Integer instantId;

    private Set<Integer> alarmTemplateId = new HashSet<>();

    private List<AlarmTemplate> alarmTemplates = new ArrayList<>();

    public Integer getInstantId() {
        return instantId;
    }

    public void setInstantId(Integer instantId) {
        this.instantId = instantId;
    }

    public List<AlarmTemplate> getAlarmTemplates() {
        return alarmTemplates;
    }

    public void setAlarmTemplates(List<AlarmTemplate> alarmTemplates) {
        this.alarmTemplates = alarmTemplates;
    }

    public Set<Integer> getAlarmTemplateId() {
        return alarmTemplateId;
    }

    public void setAlarmTemplateId(Set<Integer> alarmTemplateId) {
        this.alarmTemplateId = alarmTemplateId;
    }
}
