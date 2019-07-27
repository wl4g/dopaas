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
package com.wl4g.devops.umc.rule;

import com.google.common.net.HostAndPort;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.handler.AlarmConfigHandler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.List;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Rule configuration manager.
 * 
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-04 15:47:00
 */
public class RuleConfigManager implements ApplicationRunner {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private JedisService jedisService;

	@Autowired
	private AlarmConfigHandler ruleConfigurer;

	@Override
	public void run(ApplicationArguments args) {
		initializeCollectIds();
	}

	/**
	 * Initialize all collect IDs to the cache.
	 */
	private void initializeCollectIds() {
		AppInstance appInstance = new AppInstance();
		List<AppInstance> instancelist = ruleConfigurer.instancelist(appInstance);
		for (AppInstance i : instancelist) {
			jedisService.set(getCacheKeyIpAndPort(i.getIp() + ":" + i.getPort()), String.valueOf(i.getId()), 0);
		}
	}

	/**
	 * get collectId by collect,get from redis first ,if not found ,get from db
	 */
	public Integer transformToCollectId(String collectIpAndPort) {
		// check ip and port
		HostAndPort hostAndPort = HostAndPort.fromString(collectIpAndPort);

		String collectId = jedisService.get(getCacheKeyIpAndPort(collectIpAndPort));
		if (StringUtils.isBlank(collectId) && !StringUtils.equals(collectId, NOT_FOUND)) {
			AppInstance appInstance = new AppInstance();
			appInstance.setIp(hostAndPort.getHostText());
			appInstance.setPort(hostAndPort.getPortOrDefault(0));

			List<AppInstance> instances = ruleConfigurer.instancelist(appInstance);
			if (!isEmpty(instances)) {// found
				AppInstance appI = instances.get(0);
				collectId = String.valueOf(appI.getId());
				jedisService.set(getCacheKeyIpAndPort(collectIpAndPort), String.valueOf(appI.getId()), 0);
			} else {
				jedisService.set(getCacheKeyIpAndPort(collectIpAndPort), NOT_FOUND, 30);
			}
		}
		return Integer.parseInt(collectId);
	}

	/**
	 * Transform metric upstream ID to collect service groupId.
	 * 
	 * @param serviceId
	 * @return
	 */
	public String transformToCollectGroupId(String serviceId) {
		String groupId = jedisService.get(getCacheKeyGroup2Id(serviceId));
		if (StringUtils.isBlank(groupId) && !StringUtils.equals(groupId, NOT_FOUND)) {
			AppGroup appg = ruleConfigurer.getAppGroupByName(serviceId);
			if (null != appg) {
				groupId = appg.getId().toString();
				jedisService.set(getCacheKeyGroup2Id(serviceId), groupId, 0);
			} else {
				jedisService.set(getCacheKeyGroup2Id(serviceId), NOT_FOUND, 30);
			}
		}
		return groupId;
	}

	/**
	 * Clean rule to cache.
	 */
	public void clearAll() {
		jedisService.del("umc_alarm_66");
	}

	/**
	 * Get alarm rule template by collectId.
	 */
	public List<AlarmTemplate> getCollectIdAlarmRuleTpls(Integer collectId) {
		List<AlarmTemplate> alarmTpls = jedisService.getObjectList(getCollectIdAlarmRuleCacheKey(collectId), AlarmTemplate.class);
		if (isEmpty(alarmTpls)) {
			alarmTpls = ruleConfigurer.getAlarmTemplateByCollectId(collectId);
			if (isEmpty(alarmTpls)) {
				jedisService.setObjectAsJson(getCollectIdAlarmRuleCacheKey(collectId), NOT_FOUND, 30);
			} else {
				jedisService.setObjectAsJson(getCollectIdAlarmRuleCacheKey(collectId), alarmTpls, 0);
			}
		}
		return alarmTpls;
	}

	/**
	 * Get alarm rule template by groupId.
	 * 
	 * @param groupId
	 * @return
	 */
	public List<AlarmTemplate> getGroupIdAlarmRuleTpls(Integer groupId) {
		List<AlarmTemplate> alarmTpls = jedisService.getObjectList(getGroupIdAlarmRuleCacheKey(groupId), AlarmTemplate.class);
		if (isEmpty(alarmTpls)) {
			alarmTpls = ruleConfigurer.getAlarmTemplateByGroupId(groupId);
			if (isEmpty(alarmTpls)) {
				jedisService.setObjectAsJson(getGroupIdAlarmRuleCacheKey(groupId), NOT_FOUND, 30);
			} else {
				jedisService.setObjectAsJson(getGroupIdAlarmRuleCacheKey(groupId), alarmTpls, 0);
			}
		}
		return alarmTpls;
	}

	// --- Cache key ---

	private static String getCacheKeyIpAndPort(String collectIpAndPort) {
		return KEY_CACHE_INSTANCE_ID + collectIpAndPort;
	}

	private static String getCacheKeyGroup2Id(String groupName) {
		return KEY_CACHE_GROUP_ID + groupName;
	}

	private static String getCollectIdAlarmRuleCacheKey(Integer collectId) {
		return KEY_CACHE_ALARM_RULE_COLLECT + collectId;
	}

	private static String getGroupIdAlarmRuleCacheKey(Integer groupId) {
		return KEY_CACHE_ALARM_RULE_GROUP + groupId;
	}

}