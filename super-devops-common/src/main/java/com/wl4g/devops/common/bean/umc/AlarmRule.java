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