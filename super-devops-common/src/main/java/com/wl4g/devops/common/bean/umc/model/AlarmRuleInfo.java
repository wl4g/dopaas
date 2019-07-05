package com.wl4g.devops.common.bean.umc.model;

import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vjay
 * @date 2019-07-04 16:54:00
 */
public class AlarmRuleInfo {

	/** Metric collect point identity. */
	private Integer serviceId; // e.g. cf_instance.id

	private Set<Integer> alarmTemplateId = new HashSet<>();

	private List<AlarmTemplate> alarmTemplates = new ArrayList<>();

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer instantId) {
		this.serviceId = instantId;
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
