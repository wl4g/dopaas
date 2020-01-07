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

import com.wl4g.devops.common.bean.umc.*;
import com.wl4g.devops.common.bean.umc.model.MetricValue;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.notification.AlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmNotifier.SimpleAlarmMessage;
import com.wl4g.devops.umc.notification.AlarmType;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;
import com.wl4g.devops.umc.rule.inspect.RuleInspector.InspectWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.Map.Entry;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;
import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.lang.Math.abs;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

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

	public DefaultIndicatorsValveAlerter(JedisService jedisService, JedisLockManager lockManager, AlarmProperties config,
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
			log.info("Alarm handling for host: {} endpoint:{}", agwrap.getHost(), agwrap.getEndpoint());
		}

		// Load alarm templates by collectId.
		List<AlarmConfig> alarmConfigs = ruleManager.loadAlarmRuleTpls(agwrap.getHost(), agwrap.getEndpoint());
		if (isEmpty(alarmConfigs)) {
			if (log.isInfoEnabled()) {
				log.info("No found alarm templates for host: {} endpoint:{}", agwrap.getHost(), agwrap.getEndpoint());
			}
			return;
		}

		// Alarm match handling.
		List<AlarmResult> results = new ArrayList<>(agwrap.getMetrics().size() * 2);
		final long now = System.currentTimeMillis();
		for (MetricWrapper mwrap : agwrap.getMetrics()) {
			for (AlarmConfig tpl : alarmConfigs) {
				if (StringUtils.equals(mwrap.getMetric(), tpl.getAlarmTemplate().getMetric())) {
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
			AlarmConfig alarmConfig, long now) {
		// Match tags
		Map<String, String> matchedTag = emptyMap();
		if (!isEmpty(alarmConfig.getAlarmTemplate().getTagsMap())) {
			matchedTag = matchTag(mwrap.getTags(), alarmConfig.getAlarmTemplate().getTagsMap());
			if (isEmpty(matchedTag)) {
				log.debug("No match tag to metric: {} and alarm template: {}, metric tags: {}", mwrap.getMetric(),
						alarmConfig.getAlarmTemplate().getId(), mwrap.getTags());
				return Optional.empty();
			}
		}

		// Maximum metric keep time window of rules.
		long maxWindowTime = extractMaxRuleWindowTime(alarmConfig.getAlarmTemplate().getRules());
		// Offer latest metrics in time window queue.
		List<MetricValue> metricVals = offerTimeWindowQueue(
				agwrap.getHost() + ":" + agwrap.getEndpoint() + "@" + alarmConfig.getAlarmTemplate().getId(), mwrap.getValue(),
				agwrap.getTimestamp(), now, maxWindowTime);

		// Match alarm rules of metric values.
		List<AlarmRule> matchedRules = matchAlarmRules(metricVals, alarmConfig.getAlarmTemplate().getRules(), now);
		if (isEmpty(matchedRules)) {
			log.debug("No match rule to metric: {} and alarm template: {}, timeWindowQueue: {}", mwrap.getMetric(),
					alarmConfig.getAlarmTemplate().getId(), toJSONString(metricVals));
			return Optional.empty();
		}

		if (log.isInfoEnabled()) {
			log.info("Matched to metric: {} and alarm template: {}, timeWindowQueue: {}", mwrap.getMetric(),
					alarmConfig.getAlarmTemplate().getId(), toJSONString(metricVals));
		}
		return Optional.of(new AlarmResult(agwrap, alarmConfig, matchedTag, matchedRules));
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
			return metricTagMap;
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
		return safeList(rules).stream().filter(rule -> {
			// Get latest time window metric values.
			Double[] vals = extractValidityMetricValueInQueue(metricVals, rule.getQueueTimeWindow(), now);
			// Do inspection.
			InspectWrapper wrap = new InspectWrapper(rule.getLogicalOperator(), rule.getRelateOperator(), rule.getAggregator(),
					rule.getValue(), vals);
			if (inspector.verify(wrap)) {
				rule.setCompareValue(wrap.getCompareValue());
				return true;
			}
			return false;
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
		Map<Integer, TemplateContactWrapper> contactMap = new HashMap<>();
		for (AlarmResult result : results) {
			// Check
			AlarmConfig config = result.getAlarmConfig();
			if (config == null) {
				continue;
			}
			AlarmTemplate tpl = config.getAlarmTemplate();
			if (tpl == null || isEmpty(config.getAlarmContacts())) {
				continue;
			}

			// Merge template and contact
			TemplateContactWrapper contactWrap = contactMap.get(config.getTemplateId());
			if (null == contactWrap) {
				contactWrap = new TemplateContactWrapper(config.getTemplateId(), tpl, config.getAlarmContacts(),
						result.getMatchedTag(), result.getMatchedRules(), result.getAggregateWrap());
			} else {
				List<AlarmContact> contacts = contactWrap.getContacts();
				contacts.addAll(config.getAlarmContacts());
				contactWrap.setContacts(contacts);
			}
			contactMap.put(config.getTemplateId(), contactWrap);
		}

		for (TemplateContactWrapper contactWrap : contactMap.values()) {
			contactWrap.setContacts(distinctContacts(contactWrap.getContacts())); // Repeat
			// build alarm note
			AlarmNote note = new AlarmNote();
			note.setHost(contactWrap.getAggregateWrap().getHost());
			note.setEndpoint(contactWrap.getAggregateWrap().getEndpoint());
			note.setMatchedRules(contactWrap.getMatchedRules());
			note.setMatchedTag(contactWrap.getMatchedTag());
			note.setMetricName(contactWrap.getAlarmTemplate().getMetric());

			// Save record and record rule
			AlarmRecord record = configurer.saveAlarmRecord(contactWrap.getAlarmTemplate(),
					contactWrap.getAggregateWrap().getTimestamp(), contactWrap.getMatchedRules(), toJSONString(note));

			// Send notification
			notification(new ArrayList<>(contactWrap.getContacts()), record);
		}
	}

	/**
	 * Remove duplicate notification contacts
	 * 
	 * @param contacts
	 * @return
	 */
	public static List<AlarmContact> distinctContacts(List<AlarmContact> contacts) {
		Set<AlarmContact> _contacts = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
		_contacts.addAll(contacts);
		return new ArrayList<>(_contacts);
	}

	/**
	 * Check notification frequently limit.
	 * 
	 * @param result
	 * @return
	 */
	protected boolean checkNotifyLimit(String key, int numOfFreq) {
		String s = jedisService.get(key);
		if (StringUtils.isNotBlank(s) && Integer.valueOf(s) > numOfFreq) {
			return false;
		}
		return true;
	}

	/**
	 * Handle rate limit.
	 * 
	 * @param key
	 * @param timeOfFreq
	 */
	protected void handleRateLimit(String key, int timeOfFreq) {
		jedisService.getJedisCluster().incrBy(key, 1);
		jedisService.getJedisCluster().expire(key, timeOfFreq);
	}

	/**
	 * Notification of alarm template to users.
	 * 
	 * @param alarmTpl
	 * @param alarmConfigs
	 * @param macthedRules
	 */
	protected void notification(List<AlarmContact> alarmContacts, AlarmRecord alarmRecord) {
		log.info("into DefaultIndicatorsValveAlerter.notification prarms::" + "alarmContacts = {} , alarmNote = {} ",
				alarmContacts, alarmRecord.getAlarmNote());

		// TODO just for test
		notifier.simpleNotify(new SimpleAlarmMessage(alarmRecord.getAlarmNote(), AlarmType.BARK.getValue(), ""));

		for (AlarmContact alarmContact : alarmContacts) {
			// save notification
			AlarmNotificationContact alarmNotificationContact = new AlarmNotificationContact();
			alarmNotificationContact.setRecordId(alarmRecord.getId());
			alarmNotificationContact.setContactId(alarmContact.getId());
			alarmNotificationContact.setStatus(ALARM_SATUS_SEND);
			configurer.saveNotificationContact(alarmNotificationContact);

			// email
			if (alarmContact.getEmailEnable() == 1) {
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmRecord.getAlarmNote(), AlarmType.EMAIL.getValue(),
						alarmContact.getEmail()));
			}

			// phone
			if (alarmContact.getPhoneEnable() == 1
					&& checkNotifyLimit(ALARM_LIMIT_PHONE + alarmContact.getId(), alarmContact.getPhoneNumOfFreq())) {
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmRecord.getAlarmNote(), AlarmType.SMS.getValue(),
						alarmContact.getPhone()));
				handleRateLimit(ALARM_LIMIT_PHONE + alarmContact.getPhone(), alarmContact.getPhoneTimeOfFreq());
			}

			// dingtalk
			if (alarmContact.getDingtalkEnable() == 1
					&& checkNotifyLimit(ALARM_LIMIT_DINGTALK + alarmContact.getId(), alarmContact.getDingtalkNumOfFreq())) {
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmRecord.getAlarmNote(),
						AlarmType.DINGTALK.getValue(), alarmContact.getDingtalk()));
				handleRateLimit(ALARM_LIMIT_DINGTALK + alarmContact.getId(), alarmContact.getDingtalkTimeOfFreq());
			}

			// facebook
			if (alarmContact.getFacebookEnable() == 1
					&& checkNotifyLimit(ALARM_LIMIT_FACEBOOK + alarmContact.getId(), alarmContact.getFacebookNumOfFreq())) {
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmRecord.getAlarmNote(),
						AlarmType.FACEBOOK.getValue(), alarmContact.getFacebook()));
				handleRateLimit(ALARM_LIMIT_FACEBOOK + alarmContact.getId(), alarmContact.getFacebookTimeOfFreq());
			}

			// twitter
			if (alarmContact.getTwitterEnable() == 1
					&& checkNotifyLimit(ALARM_LIMIT_TWITTER + alarmContact.getId(), alarmContact.getTwitterNumOfFreq())) {
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmRecord.getAlarmNote(),
						AlarmType.TWITTER.getValue(), alarmContact.getTwitter()));
				handleRateLimit(ALARM_LIMIT_TWITTER + alarmContact.getId(), alarmContact.getTwitterTimeOfFreq());
			}

			// wechat
			if (alarmContact.getWechatEnable() == 1
					&& checkNotifyLimit(ALARM_LIMIT_WECHAT + alarmContact.getId(), alarmContact.getWechatNumOfFreq())) {
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmRecord.getAlarmNote(),
						AlarmType.WECHAT.getValue(), alarmContact.getWechat()));
				handleRateLimit(ALARM_LIMIT_WECHAT + alarmContact.getId(), alarmContact.getWechatTimeOfFreq());
			}

		}

	}

}