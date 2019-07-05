package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.util.Date;

public class AlarmRecord extends BaseBean {

	private String name;

	private String tags;

	private Date gatherTime;

	private Date alarmTime;

	private String alarmInfo;

	private String alarmType;

	private String alarmMember;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags == null ? null : tags.trim();
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

	public String getAlarmMember() {
		return alarmMember;
	}

	public void setAlarmMember(String alarmMember) {
		this.alarmMember = alarmMember == null ? null : alarmMember.trim();
	}
}