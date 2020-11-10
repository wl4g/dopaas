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

import java.util.List;
import java.util.Map;

import com.wl4g.devops.common.bean.umc.AlarmRule;

/**
 * @author vjay
 * @date 2019-08-20 15:19:00
 */
public class AlarmNote {

	private String host;

	private String endpoint;

	private String metricName;

	private Map<String, String> matchedTag;

	private List<AlarmRule> matchedRules;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
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
}