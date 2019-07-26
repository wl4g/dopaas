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

import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.*;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.dao.umc.*;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.handler.AlarmConfigHandler;

import org.apache.commons.lang3.StringUtils;
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
public class ServiceRuleConfigHandler implements AlarmConfigHandler {

	@Autowired
	protected JedisService jedisService;

	@Autowired
	private AppGroupDao appGroupDao;

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AlarmConfigDao alarmConfigDao;

	@Autowired
	private AlarmRecordDao alarmRecordDao;

	@Autowired
	private AlarmRecordRuleDao alarmRecordRuleDao;

	@Autowired
	private AlarmRecordUserDao alarmRecordUserDao;

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appGroupDao.instancelist(appInstance);
	}

	@Override
	public AppGroup getAppGroupByName(String groupName) {
		return appGroupDao.getAppGroupByName(groupName);
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByCollectId(Integer collectId) {
		return alarmTemplateDao.getByCollectId(collectId);
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByGroupId(Integer groupId) {
		return alarmTemplateDao.getByGroupId(groupId);
	}

	@Override
	public List<AlarmConfig> getAlarmConfigByCollectIdAndTemplateId(Integer templateId, Integer collectId) {
		return alarmConfigDao.getByCollectIdAndTemplateId(templateId, collectId);
	}

	@Override
	public List<AlarmConfig> getAlarmConfigByGroupIdAndTemplateId(Integer templateId, Integer groupId) {
		return alarmConfigDao.getByGroupIdAndTemplateId(templateId, groupId);
	}

	@Transactional
	public void saveRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, Integer collectId, Long gatherTime,
			Date nowDate, List<AlarmRule> rules) {
		for (AlarmConfig alarmConfig : alarmConfigs) {
			AlarmRecord alarmRecord = new AlarmRecord();
			alarmRecord.setTemplateId(alarmTemplate.getId());
			alarmRecord.setCollectId(collectId);
			alarmRecord.setName(alarmConfig.getName());
			alarmRecord.setGatherTime(new Date(gatherTime));
			alarmRecord.setAlarmTime(nowDate);
			alarmRecord.setAlarmInfo(alarmConfig.getAlarmContent());
			alarmRecord.setAlarmType(alarmConfig.getAlarmType());
			alarmRecordDao.insertSelective(alarmRecord);

			// TODO batch save is better
			for (AlarmRule alarmRule : rules) {
				AlarmRecordRule alarmRecordRule = new AlarmRecordRule();
				alarmRecordRule.setRecordId(alarmRecord.getId());
				alarmRecordRule.setRuleId(alarmRule.getId());
				alarmRecordRuleDao.insertSelective(alarmRecordRule);
			}
			String memberStr = alarmConfig.getAlarmMember();
			if (StringUtils.isBlank(memberStr)) {
				return;
			}
			String members[] = memberStr.split(",");
			for (String s : members) {
				AlarmRecordUser alarmRecordUser = new AlarmRecordUser();
				alarmRecordUser.setRecordId(alarmRecord.getId());
				alarmRecordUser.setUserId(Integer.parseInt(s));
				alarmRecordUserDao.insertSelective(alarmRecordUser);
			}
		}
	}

}