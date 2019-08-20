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
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.notification.AlarmNotifier;
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
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
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
		List<AlarmConfig> alarmConfigs = ruleManager.loadAlarmRuleTpls(agwrap.getCollectAddr());
		if (isEmpty(alarmConfigs)) {
			if (log.isInfoEnabled()) {
				log.info("No found alarm templates for collect: {}", agwrap.getCollectAddr());
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
		Map<String, String> matchedTag = matchTag(mwrap.getTags(), alarmConfig.getAlarmTemplate().getTagsMap());
		if (isEmpty(matchedTag)) {
			log.debug("No match tag to metric: {} and alarm template: {}, metric tags: {}", mwrap.getMetric(), alarmConfig.getAlarmTemplate().getId(),
					mwrap.getTags());
			return Optional.empty();
		}

		// Maximum metric keep time window of rules.
		long maxWindowTime = extractMaxRuleWindowTime(alarmConfig.getAlarmTemplate().getRules());
		// Offer latest metrics in time window queue.
		List<MetricValue> metricVals = offerTimeWindowQueue(agwrap.getCollectAddr(), mwrap.getValue(), agwrap.getTimestamp(), now,
				maxWindowTime);

		// Match alarm rules of metric values.
		List<AlarmRule> matchedRules = matchAlarmRules(metricVals, alarmConfig.getAlarmTemplate().getRules(), now);
		if (isEmpty(matchedRules)) {
			log.debug("No match rule to metric: {} and alarm template: {}, timeWindowQueue: {}", mwrap.getMetric(), alarmConfig.getAlarmTemplate().getId(),
					toJSONString(metricVals));
			return Optional.empty();
		}

		if (log.isInfoEnabled()) {
			log.info("Matched to metric: {} and alarm template: {}, timeWindowQueue: {}", mwrap.getMetric(), alarmConfig.getAlarmTemplate().getId(),
					toJSONString(metricVals));
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

		Map<Integer,TemplateContactWrapper> templateContactWrapperMap = new HashMap();
		for (AlarmResult result : results) {
			// Check null
			AlarmConfig alarmConfig = result.getAlarmConfig();
			if(alarmConfig==null){
				continue;
			}
			AlarmTemplate alarmTemplate = alarmConfig.getAlarmTemplate();
			if(alarmTemplate==null){
				continue;
			}
			List<AlarmContact> alarmContacts = alarmConfig.getAlarmContacts();
			if (alarmContacts == null) {
				continue;
			}

			// meger template and contact
			TemplateContactWrapper templateContactWrapper = templateContactWrapperMap.get(alarmConfig.getTemplateId());
			if(null== templateContactWrapper){
				templateContactWrapper = new TemplateContactWrapper(alarmConfig.getTemplateId(),alarmTemplate,
						new HashSet<>(alarmContacts),result.getMatchedTag(),result.getMatchedRules(),result.getAggregateWrap());
			}else{
				Set<AlarmContact> contacts = templateContactWrapper.getContacts();
				contacts.addAll(alarmContacts);
				templateContactWrapper.setContacts(contacts);
			}
			templateContactWrapperMap.put(alarmConfig.getTemplateId(),templateContactWrapper);
		}

		//TODO
		for (TemplateContactWrapper templateContactWrapper : templateContactWrapperMap.values()) {
			//TODO save notification
			AlarmNotification alarmNotification = new AlarmNotification();
			alarmNotification.setAlarmTime(new Date(templateContactWrapper.getAggregateWrap().getTimestamp()));
			//build alarm note
			AlarmNote alarmNote = new AlarmNote();
			alarmNote.setCollectorAddr(templateContactWrapper.getAggregateWrap().getCollectAddr());
			alarmNote.setMatchedRules(templateContactWrapper.getMatchedRules());
			alarmNote.setMatchedTag(templateContactWrapper.getMatchedTag());
			alarmNote.setMetricName(templateContactWrapper.getAlarmTemplate().getMetric());
			alarmNotification.setAlarmNote(JacksonUtils.toJSONString(alarmNote));
			configurer.saveNotification(alarmNotification);

			//TODO save record and record rule
			configurer.saveAlarmRecord(templateContactWrapper.getTemplateId(), templateContactWrapper.getAggregateWrap().getCollectAddr(),
					templateContactWrapper.getAggregateWrap().getTimestamp(), templateContactWrapper.getMatchedRules(), alarmNotification.getId());

			//TODO send
			notification(new ArrayList<>(templateContactWrapper.getContacts()), alarmNotification);


		}
	}

	/**
	 * Check notification frequently limit.
	 * 
	 * @param result
	 * @return
	 */
	protected boolean checkNotifyLimit(String key,int numOfFreq) {
		String s = jedisService.get(key);
		if(StringUtils.isNotBlank(s)&&Integer.valueOf(s)>numOfFreq){
			return false;
		}
		return true;
	}

	protected void setNotifyLimit(String key,int timeOfFreq) {
		String s = jedisService.get(key);
		int num = 0;
		if(StringUtils.isNotBlank(s)){
			num = Integer.valueOf(s);
			num++;
		}
		jedisService.set(key,""+num,timeOfFreq);
	}

	/**
	 * Notification of alarm template to users.
	 * 
	 * @param alarmTpl
	 * @param alarmConfigs
	 * @param macthedRules
	 */
	protected void notification(List<AlarmContact> alarmContacts,AlarmNotification alarmNotification) {

		for(AlarmContact alarmContact : alarmContacts){

			//email
			if(alarmContact.getEmailEnable()==1){
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmNotification.getAlarmNote(), AlarmType.EMAIL.getValue(),alarmContact.getEmail()));
			}

			//phone
			if(alarmContact.getPhoneEnable()==1&&checkNotifyLimit(ALARM_LIMIT_PHONE+alarmContact.getId(),alarmContact.getPhoneNumOfFreq())){
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmNotification.getAlarmNote(), AlarmType.SMS.getValue(),alarmContact.getPhone()));
				setNotifyLimit(ALARM_LIMIT_PHONE+alarmContact.getPhone(),alarmContact.getPhoneTimeOfFreq());
			}

			//dingtalk
			if(alarmContact.getDingtalkEnable()==1&&checkNotifyLimit(ALARM_LIMIT_DINGTALK+alarmContact.getId(),alarmContact.getDingtalkNumOfFreq())){
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmNotification.getAlarmNote(), AlarmType.DINGTALK.getValue(),alarmContact.getDingtalk()));
				setNotifyLimit(ALARM_LIMIT_DINGTALK+alarmContact.getId(),alarmContact.getDingtalkTimeOfFreq());
			}

			//facebook
			if(alarmContact.getFacebookEnable()==1&&checkNotifyLimit(ALARM_LIMIT_FACEBOOK+alarmContact.getId(),alarmContact.getFacebookNumOfFreq())){
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmNotification.getAlarmNote(), AlarmType.FACEBOOK.getValue(),alarmContact.getFacebook()));
				setNotifyLimit(ALARM_LIMIT_FACEBOOK+alarmContact.getId(),alarmContact.getFacebookTimeOfFreq());
			}

			//twitter
			if(alarmContact.getTwitterEnable()==1&&checkNotifyLimit(ALARM_LIMIT_TWITTER+alarmContact.getId(),alarmContact.getTwitterNumOfFreq())){
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmNotification.getAlarmNote(), AlarmType.TWITTER.getValue(),alarmContact.getTwitter()));
				setNotifyLimit(ALARM_LIMIT_TWITTER+alarmContact.getId(),alarmContact.getTwitterTimeOfFreq());
			}

			//wechat
			if(alarmContact.getWechatEnable()==1&&checkNotifyLimit(ALARM_LIMIT_WECHAT+alarmContact.getId(),alarmContact.getWechatNumOfFreq())){
				notifier.simpleNotify(new AlarmNotifier.SimpleAlarmMessage(alarmNotification.getAlarmNote(), AlarmType.WECHAT.getValue(),alarmContact.getWechat()));
				setNotifyLimit(ALARM_LIMIT_WECHAT+alarmContact.getId(),alarmContact.getWechatTimeOfFreq());
			}

		}


	}


	public class TemplateContactWrapper{
		private int templateId;
		private AlarmTemplate alarmTemplate;
		private Set<AlarmContact> contacts;
		private Map<String, String> matchedTag;
		private List<AlarmRule> matchedRules;
		private MetricAggregateWrapper aggregateWrap;

		public TemplateContactWrapper(int templateId, AlarmTemplate alarmTemplate, Set<AlarmContact> contacts, Map<String, String> matchedTag,
									  List<AlarmRule> matchedRules, MetricAggregateWrapper aggregateWrap) {
			this.templateId = templateId;
			this.alarmTemplate = alarmTemplate;
			this.contacts = contacts;
			this.matchedTag = matchedTag;
			this.matchedRules = matchedRules;
			this.aggregateWrap = aggregateWrap;
		}

		public int getTemplateId() {
			return templateId;
		}

		public void setTemplateId(int templateId) {
			this.templateId = templateId;
		}

		public AlarmTemplate getAlarmTemplate() {
			return alarmTemplate;
		}

		public void setAlarmTemplate(AlarmTemplate alarmTemplate) {
			this.alarmTemplate = alarmTemplate;
		}

		public Set<AlarmContact> getContacts() {
			return contacts;
		}

		public void setContacts(Set<AlarmContact> contacts) {
			this.contacts = contacts;
		}

		public Map<String, String> getMatchedTag() {
			return matchedTag;
		}

		public void setMatchedTag(Map<String, String> matchedTag) {
			this.matchedTag = matchedTag;
		}

		public List<AlarmRule> getMatchedRules() {
			return matchedRules;
		}

		public void setMatchedRules(List<AlarmRule> matchedRules) {
			this.matchedRules = matchedRules;
		}

		public MetricAggregateWrapper getAggregateWrap() {
			return aggregateWrap;
		}

		public void setAggregateWrap(MetricAggregateWrapper aggregateWrap) {
			this.aggregateWrap = aggregateWrap;
		}
	}

}