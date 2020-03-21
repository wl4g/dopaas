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
import com.wl4g.devops.umc.alarm.metric.MetricAggregateWrapper;

import java.util.List;
import java.util.Map;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Alarm matched result.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月14日
 * @since
 */
public class AlarmMessage {

	/**
	 * {@link MetricAggregateWrapper}
	 */
	final private MetricAggregateWrapper aggregateWrap;

	/**
	 * Alarm config
	 */
	final private AlarmConfig alarmConfig;

	/**
	 * Alarm matched tags.
	 */
	final private Map<String, String> matchedTag;

	/**
	 * Alarm matched rules.
	 */
	final private List<AlarmRule> matchedRules;

	public AlarmMessage(MetricAggregateWrapper aggregate, AlarmConfig alarmConfig, Map<String, String> matchedTag,
			List<AlarmRule> matchedRules) {
		notNullOf(aggregate, "aggregate");
		notNullOf(alarmConfig, "alarmConfig");
		this.aggregateWrap = aggregate;
		this.alarmConfig = alarmConfig;
		this.matchedTag = matchedTag;
		this.matchedRules = matchedRules;
	}

	public MetricAggregateWrapper getAggregate() {
		return aggregateWrap;
	}

	public Map<String, String> getMatchedTag() {
		return isEmpty(matchedTag) ? emptyMap() : matchedTag;
	}

	public List<AlarmRule> getMatchedRules() {
		return isEmpty(matchedRules) ? emptyList() : matchedRules;
	}

	public AlarmConfig getAlarmConfig() {
		return alarmConfig;
	}

	@Override
	public String toString() {
		return "AlarmResult [aggregateWrap=" + aggregateWrap + ", alarmConfig=" + alarmConfig + ", matchedTag=" + matchedTag
				+ ", matchedRules=" + matchedRules + "]";
	}

}