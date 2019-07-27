package com.wl4g.devops.common.bean.umc.model;

import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Alarm rule information.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class AlarmRuleInfo {

	/** Metric collect point identity. */
	// private Integer collectId; // e.g. cf_instance.id

	// private Set<Integer> alarmTemplateId = new HashSet<>();

	private List<AlarmTemplate> alarmTemplates = new ArrayList<>();

	/*
	 * public Integer getCollectId() { return collectId; }
	 * 
	 * public void setCollectId(Integer collectId) { this.collectId = collectId;
	 * }
	 */

	public List<AlarmTemplate> getAlarmTemplates() {
		return alarmTemplates;
	}

	public void setAlarmTemplates(List<AlarmTemplate> alarmTemplates) {
		this.alarmTemplates = alarmTemplates;
	}

	/*
	 * public Set<Integer> getAlarmTemplateId() { return alarmTemplateId; }
	 * 
	 * public void setAlarmTemplateId(Set<Integer> alarmTemplateId) {
	 * this.alarmTemplateId = alarmTemplateId; }
	 */

}
