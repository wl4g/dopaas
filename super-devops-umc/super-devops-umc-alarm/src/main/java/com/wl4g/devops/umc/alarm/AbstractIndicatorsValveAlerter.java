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
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_METRIC_QUEUE;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Abstract collection metric valve alerter.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 */
public abstract class AbstractIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected RuleConfigManager ruleManager;

	@Autowired
	protected AlarmConfigurer configurer;

	@Autowired
	protected CompositeAlarmNotifierAdapter notifier;

	@Autowired
	protected CompositeRuleInspectorAdapter inspector;

	@Autowired
	protected JedisService jedisService;

	public AbstractIndicatorsValveAlerter(AlarmProperties config) {
		super(config);
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
	 * @param aggWrap
	 */
	protected abstract void doHandleAlarm(MetricAggregateWrapper aggWrap);

	/**
	 * Extract largest metric keep time window of rules.
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
	 */
	protected boolean matchTags(Map<String, String> metricTagMap, Map<String, String> tplTagMap) {
		// If no tag is configured, the matching tag does not need to be
		// executed.
		if (isEmpty(tplTagMap)) {
			return true;
		}
		for (Entry<String, String> ent : tplTagMap.entrySet()) {
			if (trimToEmpty(ent.getValue()).equals(metricTagMap.get(ent.getKey()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Notification of alarm template to users.
	 */
	protected void notification(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, List<AlarmRule> macthedRules) {

		//TODO 通知方式改变，没有了notifierType，通过contact下的配置来决定发送方式

		/*for (AlarmConfig alarmConfig : alarmConfigs) {
			if (isBlank(alarmConfig.getAlarmMember())) {
				continue;
			}
			// Alarm notifier members.
			String[] alarmMembers = alarmConfig.getAlarmMember().split(",");
			if (log.isInfoEnabled()) {
				log.info("Notification alarm for templateId: {}, notifierType: {}, to: {}, content: {}", alarmTemplate.getId(),
						alarmConfig.getAlarmType(), alarmConfig.getAlarmMember(), alarmConfig.getAlarmContent());
			}
			// Alarm to composite notifiers.
			notifier.simpleNotify(
					new SimpleAlarmMessage(alarmConfig.getAlarmContent(), alarmConfig.getAlarmType(), alarmMembers));
		}*/
	}

	//
	// --- Cache key. ---
	//

	protected static String getTimeWindowQueueCacheKey(Serializable templateId) {
		return KEY_CACHE_ALARM_METRIC_QUEUE + templateId;
	}

}