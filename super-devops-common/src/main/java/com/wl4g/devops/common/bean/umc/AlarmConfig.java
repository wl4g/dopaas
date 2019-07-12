package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.scm.BaseBean;

public class AlarmConfig extends BaseBean {

	private String name;

	private Integer templateId;

	private String alarmType;

	private String alarmContent;

	private String alarmMember;

	private Integer groupId;

	public String getAlarmMember() {
		return alarmMember;
	}

	public void setAlarmMember(String alarmMember) {
		this.alarmMember = alarmMember;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType == null ? null : alarmType.trim();
	}

	public String getAlarmContent() {
		return alarmContent;
	}

	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent == null ? null : alarmContent.trim();
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
}