package com.wl4g.devops.common.bean.umc;

import java.util.Date;

import com.wl4g.devops.common.bean.BaseBean;

public class AlarmRecord extends BaseBean {

	private Integer templateId;

	private String name;

	private Integer collectId;

	private Date gatherTime;

	private Date alarmTime;

	private String alarmInfo;

	private String alarmType;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Integer getCollectId() {
		return collectId;
	}

	public void setCollectId(Integer collectId) {
		this.collectId = collectId;
	}

	public Date getGatherTime() {
		return gatherTime;
	}

	public void setGatherTime(Date gatherTime) {
		this.gatherTime = gatherTime;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo == null ? null : alarmInfo.trim();
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType == null ? null : alarmType.trim();
	}
}