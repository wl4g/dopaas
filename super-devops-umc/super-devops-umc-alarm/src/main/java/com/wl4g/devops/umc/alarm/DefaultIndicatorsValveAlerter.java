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

import static com.wl4g.devops.common.utils.lang.Collections2.ensureList;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static java.lang.Math.abs;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

	/**
	 * REDIS lock manager.
	 */
	@Autowired
	protected SimpleRedisLockManager lockManager;

	public DefaultIndicatorsValveAlerter(AlarmProperties config, JedisService jedisService, AlarmConfigurer configurer,
			RuleConfigManager ruleManager, CompositeRuleInspectorAdapter inspector, CompositeAlarmNotifierAdapter notifier) {
		super(config, jedisService, configurer, ruleManager, inspector, notifier);
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

		// Handling alarm.
		long now = System.currentTimeMillis();
		for (MetricWrapper metricWrap : agwrap.getMetrics()) {
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
					List<MetricValue> metricVals = offerTimeWindowQueue(agwrap.getCollectAddr(), metricWrap.getValue(),
							agwrap.getTimestamp(), now, maxWindowTime);

					// Match alarm rules of metric values.
					List<AlarmRule> matchedRules = matchAlarmRules(metricVals, tpl.getRules(), now);
					if (!isEmpty(matchedRules)) {
						if (log.isInfoEnabled()) {
							log.info("Matched to metric: {} and alarm template: {}, time window queue: {}", metricName,
									tpl.getId(), toJSONString(metricVals));
						}

						// Storage & notification
						storageNotification(agwrap.getCollectAddr(), tpl, agwrap.getTimestamp(), matchedRules);
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
	 * 
	 * @param metricVals
	 * @param rules
	 * @param now
	 * @return
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
	 * 
	 * @param collectAddr
	 *            collector address
	 * @param value
	 *            metric value
	 * @param gatherTime
	 *            gather time-stamp.
	 * @param now
	 *            current date time-stamp.
	 * @param ttl
	 *            time-to-live
	 * @return
	 */
	protected List<MetricValue> offerTimeWindowQueue(String collectAddr, Double value, long gatherTime, long now, long ttl) {
		String timeWindowKey = getTimeWindowQueueCacheKey(collectAddr);
		// To solve the concurrency problem of metric window queue in
		// distributed environment.
		Lock lock = lockManager.getLock(timeWindowKey);

		List<MetricValue> metricVals = emptyList();
		try {
			if (lock.tryLock(5L, TimeUnit.SECONDS)) {
				metricVals = ensureList(doPeekMetricValueQueue(collectAddr));
				metricVals.add(new MetricValue(gatherTime, value));

				// Clean expired metrics.
				Iterator<MetricValue> it = metricVals.iterator();
				while (it.hasNext()) {
					if (abs(now - it.next().getGatherTime()) >= ttl) {
						it.remove();
					}
				}

				// Storage to queue.
				doOfferMetricValueQueue(collectAddr, ttl, metricVals);
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			lock.unlock();
		}

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
	 * @param collectAddr
	 * @param collectAddr
	 * @param alarmTpl
	 * @param gatherTime
	 * @param macthedRules
	 */
	protected void storageNotification(String collectAddr, AlarmTemplate alarmTpl, long gatherTime,
			List<AlarmRule> macthedRules) {
		List<AlarmConfig> alarmConfigs = configurer.findAlarmConfig(alarmTpl.getId(), collectAddr);
		// Storage record.
		configurer.saveAlarmRecord(alarmTpl, alarmConfigs, collectAddr, gatherTime, macthedRules);
		// Notification
		notification(alarmTpl, alarmConfigs, macthedRules);
	}

}