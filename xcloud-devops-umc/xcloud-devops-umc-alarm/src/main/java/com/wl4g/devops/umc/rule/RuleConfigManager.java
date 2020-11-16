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
package com.wl4g.devops.umc.rule;

import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.components.support.redis.jedis.ScanCursor;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.core.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_TPLS;
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
	private AlarmConfigurer ruleConfigurer;

	@Override
	public void run(ApplicationArguments args) {
	}

	/**
	 * Clean rule to cache.
	 * 
	 * @param clearBatch
	 */
	public void clearAll(int clearBatch) {
		if (clearBatch <= 0) {
			clearBatch = 200;
		}
		String pattern = KEY_CACHE_ALARM_TPLS + "*";
		ScanCursor<?> cursor = jedisService.scan(pattern, clearBatch, null);
		int count = 0;
		for (String key : cursor.keysAsString()) {
			try {
				jedisService.del(key);
				++count;
			} catch (Exception e) {
				log.error(String.format("Failed to cleaning alarm tpls of '%s'", key), e);
			}
		}

		if (log.isInfoEnabled()) {
			log.info("Cleaned alarm templates: {}", count);
		}
	}

	/**
	 * Find alarm rule template by collectId.
	 * 
	 * @param clusterId
	 * @return
	 */
	public List<AlarmConfig> loadAlarmRuleTpls(String host, String endpoint) {
		String key = getCollectIdAlarmRulesCacheKey(host + ":" + endpoint);
		// First get the cache
		List<AlarmConfig> alarmTpls = jedisService.getObjectList(key, AlarmConfig.class);
		if (isEmpty(alarmTpls)) {
			alarmTpls = ruleConfigurer.findAlarmConfigByEndpoint(host, endpoint);
			if (!isEmpty(alarmTpls)) {
				jedisService.setObjectList(key, alarmTpls, 5);
			}
		}
		return safeList(alarmTpls);
	}

	// --- Cache key ---

	private static String getCollectIdAlarmRulesCacheKey(String collectAddr) {
		Assert.hasText(collectAddr, "'collectAddr' must not be empty");
		return KEY_CACHE_ALARM_TPLS + collectAddr;
	}

}