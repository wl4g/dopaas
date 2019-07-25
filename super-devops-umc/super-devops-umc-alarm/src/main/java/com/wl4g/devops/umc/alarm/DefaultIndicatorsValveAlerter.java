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
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.umc.rule.AggregatorType.of;
import static com.wl4g.devops.umc.rule.OperatorType.of;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import static com.wl4g.devops.umc.rule.RuleConfigManager.*;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo.Point;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.rule.inspect.AvgRuleInspector;
import com.wl4g.devops.umc.rule.inspect.LastRuleInspector;
import com.wl4g.devops.umc.rule.inspect.MaxRuleInspector;
import com.wl4g.devops.umc.rule.inspect.MinRuleInspector;
import com.wl4g.devops.umc.rule.inspect.RuleInspector;
import com.wl4g.devops.umc.rule.inspect.SumRuleInspector;

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
	protected void doAlarmHandling(MetricAggregateWrapper aggWrap) {
		long now = System.currentTimeMillis();
		Date nowDate = new Date();
		String collectIp = aggWrap.getCollectId();
		if (log.isInfoEnabled()) {
			log.info("Alarm matching rule collectId={}", collectIp);
		}

		List<AlarmTemplate> alarmTpls = null;
		Integer collectId = null;
		Integer groupId = null;
		if (StringUtils.equals(collectIp, USE_GROUP)) {
			groupId = ruleConfigManager.convertServiceId(aggWrap.getClassify());
			AlarmRuleInfo alarmRuleInfo = ruleConfigManager.getAlarmRuleInfoByGroupId(groupId);
			alarmTpls = alarmRuleInfo.getAlarmTemplates();
		} else {
			collectId = ruleConfigManager.convertCollectId(collectIp);
			if (null == collectId) {
				return;
			}
			AlarmRuleInfo alarmRuleInfo = ruleConfigManager.getAlarmRuleInfoByCollectId(collectId);
			if (alarmRuleInfo != null) {
				alarmTpls = alarmRuleInfo.getAlarmTemplates();
			}
		}
		if (null == alarmTpls) {
			return;
		}

		for (MetricWrapper metricWrap : aggWrap.getMetrics()) {
			String metricName = metricWrap.getMetric();
			for (AlarmTemplate alarmTemplate : alarmTpls) {
				if (StringUtils.equals(metricName, alarmTemplate.getMetric())) {
					// Matching tags
					if (!matchTags(metricWrap.getTags(), alarmTemplate.getTags())) {
						continue;
					}

					// Inspection by rule.
					List<AlarmRule> rules = alarmTemplate.getRules();
					// largest metric keep time window of rules.
					Long largestRuleWindowKeepTime = extLargestRuleWindowKeepTime(rules);
					// get history point from redis
					List<Point> points = ruleConfigManager.duelTempalteInRedis(alarmTemplate.getId(), metricWrap.getValue(),
							aggWrap.getTimestamp(), now, largestRuleWindowKeepTime.intValue());

					List<AlarmRule> macthRule = new ArrayList<>();
					if (checkRuleMatch(points, rules, now, macthRule)) {
						log.info("match template rule,metricName={}, template_id={},historyData={}", metricName,
								alarmTemplate.getId(), JacksonUtils.toJSONString(points));

						storageAndNotification(collectIp, collectId, groupId, alarmTemplate, aggWrap.getTimestamp(), nowDate,
								macthRule);
					} else {
						log.debug("not match rule, needn't send msg");
					}
				}

			}
		}
	}

	/**
	 * Check rule is match
	 */
	protected boolean checkRuleMatch(List<Point> points, List<AlarmRule> rules, long now, List<AlarmRule> macthRule) {
		// match mode for 'OR'.
		boolean result = false;
		for (AlarmRule rule : rules) {
			// Get latest time window metric values.
			Double[] metricValues = getLatestTimeWindowMetricValues(rule.getContinuityTime(), points, now);
			// Get rule inspector.
			RuleInspector inspector = createRuleInspector(rule.getAggregator());
			// Do inspection verify.
			if (inspector.verify(metricValues, of(rule.getOperator()), rule.getValue())) {
				macthRule.add(rule);
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Create Rule inspector by aggregate.
	 * 
	 * @param aggregator
	 * @return
	 */
	protected RuleInspector createRuleInspector(String aggregator) {
		switch (of(aggregator)) {
		case AVG:
			return new AvgRuleInspector();
		case LAST:
			return new LastRuleInspector();
		case MAX:
			return new MaxRuleInspector();
		case MIN:
			return new MinRuleInspector();
		case SUM:
			return new SumRuleInspector();
		default:
			throw new UnsupportedOperationException(String.format("Unsupport aggregator type (%s)", aggregator));
		}
	}

	/**
	 * Get a metrics of the latest duration time.
	 * 
	 * @param durationMs
	 * @param points
	 * @param now
	 * @return
	 */
	protected Double[] getLatestTimeWindowMetricValues(long durationMs, List<Point> points, long now) {
		List<Double> values = new ArrayList<>();
		for (Point point : points) {
			if (now - point.getTimeStamp() < durationMs * 1000) {
				values.add(point.getValue());
			}
		}
		return values.toArray(new Double[values.size()]);
	}

	/**
	 * Matching tags
	 */
	@SuppressWarnings("unchecked")
	protected boolean matchTags(Map<String, String> tagsMap, String tplTags) {
		if (isBlank(tplTags)) {
			return false;
		}
		Map<String, String> tplTagsMap = parseJSON(tplTags, Map.class);

		boolean matched = true;
		for (Map.Entry<String, String> entry : tplTagsMap.entrySet()) {
			String value = tagsMap.get(entry.getKey());
			if (StringUtils.isBlank(value)) {
				matched = false;
				break;
			}
			if (!StringUtils.equals(value, entry.getValue())) {
				matched = false;
				break;
			}
		}
		return matched;
	}

	/**
	 * Storage and notification.
	 * 
	 * @param collectIp
	 * @param collectId
	 * @param groupId
	 * @param alarmTemplate
	 * @param gatherTime
	 * @param nowDate
	 * @param macthRule
	 */
	protected void storageAndNotification(String collectIp, Integer collectId, Integer groupId, AlarmTemplate alarmTemplate,
			long gatherTime, Date nowDate, List<AlarmRule> macthRule) {
		if (StringUtils.equals(collectIp, USE_GROUP)) {
			List<AlarmConfig> alarmConfigs = ruleConfigHandler.getAlarmConfigByGroupIdAndTemplateId(alarmTemplate.getId(),
					groupId);
			ruleConfigHandler.saveRecord(alarmTemplate, alarmConfigs, groupId, gatherTime, nowDate, macthRule);

			// Notification
			notification(alarmTemplate, alarmConfigs);
		} else {
			List<AlarmConfig> alarmConfigs = ruleConfigHandler.getAlarmConfigByCollectIdAndTemplateId(alarmTemplate.getId(),
					collectId);
			ruleConfigHandler.saveRecord(alarmTemplate, alarmConfigs, collectId, gatherTime, nowDate, macthRule);

			// Notification
			notification(alarmTemplate, alarmConfigs);
		}
	}

}