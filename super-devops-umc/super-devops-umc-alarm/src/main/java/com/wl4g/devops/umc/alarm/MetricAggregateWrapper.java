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
		return timestamp;
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
