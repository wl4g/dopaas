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
package com.wl4g.devops.umc.alarm;

import com.wl4g.components.core.bean.umc.*;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRecord;
import com.wl4g.devops.common.bean.umc.AlarmRecordRule;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.dao.umc.*;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.iam.bean.Contact;
import com.wl4g.iam.bean.NotificationContact;
import com.wl4g.iam.dao.ContactDao;
import com.wl4g.iam.dao.NotificationContactDao;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Service metric indicators rule handler.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class ServiceRuleConfigurer implements AlarmConfigurer {

	@Autowired
	protected JedisService jedisService;

	@Autowired
	private AlarmConfigDao alarmConfigDao;

	@Autowired
	private AlarmRecordDao alarmRecordDao;

	@Autowired
	private AlarmRecordRuleDao alarmRecordRuleDao;

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private NotificationContactDao notificationContactDao;

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
		return contactDao.getContactByGroupIds(groupIds);
	}

	@Override
	public NotificationContact saveNotificationContact(NotificationContact notificationContact) {
		notificationContact.preInsert();
		notificationContactDao.insertSelective(notificationContact);
		return notificationContact;
	}

}