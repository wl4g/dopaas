/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.umc.opentsdb.client.bean.response;

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