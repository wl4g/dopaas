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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Metric aggregate wrapper.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class MetricAggregateWrapper implements Serializable {
	private static final long serialVersionUID = 6450172459195801884L;

	/**
	 * Metric collection target ID.
	 */
	private String collectId = EMPTY;

	/** Collect metric type. */
	private String classify = EMPTY;

	/** Collect metric list. */
	private List<MetricWrapper> metrics;

	/** Collect metric time-stamp. */
	private Long timestamp;

	public String getCollectId() {
		return collectId;
	}

	public void setCollectId(String collectId) {
		this.collectId = collectId;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String metricType) {
		this.classify = metricType;
	}

	public List<MetricWrapper> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<MetricWrapper> metrics) {
		this.metrics = metrics;
	}

	public Long getTimestamp() {
		return timestamp * 1000;
	}

	public void setTimestamp(Long timeStamp) {
		this.timestamp = timeStamp;
	}

	@Override
	public String toString() {
		return "MetricAggregateWrapper{" + "collectId='" + collectId + '\'' + ", classify='" + classify + '\'' + ", metrics="
				+ metrics + ", timeStamp=" + timestamp + '}';
	}

	/**
	 * Metric wrapper.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年7月5日
	 * @since
	 */
	public static class MetricWrapper {

		private String metric;

		private Map<String, String> tags = new HashMap<>();

		private Double value;

		public String getMetric() {
			return metric;
		}

		public void setMetric(String metric) {
			this.metric = metric;
		}

		public Map<String, String> getTags() {
			return tags;
		}

		public void setTags(Map<String, String> tags) {
			this.tags = tags;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}
	}

}