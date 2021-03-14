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
package com.wl4g.dopaas.umc.alarm;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.dopaas.common.bean.umc.AlarmConfig;
import com.wl4g.dopaas.common.bean.umc.AlarmRecord;
import com.wl4g.dopaas.common.bean.umc.AlarmRecordRule;
import com.wl4g.dopaas.common.bean.umc.AlarmRule;
import com.wl4g.dopaas.common.bean.umc.AlarmTemplate;
import com.wl4g.dopaas.umc.data.AlarmConfigDao;
import com.wl4g.dopaas.umc.data.AlarmRecordDao;
import com.wl4g.dopaas.umc.data.AlarmRecordRuleDao;
import com.wl4g.dopaas.umc.handler.AlarmConfigurer;
import com.wl4g.iam.common.bean.Contact;
import com.wl4g.iam.common.bean.NotificationContact;
import com.wl4g.iam.service.ContactService;
import com.wl4g.iam.service.NotificationContactService;

/**
 * Service metric indicators rule handler.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class ServiceRuleConfigurer implements AlarmConfigurer {

	private @Autowired AlarmConfigDao alarmConfigDao;
	private @Autowired AlarmRecordDao alarmRecordDao;
	private @Autowired AlarmRecordRuleDao alarmRecordRuleDao;
	private @Autowired ContactService contactService;
	private @Autowired NotificationContactService notificationContactService;

	/**
	 * Large search from db
	 */
	@Override
	public List<AlarmConfig> findAlarmConfigByEndpoint(String host, String endpoint) {
		List<AlarmConfig> configs = alarmConfigDao.getAlarmConfigTpls(host, endpoint);
		return configs;
	}

	@Override
	public List<AlarmConfig> findAlarmConfig(Long templateId, String collectAddr) {
		return alarmConfigDao.getByCollectAddrAndTemplateId(templateId, collectAddr);
	}

	@Override
	// @Transactional
	public AlarmRecord saveAlarmRecord(AlarmTemplate alarmTemplate, Long gatherTime, List<AlarmRule> rules, String alarmNote) {
		AlarmRecord record = new AlarmRecord();
		record.preInsert();
		record.setName(alarmTemplate.getMetric());
		record.setTemplateId(alarmTemplate.getId());
		record.setGatherTime(new Date(gatherTime));
		record.setCreateTime(new Date());
		record.setAlarmNote(alarmNote);
		alarmRecordDao.insertSelective(record);
		// Alarm matched rules.
		for (AlarmRule rule : rules) {
			AlarmRecordRule recordRule = new AlarmRecordRule();
			recordRule.preInsert();
			recordRule.setRecordId(record.getId());
			recordRule.setRuleId(rule.getId());
			recordRule.setCompareValue(rule.getCompareValue());
			alarmRecordRuleDao.insertSelective(recordRule);
		}
		return record;
	}

	@Override
	public List<Contact> getContactByGroupIds(List<Long> groupIds) {
		return contactService.findContactByGroupIds(groupIds);
	}

	@Override
	public NotificationContact saveNotificationContact(NotificationContact notificationContact) {
		notificationContact.preInsert();
		notificationContactService.save(notificationContact);
		return notificationContact;
	}

}