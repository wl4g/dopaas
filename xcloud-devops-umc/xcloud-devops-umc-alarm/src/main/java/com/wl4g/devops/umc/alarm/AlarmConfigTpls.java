/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.umc.alarm;

import com.wl4g.components.core.bean.umc.AlarmConfig;
import com.wl4g.components.core.bean.umc.AlarmTemplate;
import com.wl4g.components.core.bean.iam.Contact;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-20 10:35:00
 */
public class AlarmConfigTpls {

	private AlarmConfig alarmConfig;

	private AlarmTemplate alarmTemplate;

	private List<Contact> contacts;

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

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
}