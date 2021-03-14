/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.umc.alarm;

import java.util.List;
import java.util.Map;

import com.wl4g.paas.common.bean.umc.AlarmRule;
import com.wl4g.paas.common.bean.umc.AlarmTemplate;
import com.wl4g.paas.umc.alarm.metric.MetricAggregateWrapper;
import com.wl4g.iam.common.bean.Contact;

/**
 * @author vjay
 * @date 2019-08-22 15:16:00
 */
public class TemplateContactWrapper {
	private long templateId;
	private AlarmTemplate alarmTemplate;
	private List<Contact> contacts;
	private Map<String, String> matchedTag;
	private List<AlarmRule> matchedRules;
	private MetricAggregateWrapper aggregateWrap;

	public TemplateContactWrapper(long templateId, AlarmTemplate alarmTemplate, List<Contact> contacts,
			Map<String, String> matchedTag, List<AlarmRule> matchedRules, MetricAggregateWrapper aggregateWrap) {
		this.templateId = templateId;
		this.alarmTemplate = alarmTemplate;
		this.contacts = contacts;
		this.matchedTag = matchedTag;
		this.matchedRules = matchedRules;
		this.aggregateWrap = aggregateWrap;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public AlarmTemplate getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(AlarmTemplate alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
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