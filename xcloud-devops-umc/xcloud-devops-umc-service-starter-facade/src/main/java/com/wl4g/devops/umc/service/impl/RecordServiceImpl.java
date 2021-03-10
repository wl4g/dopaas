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
package com.wl4g.devops.umc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.umc.AlarmRecord;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.umc.data.AlarmRecordDao;
import com.wl4g.devops.umc.data.AlarmRuleDao;
import com.wl4g.devops.umc.data.AlarmTemplateDao;
import com.wl4g.devops.umc.service.RecordService;
import com.wl4g.iam.common.bean.NotificationContact;
import com.wl4g.iam.service.NotificationContactService;

/**
 * @author vjay
 * @date 2019-08-06 16:24:00
 */
@Service
public class RecordServiceImpl implements RecordService {

	private @Autowired AlarmRecordDao alarmRecordDao;
	private @Autowired AlarmRuleDao alarmRuleDao;
	private @Autowired AlarmTemplateDao alarmTemplateDao;
	private @Autowired NotificationContactService notificationContactService;

	@Override
	public PageHolder<AlarmRecord> list(PageHolder<AlarmRecord> pm, String name, String startDate, String endDate) {
		pm.startPage();
		pm.setRecords(alarmRecordDao.list(name, startDate, endDate));
		return pm;
	}

	@Override
	public AlarmRecord detail(Long id) {
		Assert.notNull(id, "id is null");
		AlarmRecord alarmRecord = alarmRecordDao.selectByPrimaryKey(id);
		Assert.notNull(alarmRecord, "alarmRecord is null");
		List<AlarmRule> alarmRules = alarmRuleDao.selectByRecordId(id);
		AlarmTemplate alarmTemplate = alarmTemplateDao.selectByPrimaryKey(alarmRecord.getTemplateId());
		Assert.notNull(alarmTemplate, "alarmTemplate is null");
		List<NotificationContact> notificationContacts = notificationContactService.getByRecordId(id);
		alarmRecord.setNotificationContacts(notificationContacts);
		alarmRecord.setAlarmRules(alarmRules);
		alarmRecord.setAlarmTemplate(alarmTemplate);
		return alarmRecord;
	}

}