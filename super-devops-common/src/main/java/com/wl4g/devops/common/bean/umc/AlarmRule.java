package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

public class AlarmRule extends BaseBean {

	private Integer templateId;

	private String aggregator;

	private Integer relateOperator;

	private Double value;

	private Integer logicalOperator;

	private Long queueTimeWindow;

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

	public Integer getRelateOperator() {
		return relateOperator;
	}

	public void setRelateOperator(Integer operator) {
		this.relateOperator = operator;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(Integer triggerType) {
		this.logicalOperator = triggerType;
	}

	public Long getQueueTimeWindow() {
		return queueTimeWindow;
	}

	public void setQueueTimeWindow(Long continuityTime) {
		this.queueTimeWindow = continuityTime;
	}
}