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
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 构建数据点对象
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午1:52
 * @Version: 1.0
 */
public class Point {

	private String metric;

	private Map<String, String> tags = new HashMap<>();

	private Number value;

	private long timestamp;

	public static MetricBuilder metric(String metric) {
		return new MetricBuilder(metric);
	}

	public static class MetricBuilder {

		private String metric;

		private Map<String, String> tags = new HashMap<>();

		private Number value;

		private long timestamp;

		public MetricBuilder(String metric) {
			if (StringUtils.isBlank(metric)) {
				throw new IllegalArgumentException("The metric can't be empty");
			}
			this.metric = metric;
		}

		public MetricBuilder value(long timestamp, Number value) {
			if (timestamp == 0) {
				throw new IllegalArgumentException("timestamp must gt 0");
			}
			Objects.requireNonNull(value);
			this.timestamp = timestamp;
			this.value = value;
			return this;
		}

		public MetricBuilder tag(final String tagName, final String value) {
			if (StringUtils.isNoneBlank(tagName) && StringUtils.isNoneBlank(value)) {
				tags.put(tagName, value);
			}
			return this;
		}

		public MetricBuilder tag(final Map<String, String> tags) {
			if (!MapUtils.isEmpty(tags)) {
				this.tags.putAll(tags);
			}
			return this;
		}

		public Point build() {
			Point point = new Point();
			point.metric = this.metric;

			if (MapUtils.isEmpty(tags)) {
				throw new IllegalArgumentException("tags can't be empty");
			}
			point.tags = this.tags;

			point.timestamp = this.timestamp;
			point.value = this.value;
			return point;
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

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}