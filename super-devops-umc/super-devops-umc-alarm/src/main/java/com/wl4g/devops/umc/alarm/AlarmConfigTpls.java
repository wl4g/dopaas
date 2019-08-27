package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-20 10:35:00
 */
public class AlarmConfigTpls {

	private AlarmConfig alarmConfig;

	private AlarmTemplate alarmTemplate;

	private List<AlarmContact> alarmContacts;

	public AlarmConfig getAlarmConfig() {
		return alarmConfig;
	}

	public void setAlarmConfig(AlarmConfig alarmConfig) {
		this.alarmConfig = alarmConfig;
	}

	public AlarmTemplate getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(AlarmTemplate alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}

	public List<AlarmContact> getAlarmContacts() {
		return alarmContacts;
	}

	public void setAlarmContacts(List<AlarmContact> alarmContacts) {
		this.alarmContacts = alarmContacts;
	}
}
