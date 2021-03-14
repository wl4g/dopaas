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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Maps;

/**
 * 子查询，详见<a>http://opentsdb.net/docs/build/html/api_http/query/index.html</a>
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午10:37
 * @Version: 1.0
 */
@SuppressWarnings("unused")
public class SubQuery {

	private Aggregator aggregator;

	private String metric;

	private String downsample;

	private Boolean rate;

	private RateOptions rateOptions;

	private Map<String, String> tags;

	private Boolean explicitTags;

	private String rollupUsage;

	private List<Filter> filters;

	public static MetricBuilder metric(String metric) {
		return new MetricBuilder(metric);
	}

	public static AggregatorBuilder aggregator(Aggregator aggregator) {
		return new AggregatorBuilder(aggregator);
	}

	/***
	 * subQuery builder
	 */
	public static class Builder {

		private Aggregator aggregator;

		private String metric;

		private String downsample;

		private Boolean rate;

		private RateOptions rateOptions;

		private Map<String, String> tags = new HashMap<>();

		private Boolean explicitTags;

		private String rollupUsage;

		private List<Filter> filters = new ArrayList<>();

		public Builder(String metric, Aggregator aggregator) {
			Objects.requireNonNull(metric, "metric");
			Objects.requireNonNull(aggregator, "aggregator");
			this.aggregator = aggregator;
			this.metric = metric;
		}

		public Builder rate() {
			this.rate = true;
			return this;
		}

		public Builder rate(RateOptions rateOptions) {
			this.rate = true;
			this.rateOptions = rateOptions;
			return this;
		}

		public Builder downsample(String downsample) {
			if (StringUtils.isNoneBlank(downsample)) {
				this.downsample = downsample;
			}
			return this;
		}

		public Builder rollupUsage(String rollupUsage) {
			if (StringUtils.isNoneBlank(rollupUsage)) {
				this.rollupUsage = rollupUsage;
			}
			return this;
		}

		public Builder explicitTags() {
			this.explicitTags = true;
			return this;
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

		public Builder filter(Filter filter) {
			filters.add(filter);
			return this;
		}

		public Builder filter(List<Filter> filterList) {
			if (!CollectionUtils.isEmpty(filterList)) {
				filters.addAll(filterList);
			}
			return this;
		}

		public SubQuery build() {
			SubQuery subQuery = new SubQuery();
			subQuery.aggregator = this.aggregator;
			subQuery.downsample = this.downsample;
			subQuery.metric = this.metric;
			subQuery.tags = this.tags;
			subQuery.filters = this.filters;
			subQuery.rate = this.rate;
			subQuery.rateOptions = this.rateOptions;
			subQuery.explicitTags = this.explicitTags;
			subQuery.rollupUsage = this.rollupUsage;
			return subQuery;
		}

	}

	/***
	 * metric和aggregator是必传参数
	 */
	public static class MetricBuilder {
		private String metric;

		public MetricBuilder(String metric) {
			this.metric = metric;
		}

		public Builder aggregator(Aggregator aggregator) {
			return new Builder(metric, aggregator);
		}
	}

	public static class AggregatorBuilder {
		private Aggregator aggregator;

		public AggregatorBuilder(Aggregator aggregator) {
			this.aggregator = aggregator;
		}

		public Builder metric(String metric) {
			return new Builder(metric, aggregator);
		}
	}

	/***
	 * 聚合方法的枚举类
	 */
	public static enum Aggregator {

		/***
		 * 聚合方法
		 */
		MULT("mult"), P90("p90"), ZIMSUM("zimsum"), MIMMAX("mimmax"), SUM("sum"), P50("p50"), NONE("none"), P95("p95"), EP99R7("ep99r7"), P75("p75"), P99("p99"), EP99R3("ep99r3"), EP95R7("ep95r7"), MIN("min"), AVG("avg"), EP75R7("ep75r7"), DEV("dev"), EP95R3("ep95r3"), EP75R3("ep75r3"), EP50R7("ep50r7"), EP90R7("ep90r7"), MIMMIN("mimmin"), P999("p999"), EP50R3("ep50r3"), EP90R3("ep90r3"), EP999R7("ep999r7"), LAST("last"), MAX("max"), COUNT("count"), EP999R3("ep999r3"), FIRST("first");

		private static final Map<String, Aggregator> AGG_MAP = Maps.newHashMapWithExpectedSize(Aggregator.values().length);

		static {
			for (Aggregator typeEnum : Aggregator.values()) {
				AGG_MAP.put(typeEnum.getName(), typeEnum);
			}
		}

		public static Aggregator getEnum(String name) {
			return AGG_MAP.get(name);
		}

		private String name;

		private Aggregator(String name) {
			this.name = name;
		}

		@JsonValue
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	/***
	 * 速率
	 */
	public static class RateOptions {

		private Boolean counter;

		private Boolean dropResets;

		private Long counterMax;

		private Long resetValue;

		public static Builder newBuilder() {
			return new Builder();
		}

		public static class Builder {

			private Boolean counter;

			private Boolean dropResets;

			private Long counterMax;

			private Long resetValue;

			public Builder() {

			}

			public Builder counter(boolean counter) {
				this.counter = counter;
				return this;
			}

			public Builder dropResets(boolean dropResets) {
				this.dropResets = dropResets;
				return this;
			}

			public Builder counterMax(long counterMax) {
				this.counterMax = counterMax;
				return this;
			}

			public Builder resetValue(long resetValue) {
				this.resetValue = resetValue;
				return this;
			}

			public RateOptions build() {
				RateOptions rateOptions = new RateOptions();
				if (counter != null) {
					rateOptions.counter = counter;
				}
				if (dropResets != null) {
					rateOptions.dropResets = dropResets;
				}
				if (counterMax != null) {
					rateOptions.counterMax = counterMax;
				}
				if (resetValue != null) {
					rateOptions.resetValue = resetValue;
				}
				return rateOptions;
			}
		}

	}

	/***
	 * 查询过滤器
	 */
	public static class Filter {

		private FilterType type;

		private String tagk;

		private String filter;

		private Boolean groupBy;

		public static class Builder {

			private FilterType type;

			private String tagk;

			private String filter;

			private Boolean groupBy;

			public Builder(FilterType type, String tagk, String filter, Boolean groupBy) {
				super();
				this.type = type;
				this.tagk = tagk;
				this.filter = filter;
				this.groupBy = groupBy;
			}

			public Builder(FilterType type, String tagk, String filter) {
				super();
				this.type = type;
				this.tagk = tagk;
				this.filter = filter;
			}

			public Filter build() {
				Filter f = new Filter();
				f.type = this.type;
				f.tagk = this.tagk;
				f.filter = this.filter;
				if (this.groupBy == true) {
					f.groupBy = this.groupBy;
				}

				return f;
			}

		}

		public static Builder filter(FilterType type, String tagk, String filter) {
			return new Builder(type, tagk, filter);
		}

		public static Builder filter(FilterType type, String tagk, String filter, Boolean groupBy) {
			return new Builder(type, tagk, filter, groupBy);
		}

		public static Builder filter(FilterType type, String filter) {
			return new Builder(type, null, filter);
		}

		/***
		 * 过滤类型枚举类
		 */
		public static enum FilterType {

			/**
			 * 具体说明可以手动调用openTSDB的接口获取 get请求ip:port/api/config/filters
			 */
			REGEXP("regexp"), IWILDCARD("iwildcard"), ILITERAL_OR("iliteral_or"), NOT_ILITERAL_OR("not_iliteral_or"), NOT_LITERAL_OR("not_literal_or"), LITERAL_OR("literal_or");

			private String name;

			private FilterType(String name) {
				this.name = name;
			}

			@JsonValue
			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			@Override
			public String toString() {
				return name;
			}

		}

	}

}