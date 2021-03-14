/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.umc.handler;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.paas.common.bean.umc.AlarmConfig;
import com.wl4g.paas.common.bean.umc.AlarmRecord;
import com.wl4g.paas.common.bean.umc.AlarmRule;
import com.wl4g.paas.common.bean.umc.AlarmTemplate;
import com.wl4g.iam.common.bean.Contact;
import com.wl4g.iam.common.bean.NotificationContact;

/**
 * Check required implements rule handler.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class CheckImpledAlarmConfigurer implements AlarmConfigurer, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Rule handlers must be implemented '%s'", AlarmConfigurer.class));
	}

	@Override
	public List<AlarmConfig> findAlarmConfigByEndpoint(String host, String endpoint) {
		return null;
	}

	@Override
	public List<AlarmConfig> findAlarmConfig(Long templateId, String collectId) {
		return null;
	}

	@Override
	public AlarmRecord saveAlarmRecord(AlarmTemplate alarmTemplate, Long gatherTime, List<AlarmRule> rules, String alarmNote) {
		return null;
	}

	@Override
	public List<Contact> getContactByGroupIds(List<Long> groupIds) {
		return null;
	}

	@Override
	public NotificationContact saveNotificationContact(NotificationContact notificationContact) {
		return null;
	}

}