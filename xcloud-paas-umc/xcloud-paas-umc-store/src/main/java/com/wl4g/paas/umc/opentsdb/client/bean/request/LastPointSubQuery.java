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
package com.wl4g.paas.umc.opentsdb.client.bean.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午2:48
 * @Version: 1.0
 */
public class LastPointSubQuery {

	private String metric;

	private Map<String, String> tags;

	public static class Builder {

		private String metric;

		private Map<String, String> tags = new HashMap<>();

		public LastPointSubQuery build() {
			LastPointSubQuery query = new LastPointSubQuery();
			query.metric = this.metric;
			query.tags = this.tags;
			return query;
		}

		public Builder(String metric) {
			this.metric = metric;
		}

		public Builder tag(String tagk, String tagv) {
			if (StringUtils.isNoneBlank(tagk) && StringUtils.isNoneBlank(tagv)) {
				this.tags.put(tagk, tagv);
			}
			return this;
		}

		public Builder tag(Map<String, String> tags) {
			if (!MapUtils.isEmpty(tags)) {
				this.tags.putAll(tags);
			}
			return this;
		}

	}

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

	public static Builder metric(String metric) {
		return new Builder(metric);
	}

}