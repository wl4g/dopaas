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

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;

/**
 * Alarm matched result.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月14日
 * @since
 */
public class AlarmResult {

	/**
	 * MetricAggregateWrapper
	 */
	final private MetricAggregateWrapper aggregateWrap;

	/**
	 * AlarmTemplate
	 */
	final private AlarmTemplate alarmTemplate;

	/**
	 * Alarm matched tags.
	 */
	final private Map<String, String> matchedTag;

	/**
	 * Alarm matched rules.
	 */
	final private List<AlarmRule> matchedRules;

	public AlarmResult(MetricAggregateWrapper aggregateWrap, AlarmTemplate alarmTemplate, Map<String, String> matchedTag,
			List<AlarmRule> matchedRules) {
		Assert.notNull(aggregateWrap, "Alarm result aggregateWrap must not be null");
		Assert.notNull(alarmTemplate, "Alarm result alarmTemplate must not be null");
		this.aggregateWrap = aggregateWrap;
		this.alarmTemplate = alarmTemplate;
		this.matchedTag = matchedTag;
		this.matchedRules = matchedRules;
	}

	public MetricAggregateWrapper getAggregateWrap() {
		return aggregateWrap;
	}

	public AlarmTemplate getAlarmTemplate() {
		return alarmTemplate;
	}

	public Map<String, String> getMatchedTag() {
		return isEmpty(matchedTag) ? emptyMap() : matchedTag;
	}

	public List<AlarmRule> getMatchedRules() {
		return isEmpty(matchedRules) ? emptyList() : matchedRules;
	}

	@Override
	public String toString() {
		return "AlarmResult [aggregateWrap=" + aggregateWrap + ", alarmTemplate=" + alarmTemplate + ", matchedTag=" + matchedTag
				+ ", matchedRules=" + matchedRules + "]";
	}

}
