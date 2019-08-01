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

import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.MetricValue;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.rule.inspect.RuleInspector.InspectWrapper;

/**
 * Default collection metric valve alerter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月24日
 * @since
 */
public class DefaultIndicatorsValveAlerter extends AbstractIndicatorsValveAlerter {

	public DefaultIndicatorsValveAlerter(AlarmProperties config) {
		super(config);
	}

	@Override
	protected void doHandleAlarm(MetricAggregateWrapper aggWrap) {
		if (log.isInfoEnabled()) {
			log.info("Alarm handling for collectId: {}", aggWrap.getCollectId());
		}

		// Load alarm templates by collectId.
		List<AlarmTemplate> alarmTpls = ruleConfigManager.loadAlarmRuleTpls(aggWrap.getCollectId());
		if (isEmpty(alarmTpls)) {
			if (log.isInfoEnabled()) {
				log.info("No found alarm templates for {}", aggWrap.getCollectId());
			}
			return;
		}

		// Handling alarm.
		long now = System.currentTimeMillis();
		for (MetricWrapper metricWrap : aggWrap.getMetrics()) {
			String metricName = metricWrap.getMetric();
			for (AlarmTemplate tpl : alarmTpls) {
				if (StringUtils.equals(metricName, tpl.getMetric())) {
					// Match tags
					if (!matchTags(metricWrap.getTags(), tpl.getTagsMap())) {
						continue;
					}

					// Maximum metric keep time window of rules.
					long maxWindowTime = extractMaxRuleWindowTime(tpl.getRules());
					// Offer latest metrics in time window queue.
					List<MetricValue> metricVals = offerTimeWindowQueue(tpl.getId(), metricWrap.getValue(),
							aggWrap.getTimestamp(), now, maxWindowTime);

					// Match alarm rules of metric values.
					List<AlarmRule> matchedRules = matchAlarmRules(metricVals, tpl.getRules(), now);
					if (!isEmpty(matchedRules)) {
						if (log.isInfoEnabled()) {
							log.info("Matched to metric: {} and alarm template: {}, time window queue: {}", metricName,
									tpl.getId(), toJSONString(metricVals));
						}

						// Storage & notification
						storageNotification(aggWrap.getCollectId(), tpl, aggWrap.getTimestamp(), matchedRules);
					} else if (log.isDebugEnabled()) {
						log.debug("No match to metric: {} and alarm template: {}, time window queue: {}", metricName, tpl.getId(),
								toJSONString(metricVals));
					}
				}
			}
		}

	}

	/**
	 * Match alarm rules.
	 */
	protected List<AlarmRule> matchAlarmRules(List<MetricValue> metricVals, List<AlarmRule> rules, long now) {
		// Match mode for 'OR'/'AND'.
		return safeList(rules).stream().map(rule -> {
			// Get latest time window metric values.
			Double[] vals = extractAvailableQueueMetricValues(metricVals, rule.getQueueTimeWindow(), now);
			// Do inspection.
			InspectWrapper wrap = new InspectWrapper(rule.getLogicalOperator(), rule.getRelateOperator(), rule.getAggregator(),
					rule.getValue(), vals);
			if (inspector.verify(wrap)) {
				return rule;
			}
			return null;
		}).collect(toList());
	}

	/**
	 * Offer metric values in time windows.
	 */
	protected List<MetricValue> offerTimeWindowQueue(Serializable tplId, Double value, long gatherTime, long now, long ttl) {
		String timeWindowKey = getTimeWindowQueueCacheKey(tplId);
		List<MetricValue> metricVals = jedisService.getObjectList(timeWindowKey, MetricValue.class);

		// Clean expired metrics.
		Iterator<MetricValue> it = safeList(metricVals).iterator();
		while (it.hasNext()) {
			if (abs(now - it.next().getGatherTime()) >= ttl) {
				it.remove();
			}
		}

		// Append to cache.
		jedisService.listObjectAdd(timeWindowKey, new MetricValue(gatherTime, value));
		return metricVals;
	}

	/**
	 * Extract a metrics of the latest duration time.
	 * 
	 * @param metricVals
	 * @param durationMs
	 * @param now
	 * @return
	 */
	protected Double[] extractAvailableQueueMetricValues(List<MetricValue> metricVals, long durationMs, long now) {
		List<Double> values = new ArrayList<>();
		for (MetricValue val : metricVals) {
			if ((now - val.getGatherTime()) < durationMs) {
				values.add(val.getValue());
			}
		}
		return values.toArray(new Double[values.size()]);
	}

	/**
	 * Storage and notification.
	 * 
	 * @param collectId
	 * @param collectId
	 * @param alarmTpl
	 * @param gatherTime
	 * @param macthedRules
	 */
	protected void storageNotification(String collectId, AlarmTemplate alarmTpl, long gatherTime, List<AlarmRule> macthedRules) {
		List<AlarmConfig> alarmConfigs = alarmConfigHandler.findAlarmConfig(alarmTpl.getId(), collectId);
		// Storage record.
		alarmConfigHandler.saveAlarmRecord(alarmTpl, alarmConfigs, collectId, gatherTime, macthedRules);
		// Notification
		notification(alarmTpl, alarmConfigs, macthedRules);
	}

}