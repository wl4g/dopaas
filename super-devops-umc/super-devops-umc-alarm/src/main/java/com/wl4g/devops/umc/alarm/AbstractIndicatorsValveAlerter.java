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

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.MetricValue;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_METRIC_QUEUE;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Abstract collection metric valve alerter.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 */
public abstract class AbstractIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** JEDIS service */
	final protected JedisService jedisService;

	/** Alarm configuration */
	final protected AlarmConfigurer configurer;

	/** Alarm rule manager */
	final protected RuleConfigManager ruleManager;

	/** Alarm rule inspector */
	final protected CompositeRuleInspectorAdapter inspector;

	/** Alarm notifier */
	final protected CompositeAlarmNotifierAdapter notifier;

	public AbstractIndicatorsValveAlerter(AlarmProperties config, JedisService jedisService, AlarmConfigurer configurer,
			RuleConfigManager ruleManager, CompositeRuleInspectorAdapter inspector, CompositeAlarmNotifierAdapter notifier) {
		super(config);
		Assert.notNull(jedisService, "JedisService is null, please check config.");
		Assert.notNull(configurer, "AlarmConfigurer is null, please check config.");
		Assert.notNull(ruleManager, "RuleManager is null, please check config.");
		Assert.notNull(inspector, "RuleInspector is null, please check config.");
		Assert.notNull(notifier, "AlarmNotifier is null, please check config.");
		this.jedisService = jedisService;
		this.configurer = configurer;
		this.ruleManager = ruleManager;
		this.inspector = inspector;
		this.notifier = notifier;
	}

	@Override
	public void run() {
		// Ignore
	}

	@Override
	public void alarm(MetricAggregateWrapper wrap) {
		getWorker().execute(() -> doHandleAlarm(wrap));
	}

	/**
	 * Do handling alarm.
	 * 
	 * @param agwrap
	 */
	protected abstract void doHandleAlarm(MetricAggregateWrapper agwrap);

	/**
	 * Extract largest metric keep time window of rules.
	 * 
	 * @param rules
	 * @return
	 */
	protected long extractMaxRuleWindowTime(List<AlarmRule> rules) {
		long largestTimeWindow = 0;
		for (AlarmRule alarmRule : rules) {
			Long timeWindow = alarmRule.getQueueTimeWindow();
			if ((timeWindow != null ? timeWindow : 0) > largestTimeWindow) {
				largestTimeWindow = alarmRule.getQueueTimeWindow();
			}
		}
		return largestTimeWindow;
	}

	/**
	 * Match metric tags
	 * 
	 * @param metricTagMap
	 * @param tplTagMap
	 * @return
	 */
	protected Map<String, String> matchTags(Map<String, String> metricTagMap, Map<String, String> tplTagMap) {
		// If no tag is configured, the matching tag does not need to be
		// executed.
		if (isEmpty(tplTagMap)) {
			return emptyMap();
		}
		Map<String, String> matchedTags = new HashMap<>();
		for (Entry<String, String> ent : tplTagMap.entrySet()) {
			if (trimToEmpty(ent.getValue()).equals(metricTagMap.get(ent.getKey()))) {
				matchedTags.put(ent.getKey(), ent.getValue());
			}
		}
		return matchedTags;
	}

	/**
	 * GET metric values queue by collect address.
	 * 
	 * @param collectAddr
	 * @return
	 */
	protected List<MetricValue> doPeekMetricValueQueue(String collectAddr) {
		String timeWindowKey = getTimeWindowQueueCacheKey(collectAddr);
		return jedisService.getObjectList(timeWindowKey, MetricValue.class);
	}

	/**
	 * Storage metric values to cache.
	 * 
	 * @param collectAddr
	 * @param ttl
	 * @param metricVals
	 */
	protected List<MetricValue> doOfferMetricValueQueue(String collectAddr, long ttl, List<MetricValue> metricVals) {
		String timeWindowKey = getTimeWindowQueueCacheKey(collectAddr);
		jedisService.setObjectList(timeWindowKey, metricVals, (int) ttl);
		return metricVals;
	}

	/**
	 * Notification of alarm template to users.
	 * 
	 * @param alarmTpl
	 * @param alarmConfigs
	 * @param macthedRules
	 */
	protected void notification(AlarmTemplate alarmTpl, List<AlarmConfig> alarmConfigs, List<AlarmRule> macthedRules) {

		// TODO 通知方式改变，没有了notifierType，通过contact下的配置来决定发送方式

		// for (AlarmConfig alarmConfig : alarmConfigs) {
		// if (isBlank(alarmConfig.getAlarmMember())) {
		// continue;
		// }
		// // Alarm notifier members.
		// String[] alarmMembers = alarmConfig.getAlarmMember().split(",");
		// if (log.isInfoEnabled()) {
		// log.info("Notification alarm for templateId: {}, notifierType: {},
		// to: {}, content: {}", alarmTemplate.getId(),
		// alarmConfig.getAlarmType(), alarmConfig.getAlarmMember(),
		// alarmConfig.getAlarmContent());
		// }
		// // Alarm to composite notifiers.
		// notifier.simpleNotify(
		// new SimpleAlarmMessage(alarmConfig.getAlarmContent(),
		// alarmConfig.getAlarmType(), alarmMembers));
		// }
	}

	//
	// --- Cache key. ---
	//

	protected String getTimeWindowQueueCacheKey(String collectAddr) {
		Assert.hasText(collectAddr, "Collect addr must not be empty");
		return KEY_CACHE_ALARM_METRIC_QUEUE + collectAddr;
	}

}