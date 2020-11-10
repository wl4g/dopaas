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
package com.wl4g.devops.umc.handler;

import com.wl4g.components.core.bean.umc.*;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRecord;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.iam.bean.Contact;
import com.wl4g.iam.bean.NotificationContact;

import java.util.List;

/**
 * Rule configuration handler.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface AlarmConfigurer {

	List<AlarmConfig> findAlarmConfigByEndpoint(String host, String endpoint);

	List<AlarmConfig> findAlarmConfig(Long templateId, String collectId);

	AlarmRecord saveAlarmRecord(AlarmTemplate alarmTemplate, Long gatherTime, List<AlarmRule> rules, String alarmNote);

	List<Contact> getContactByGroupIds(List<Long> groupIds);

	NotificationContact saveNotificationContact(NotificationContact notificationContact);

}