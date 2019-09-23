package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AlarmRule extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer templateId;

	private String aggregator;

	private Integer relateOperator;

	private Integer logicalOperator;

	private Long queueTimeWindow;

	private Double value;

	private Integer alarmLevel;

	private Double compareValue;

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

	public void setRelateOperator(Integer relateOperator) {
		this.relateOperator = relateOperator;
	}

	public Integer getLogicalOperator() {
		return logicalOperator;
	}

	public void setLogicalOperator(Integer logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public Long getQueueTimeWindow() {
		return queueTimeWindow;
	}

	public void setQueueTimeWindow(Long queueTimeWindow) {
		this.queueTimeWindow = queueTimeWindow;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(Integer alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public Double getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(Double compareValue) {
		this.compareValue = compareValue;
	}
}