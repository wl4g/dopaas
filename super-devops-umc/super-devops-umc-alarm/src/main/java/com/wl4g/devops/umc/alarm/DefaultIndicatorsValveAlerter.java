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
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.MetricValue;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;
import com.wl4g.devops.umc.rule.inspect.RuleInspector.InspectWrapper;

/**
 * Default collection metric valve alerter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月24日
 * @since
 */
public class DefaultIndicatorsValveAlerter extends AbstractIndicatorsValveAlerter {

	/** Alarm configuration */
	final protected AlarmConfigurer configurer;

	/** Alarm rule manager */
	final protected RuleConfigManager ruleManager;

	/** Alarm rule inspector */
	final protected CompositeRuleInspectorAdapter inspector;

	/** Alarm notifier */
	final protected CompositeAlarmNotifierAdapter notifier;

	public DefaultIndicatorsValveAlerter(JedisService jedisService, SimpleRedisLockManager lockManager, AlarmProperties config,
			AlarmConfigurer configurer, RuleConfigManager ruleManager, CompositeRuleInspectorAdapter inspector,
			CompositeAlarmNotifierAdapter notifier) {
		super(jedisService, lockManager, config);
		Assert.notNull(configurer, "AlarmConfigurer is null, please check config.");
		Assert.notNull(ruleManager, "RuleManager is null, please check config.");
		Assert.notNull(inspector, "RuleInspector is null, please check config.");
		Assert.notNull(notifier, "AlarmNotifier is null, please check config.");
		this.configurer = configurer;
		this.ruleManager = ruleManager;
		this.inspector = inspector;
		this.notifier = notifier;
	}

	@Override
	protected void doHandleAlarm(MetricAggregateWrapper agwrap) {
		if (log.isInfoEnabled()) {
			log.info("Alarm handling for collectId: {}", agwrap.getCollectAddr());
		}

		// Load alarm templates by collectId.
		List<AlarmTemplate> alarmTpls = ruleManager.loadAlarmRuleTpls(agwrap.getCollectAddr());
		if (isEmpty(alarmTpls)) {
			if (log.isInfoEnabled()) {
				log.info("No found alarm templates for collect: {}", agwrap.getCollectAddr());
			}
			return;
		}

		// Alarm match handling.
		List<AlarmResult> results = new ArrayList<>(agwrap.getMetrics().size() * 2);
		final long now = System.currentTimeMillis();
		for (MetricWrapper mwrap : agwrap.getMetrics()) {
			for (AlarmTemplate tpl : alarmTpls) {
				if (StringUtils.equals(mwrap.getMetric(), tpl.getMetric())) {
					// Obtain matching alarm result.
					Optional<AlarmResult> ropt = doGetAlarmResultWithMatchRule(agwrap, mwrap, tpl, now);
					if (ropt.isPresent()) {
						results.add(ropt.get());
					}
				}
			}
		}

		// Record & notification
		postAlarmResultProcessed(results);
	}

	// --- Matching. ---

	/**
	 * Do obtain alarm result with match rule.
	 * 
	 * @param agwrap
	 * @param mwrap
	 * @param tpl
	 * @param now
	 * @return
	 */
	protected Optional<AlarmResult> doGetAlarmResultWithMatchRule(MetricAggregateWrapper agwrap, MetricWrapper mwrap,
			AlarmTemplate tpl, long now) {
		// Match tags
		Map<String, String> matchedTag = matchTag(mwrap.getTags(), tpl.getTagsMap());
		if (isEmpty(matchedTag)) {
			log.debug("No match tag to metric: {} and alarm template: {}, metric tags: {}", mwrap.getMetric(), tpl.getId(),
					mwrap.getTags());
			return Optional.empty();
		}

		// Maximum metric keep time window of rules.
		long maxWindowTime = extractMaxRuleWindowTime(tpl.getRules());
		// Offer latest metrics in time window queue.
		List<MetricValue> metricVals = offerTimeWindowQueue(agwrap.getCollectAddr(), mwrap.getValue(), agwrap.getTimestamp(), now,
				maxWindowTime);

		// Match alarm rules of metric values.
		List<AlarmRule> matchedRules = matchAlarmRules(metricVals, tpl.getRules(), now);
		if (isEmpty(matchedRules)) {
			log.debug("No match rule to metric: {} and alarm template: {}, timeWindowQueue: {}", mwrap.getMetric(), tpl.getId(),
					toJSONString(metricVals));
			return Optional.empty();
		}

		if (log.isInfoEnabled()) {
			log.info("Matched to metric: {} and alarm template: {}, timeWindowQueue: {}", mwrap.getMetric(), tpl.getId(),
					toJSONString(metricVals));
		}
		return Optional.of(new AlarmResult(agwrap, tpl, matchedTag, matchedRules));
	}

	/**
	 * Match metric tags
	 * 
	 * @param metricTagMap
	 * @param tplTagMap
	 * @return
	 */
	protected Map<String, String> matchTag(Map<String, String> metricTagMap, Map<String, String> tplTagMap) {
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
	 * Match alarm rules.
	 * 
	 * @param metricVals
	 * @param rules
	 * @param now
	 * @return
	 */
	protected List<AlarmRule> matchAlarmRules(List<MetricValue> metricVals, List<AlarmRule> rules, long now) {
		// Match mode for 'OR'/'AND'.
		return safeList(rules).stream().map(rule -> {
			// Extract validity metric values.
			Double[] validityMetricVals = extractValidityMetricValueInQueue(metricVals, rule.getQueueTimeWindow(), now);
			// Do inspection.
			InspectWrapper wrap = new InspectWrapper(rule.getLogicalOperator(), rule.getRelateOperator(), rule.getAggregator(),
					rule.getValue(), validityMetricVals);
			if (inspector.verify(wrap)) {
				return rule;
			}
			return null;
		}).collect(toList());
	}

	/**
	 * Metric the validity of extraction from queue.
	 * 
	 * @param metricVals
	 * @param durationMs
	 * @param now
	 * @return
	 */
	protected Double[] extractValidityMetricValueInQueue(List<MetricValue> metricVals, final long durationMs, long now) {
		return safeList(metricVals).stream().filter(v -> abs(now - v.getGatherTime()) < durationMs).map(v -> v.getValue())
				.collect(toList()).toArray(new Double[] {});
	}

	// --- Alarm result processed. ---

	/**
	 * After alarm result processed.
	 * 
	 * @param collectAddr
	 * @param alarmTpl
	 * @param gatherTime
	 * @param macthedRules
	 */
	protected void postAlarmResultProcessed(List<AlarmResult> results) {
		for (AlarmResult result : results) {
			if (checkNotifyLimit(result)) {
				// TODO
				// Save notification
			}
		}

		// List<AlarmConfig> alarmConfigs =
		// configurer.findAlarmConfig(alarmTpl.getId(), collectAddr);
		// // Storage record.
		// configurer.saveAlarmRecord(alarmTpl, alarmConfigs, collectAddr,
		// gatherTime, macthedRules);
		// // Notification
		// notification(alarmTpl, alarmConfigs, macthedRules);
	}

	/**
	 * Check notification frequently limit.
	 * 
	 * @param result
	 * @return
	 */
	protected boolean checkNotifyLimit(AlarmResult result) {
		// TODO
		return false;
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

}