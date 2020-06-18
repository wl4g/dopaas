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
package com.wl4g.devops.common.bean.umc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.bean.BaseBean;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static org.springframework.util.CollectionUtils.isEmpty;

public class AlarmTemplate extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private String name;

	private Integer metricId;

	private String metric;

	private String classify;

	private String tags;

	private Integer notifyLevel;

	private List<AlarmRule> rules = new ArrayList<>();

	private List<Map<String, String>> tagMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric == null ? null : metric.trim();
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify == null ? null : classify.trim();
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags == null ? null : tags.trim();
	}

	public Integer getNotifyLevel() {
		return notifyLevel;
	}

	public void setNotifyLevel(Integer notifyLevel) {
		this.notifyLevel = notifyLevel;
	}

	public List<AlarmRule> getRules() {
		return rules;
	}

	public void setRules(List<AlarmRule> rules) {
		this.rules = rules;
	}

	public List<Map<String, String>> getTagMap() {
		return tagMap;
	}

	public void setTagMap(List<Map<String, String>> tagMap) {
		if (isEmpty(tagMap) && StringUtils.isNotBlank(getTags())) {
			tagMap = parseJSON(getTags(), new TypeReference<List<Map<String, String>>>() {
			});
		}
		this.tagMap = tagMap;
	}

	public Integer getMetricId() {
		return metricId;
	}

	public void setMetricId(Integer metricId) {
		this.metricId = metricId;
	}

	@SuppressWarnings("unchecked")
	public synchronized Map<String, String> getTagsMap() {
		if (isEmpty(tagMap) && StringUtils.isNotBlank(getTags())) {
			tagMap = parseJSON(getTags(), List.class);
		}

		Map<String, String> map = new HashMap<String, String>();
		if (isEmpty(tagMap)) {
			return map;
		}
		for (Map<String, String> m : tagMap) {
			if (m.get("name") != null && m.get("value") != null && StringUtils.isNotBlank(m.get("name"))
					&& StringUtils.isNotBlank(m.get("value"))) {
				map.put(m.get("name"), m.get("value"));
			}
		}
		return map;
	}
}