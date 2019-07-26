package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

public class AlarmRule extends BaseBean {

	private Integer templateId;

	private String aggregator;

	private Integer operator;

	private Double value;

	private Integer triggerType;

	private Long continuityTime;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public String getAggregator() {
		return aggregator;
	}

	public void setAggregator(String aggregator) {
		this.aggregator = aggregator == null ? null : aggregator.trim();
	}

	public Integer getOperator() {
		return operator;
	}

	public void setOperator(Integer operator) {
		this.operator = operator;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	public Long getContinuityTime() {
		return continuityTime;
	}

	public void setContinuityTime(Long continuityTime) {
		this.continuityTime = continuityTime;
	}
}