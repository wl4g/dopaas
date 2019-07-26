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
import com.wl4g.devops.umc.handler.AlarmConfigHandler;
import com.wl4g.devops.umc.notification.AlarmNotifier.SimpleAlarmMessage;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_TEMPLATE_HIS;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static java.lang.Math.abs;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract collection metric valve alerter.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 */
public abstract class AbstractIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected RuleConfigManager ruleConfigManager;

	@Autowired
	protected AlarmConfigHandler alarmConfigHandler;

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
	protected long extractLargestRuleWindowKeepTime(List<AlarmRule> rules) {
		long largestTimeWindow = 0;
		for (AlarmRule alarmRule : rules) {
			Long timeWindow = alarmRule.getContinuityTime();
			if ((timeWindow != null ? timeWindow : 0) > largestTimeWindow) {
				largestTimeWindow = alarmRule.getContinuityTime();
			}
		}
		return largestTimeWindow;
	}

	/**
	 * Offer and update metric values in time windows.
	 */
	protected List<MetricValue> offerMetricValuesTimeWindow(Serializable templateId, Double value, long timestamp, long now,
			long ttl) {
		String slipTimeWindowKey = getLatestSlipTimeWindowCacheKey(templateId);
		List<MetricValue> metricValues = jedisService.getObjectList(slipTimeWindowKey, MetricValue.class);

		// Clean expired metrics.
		Iterator<MetricValue> it = safeList(metricValues).iterator();
		while (it.hasNext()) {
			if (abs(now - it.next().getTimestamp()) >= ttl) {
				it.remove();
			}
		}

		jedisService.listObjectAdd(slipTimeWindowKey, new MetricValue(timestamp, value));
		return metricValues;
	}

	/**
	 * Matching tags
	 */
	protected boolean matchTags(Map<String, String> metricTagMap, Map<String, String> tplTagMap) {
		if (isEmpty(tplTagMap)) {
			return false;
		}
		for (Entry<String, String> ent : tplTagMap.entrySet()) {
			if (StringUtils.equals(metricTagMap.get(ent.getKey()), ent.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Found sent to who by template
	 */
	protected void notification(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs) {
		for (AlarmConfig alarmConfig : alarmConfigs) {
			if (isBlank(alarmConfig.getAlarmMember())) {
				continue;
			}
			// Alarm notifier members.
			String[] alarmMembers = alarmConfig.getAlarmMember().split(",");
			if (log.isInfoEnabled()) {
				log.info("Alarm notification for templateId: {}, notifierType: {}, notifierTo: {}, content: {}",
						alarmTemplate.getId(), alarmConfig.getAlarmType(), alarmConfig.getAlarmMember(),
						alarmConfig.getAlarmContent());
			}
			// Alarm to multiple notifiers.
			notifier.simpleNotify(
					new SimpleAlarmMessage(alarmConfig.getAlarmContent(), alarmConfig.getAlarmType(), alarmMembers));
		}
	}

	protected static String getLatestSlipTimeWindowCacheKey(Serializable templateId) {
		return KEY_CACHE_TEMPLATE_HIS + templateId;
	}

}