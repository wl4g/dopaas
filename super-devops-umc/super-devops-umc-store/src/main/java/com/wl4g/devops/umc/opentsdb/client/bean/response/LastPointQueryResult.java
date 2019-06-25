package com.wl4g.devops.umc.opentsdb.client.bean.response;

import java.util.Map;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午3:07
 * @Version: 1.0
 */
public class LastPointQueryResult {

	private String metric;

	private long timestamp;

	private Object value;

	private String tsuid;

	private Map<String, String> tags;

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getTsuid() {
		return tsuid;
	}

	public void setTsuid(String tsuid) {
		this.tsuid = tsuid;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
}
