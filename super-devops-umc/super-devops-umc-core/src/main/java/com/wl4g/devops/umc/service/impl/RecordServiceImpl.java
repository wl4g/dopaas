package com.wl4g.devops.umc.service.impl;

import com.wl4g.devops.common.bean.umc.AlarmNotificationContact;
import com.wl4g.devops.common.bean.umc.AlarmRecord;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.dao.umc.*;
import com.wl4g.devops.umc.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-06 16:24:00
 */
@Service
public class RecordServiceImpl implements RecordService {

	@Autowired
	private AlarmRecordDao alarmRecordDao;

	@Autowired
	private AlarmRuleDao alarmRuleDao;

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AlarmNotificationContactDao alarmNotificationContactDao;

	@Override
	public AlarmRecord detail(Integer id) {
		Assert.notNull(id, "id is null");
		AlarmRecord alarmRecord = alarmRecordDao.selectByPrimaryKey(id);
		Assert.notNull(alarmRecord, "alarmRecord is null");
		List<AlarmRule> alarmRules = alarmRuleDao.selectByRecordId(id);
		AlarmTemplate alarmTemplate = alarmTemplateDao.selectByPrimaryKey(alarmRecord.getTemplateId());
		Assert.notNull(alarmTemplate, "alarmTemplate is null");
		List<AlarmNotificationContact> notificationContacts = alarmNotificationContactDao.getByRecordId(id);
		alarmRecord.setNotificationContacts(notificationContacts);
		alarmRecord.setAlarmRules(alarmRules);
		alarmRecord.setAlarmTemplate(alarmTemplate);
		return alarmRecord;
	}

}
