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

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.USE_GROUP;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.umc.rule.AggregatorType.of;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.MetricValue;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.rule.OperatorType;
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

		List<AlarmTemplate> alarmTpls = null;
		String serviceId = null;
		String groupId = null;
		if (StringUtils.equals(aggWrap.getCollectId(), USE_GROUP)) {
			groupId = ruleConfigManager.transformToCollectGroupId(aggWrap.getClassify());
			alarmTpls = ruleConfigManager.getGroupIdAlarmRuleTpls(groupId);
		} else {
			serviceId = ruleConfigManager.transformToCollectId(aggWrap.getCollectId());
			alarmTpls = ruleConfigManager.getCollectIdAlarmRuleTpls(serviceId);
		}
		if (isEmpty(alarmTpls)) {
			return;
		}

		long now = System.currentTimeMillis();
		for (MetricWrapper metricWrap : aggWrap.getMetrics()) {
			String metricName = metricWrap.getMetric();
			for (AlarmTemplate tpl : alarmTpls) {
				if (StringUtils.equals(metricName, tpl.getMetric())) {
					// Match tags
					if (!matchTags(metricWrap.getTags(), tpl.getTagsMap())) {
						continue;
					}
					// largest metric keep time window of rules.
					long largestRuleWindowKeepTime = extractLargestRuleWindowKeepTime(tpl.getRules());

					// Extract latest metrics in time window
					List<MetricValue> metricVals = offerMetricValuesTimeWindow(tpl.getId(), metricWrap.getValue(),
							aggWrap.getTimestamp(), now, largestRuleWindowKeepTime);

					// Matching alarm rules of metric values.
					List<AlarmRule> matchedRules = matchAlarmRules(metricVals, tpl.getRules(), now);
					if (!isEmpty(matchedRules)) {
						if (log.isInfoEnabled()) {
							log.info("Matched to metric: {} and rule template: {}, time window data: {}", metricName, tpl.getId(),
									toJSONString(metricVals));
						}
						storageNotification(aggWrap.getCollectId(), serviceId, groupId, tpl, aggWrap.getTimestamp(),
								matchedRules);
					} else if (log.isDebugEnabled()) {
						log.debug("No match to metric: {} and rule template: {}, time window data: {}", metricName, tpl.getId(),
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
		// Match mode for 'OR'.
		return safeList(rules).stream().map(rule -> {
			// Get latest time window metric values.
			Double[] vals = extractAvailableTimeWindowMetricValues(metricVals, rule.getContinuityTime(), now);
			// Do inspection.
			OperatorType oper = OperatorType.of(rule.getOperator());
			if (inspector.verify(new InspectWrapper(oper, of(rule.getAggregator()), rule.getValue(), vals))) {
				return rule;
			}
			return null;
		}).collect(toList());
	}

	/**
	 * Offer and update metric values in time windows.
	 */
	protected List<MetricValue> offerMetricValuesTimeWindow(Serializable templateId, Double value, long timestamp, long now,
			long ttl) {
		String timeWindowKey = getLatestSlipTimeWindowCacheKey(templateId);
		List<MetricValue> metricVals = jedisService.getObjectList(timeWindowKey, MetricValue.class);

		// Clean expired metrics.
		Iterator<MetricValue> it = safeList(metricVals).iterator();
		while (it.hasNext()) {
			if (abs(now - it.next().getTimestamp()) >= ttl) {
				it.remove();
			}
		}

		jedisService.listObjectAdd(timeWindowKey, new MetricValue(timestamp, value));
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
	protected Double[] extractAvailableTimeWindowMetricValues(List<MetricValue> metricVals, long durationMs, long now) {
		List<Double> values = new ArrayList<>();
		for (MetricValue val : metricVals) {
			if ((now - val.getTimestamp()) < durationMs) {
				values.add(val.getValue());
			}
		}
		return values.toArray(new Double[values.size()]);
	}

	/**
	 * Storage and notification.
	 * 
	 * @param collectId
	 * @param serviceId
	 * @param groupId
	 * @param alarmTpl
	 * @param gatherTime
	 * @param macthedRules
	 */
	protected void storageNotification(String collectId, String serviceId, String groupId, AlarmTemplate alarmTpl,
			long gatherTime, List<AlarmRule> macthedRules) {
		List<AlarmConfig> alarmConfigs = null;
		if (StringUtils.equals(collectId, USE_GROUP)) {
			alarmConfigs = alarmConfigHandler.getAlarmConfigByGroupIdAndTemplateId(alarmTpl.getId(), groupId);
			// Storage record.
			alarmConfigHandler.saveRecord(alarmTpl, alarmConfigs, groupId, gatherTime, new Date(), macthedRules);
			// Notification
			notification(alarmTpl, alarmConfigs);
		} else {
			alarmConfigs = alarmConfigHandler.getAlarmConfigByCollectIdAndTemplateId(alarmTpl.getId(), serviceId);
			// Storage record.
			alarmConfigHandler.saveRecord(alarmTpl, alarmConfigs, serviceId, gatherTime, new Date(), macthedRules);
			// Notification
			notification(alarmTpl, alarmConfigs);
		}
	}

}