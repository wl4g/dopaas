package com.wl4g.devops.common.bean.umc;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wl4g.devops.common.bean.BaseBean;

public class AlarmTemplate extends BaseBean {

	private String name;

	private String metric;

	private Integer classify;

	private String tags;

	private List<AlarmRule> rules = new ArrayList<>();

	//
	// Temporary
	//

	private Map<String, String> tagMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric == null ? null : metric.trim();
	}

	public Integer getClassify() {
		return classify;
	}

	public void setClassify(Integer templateClassify) {
		this.classify = templateClassify;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags == null ? null : tags.trim();
	}

	public List<AlarmRule> getRules() {
		return rules;
	}

	public void setRules(List<AlarmRule> rules) {
		this.rules = rules;
	}

	@SuppressWarnings("unchecked")
	public synchronized Map<String, String> getTagsMap() {
		if (isEmpty(tagMap)) {
			tagMap = parseJSON(getTags(), Map.class);
		}
		return tagMap;
	}
}