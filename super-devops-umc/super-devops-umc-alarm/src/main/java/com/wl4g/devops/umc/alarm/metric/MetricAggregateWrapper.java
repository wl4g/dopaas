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
package com.wl4g.devops.umc.alarm.metric;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.CollectionUtils.isEmpty;

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
	 * Standard timing data collector address ID, such as REDIS service metrics
	 * collector: defaultRedisCollector, or IOT device collector (DTU custom
	 * address) e.g.: 11511888
	 */
	private String host = EMPTY;

	/**
	 * Collect endpoint. (metric data from source)
	 */
	private String endpoint = EMPTY;

	/** Collect metric type. */
	private String classify = EMPTY;

	/** Collect metric list. */
	private List<MetricWrapper> metrics = new ArrayList<>();

	/** Collect metric time-stamp. */
	private Long timestamp = -1L;

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

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		Assert.hasText(classify, "Collect classify must not be empty.");
		this.classify = classify;
	}

	public List<MetricWrapper> getMetrics() {
		return metrics;
	}

	public void setMetrics(List<MetricWrapper> metrics) {
		if (!isEmpty(metrics)) {
			this.metrics.addAll(metrics);
		}
	}

	public Long getTimestamp() {
		return timestamp * 1000;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "MetricAggregateWrapper{" + "host='" + host + '\'' + ", endpoint='" + endpoint + '\'' + ", classify='" + classify
				+ '\'' + ", metrics=" + metrics + ", timestamp=" + timestamp + '}';
	}

	/**
	 * Metric wrapper.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年7月5日
	 * @since
	 */
	public static class MetricWrapper {

		private String metric = EMPTY;

		/**
		 * Standard time series data TAG, such as: collect of server REDIS
		 * metric:
		 * 
		 * <pre>
		 * tag.put("server", "192.168.1.2:6379")
		 * </pre>
		 * 
		 * or collect of IOT device metric:
		 * 
		 * <pre>
		 * tag.put("addrIPOrder", "01")
		 * </pre>
		 */
		private Map<String, String> tags = new HashMap<>();

		/**
		 * Value metric by standard time series data, such as MySQL current
		 * connections: 1000, or current electric: 500 KW/H
		 */
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