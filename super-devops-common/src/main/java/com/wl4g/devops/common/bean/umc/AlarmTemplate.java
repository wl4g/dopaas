package com.wl4g.devops.common.bean.umc;

import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.common.bean.BaseBean;

public class AlarmTemplate extends BaseBean {

	private String metric;

	private Integer templateClassify;

	private String tags;

	private List<AlarmRule> rules = new ArrayList<>();

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric == null ? null : metric.trim();
	}

	public Integer getTemplateClassify() {
		return templateClassify;
	}

	public void setTemplateClassify(Integer templateClassify) {
		this.templateClassify = templateClassify;
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
}