package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AlarmRecordRule extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer recordId;

	private Integer ruleId;

	private Double compareValue;

	public Integer getRecordId() {
		return recordId;
	}

	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public Double getCompareValue() {
		return compareValue;
	}

	public void setCompareValue(Double compareValue) {
		this.compareValue = compareValue;
	}
}