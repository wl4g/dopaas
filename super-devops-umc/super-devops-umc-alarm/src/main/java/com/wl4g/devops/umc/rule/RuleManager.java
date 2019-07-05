package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.support.cache.JedisService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_RULE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_INSTANCE_ID;

/**
 * @author vjay
 * @date 2019-07-04 15:47:00
 */
public class RuleManager {

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AlarmConfigDao alarmConfigDao;

	@Autowired
	private JedisService jedisService;

	@Autowired
	private AppGroupDao appGroupDao;

	private Map<Integer, Set<Integer>> tagToTemplate = new HashMap<>();

	public void putAllAlarmConfigIntoRedis() {

		// trun <templates,tags> to <tags,templates>
		Map<Integer, AlarmTemplate> alarmTemplateMap = getAllAlarmTemplate();
		List<AlarmConfig> alarmConfigs = alarmConfigDao.selectAll();
		for (AlarmConfig alarmConfig : alarmConfigs) {
			String tagsStr = alarmConfig.getTags();
			Integer templateId = alarmConfig.getTemplateId();
			String[] tags = tagsStr.split(",");
			for (String tag : tags) {
				int instandId = Integer.parseInt(tag);
				Set<Integer> set = tagToTemplate.get(instandId);
				if (null == set) {
					set = new HashSet<>();
				}
				set.add(templateId);
			}
		}

		// List<AlarmConfigRedis> alarmConfigRedisList = new ArrayList<>();
		for (Map.Entry<Integer, Set<Integer>> entry : tagToTemplate.entrySet()) {
			int instandId = entry.getKey();
			Set<Integer> temList = entry.getValue();
			AlarmRuleInfo alarmConfigRedis = new AlarmRuleInfo();
			alarmConfigRedis.setServiceId(instandId);
			alarmConfigRedis.setAlarmTemplateId(temList);
			List<AlarmTemplate> alarmTemplates = new ArrayList<>();
			for (Integer tem : temList) {
				alarmTemplates.add(alarmTemplateMap.get(tem));
			}
			alarmConfigRedis.setAlarmTemplates(alarmTemplates);
			// alarmConfigRedisList.add(alarmConfigRedis);

			// TODO
			String json = JacksonUtils.toJSONString(alarmConfigRedis);
			jedisService.set(KEY_CACHE_ALARM_RULE + String.valueOf(instandId), json, 0);

		}
	}

	private Map<Integer, AlarmTemplate> getAllAlarmTemplate() {
		Map<Integer, AlarmTemplate> alarmTemplateMap = new HashMap<>();
		List<AlarmTemplate> alarmTemplates = alarmTemplateDao.selectAllWithRule();
		for (AlarmTemplate alarmTemplate : alarmTemplates) {
			alarmTemplateMap.put(alarmTemplate.getId(), alarmTemplate);
		}
		return alarmTemplateMap;
	}

	public void putInstandIPToIdInRedis() {
		AppInstance appInstance = new AppInstance();
		List<AppInstance> instancelist = appGroupDao.instancelist(appInstance);
		for (AppInstance appInstance1 : instancelist) {
			jedisService.set(KEY_CACHE_INSTANCE_ID + appInstance1.getIp() + ":" + appInstance1.getPort(),
					String.valueOf(appInstance1.getId()), 0);
		}

	}

}
