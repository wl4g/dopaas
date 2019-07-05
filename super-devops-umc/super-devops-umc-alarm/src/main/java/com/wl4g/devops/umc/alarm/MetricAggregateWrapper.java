package com.wl4g.devops.umc.alarm;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * Collection metric type .
	 */
	private String classify = EMPTY;

	/**
	 * Collection metric target tags.
	 */
	private Map<String, String> tags = new HashMap<>();

	/**
	 * Collection metric value.
	 */
	private Double value;

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

	@Override
	public String toString() {
		return "MetricAggregateWrapper [collectId=" + collectId + ", metricType=" + classify + ", tags=" + tags + ", value="
				+ value + "]";
	}

}
