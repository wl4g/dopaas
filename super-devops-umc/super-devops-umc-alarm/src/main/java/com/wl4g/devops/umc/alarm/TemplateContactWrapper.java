package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-08-22 15:16:00
 */
public class TemplateContactWrapper {
	private int templateId;
	private AlarmTemplate alarmTemplate;
	private List<AlarmContact> contacts;
	private Map<String, String> matchedTag;
	private List<AlarmRule> matchedRules;
	private MetricAggregateWrapper aggregateWrap;

	public TemplateContactWrapper(int templateId, AlarmTemplate alarmTemplate, List<AlarmContact> contacts,
			Map<String, String> matchedTag, List<AlarmRule> matchedRules, MetricAggregateWrapper aggregateWrap) {
		this.templateId = templateId;
		this.alarmTemplate = alarmTemplate;
		this.contacts = contacts;
		this.matchedTag = matchedTag;
		this.matchedRules = matchedRules;
		this.aggregateWrap = aggregateWrap;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public AlarmTemplate getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(AlarmTemplate alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}

	public List<AlarmContact> getContacts() {
		return contacts;
	}

	public void setContacts(List<AlarmContact> contacts) {
		this.contacts = contacts;
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

	public MetricAggregateWrapper getAggregateWrap() {
		return aggregateWrap;
	}

	public void setAggregateWrap(MetricAggregateWrapper aggregateWrap) {
		this.aggregateWrap = aggregateWrap;
	}
}
