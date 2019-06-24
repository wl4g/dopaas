package com.wl4g.devops.umc.opentsdb.client.bean.response;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 上午11:51
 * @Version: 1.0
 */
public class QueryResult {

	private String metric;

	private Map<String, String> tags;

	private List<String> aggregateTags;

	private LinkedHashMap<Long, Number> dps;

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

	public List<String> getAggregateTags() {
		return aggregateTags;
	}

	public void setAggregateTags(List<String> aggregateTags) {
		this.aggregateTags = aggregateTags;
	}

	public LinkedHashMap<Long, Number> getDps() {
		return dps;
	}

	public void setDps(LinkedHashMap<Long, Number> dps) {
		this.dps = dps;
	}
}
