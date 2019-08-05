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

import com.wl4g.devops.common.bean.umc.*;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmRecordDao;
import com.wl4g.devops.dao.umc.AlarmRecordRuleDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AlarmConfigDao alarmConfigDao;

	@Autowired
	private AlarmRecordDao alarmRecordDao;

	@Autowired
	private AlarmRecordRuleDao alarmRecordRuleDao;


	@Override
	public List<AlarmTemplate> findAlarmTemplate(Integer collectId) {
		return alarmTemplateDao.getByCollectId(collectId);
	}

	@Override
	public List<AlarmConfig> findAlarmConfig(Integer templateId, String collectId) {
		return alarmConfigDao.getByCollectIdAndTemplateId(templateId, Integer.parseInt(collectId));
	}

	@Transactional
	public void saveAlarmRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, String collectId, Long gatherTime,
			List<AlarmRule> rules) {
		for (AlarmConfig alarmConfig : alarmConfigs) {
			AlarmRecord record = new AlarmRecord();
			record.setConfigId(alarmConfig.getId());
			record.setGatherTime(new Date(gatherTime));
			record.setCreateTime(new Date());
			//TODO
			record.setAlarmInfo("//TODO");
			alarmRecordDao.insertSelective(record);

			// Alarm matched rules.
			for (AlarmRule rule : rules) {
				AlarmRecordRule recordRule = new AlarmRecordRule();
				recordRule.setRecordId(record.getId());
				recordRule.setRuleId(rule.getId());
				alarmRecordRuleDao.insertSelective(recordRule);
			}

			// Alarm notification users.
			Integer contactGroupId = alarmConfig.getContactGroupId();
			//TODO 根据联系分组，找到联系人，生成notification

		}

	}

}