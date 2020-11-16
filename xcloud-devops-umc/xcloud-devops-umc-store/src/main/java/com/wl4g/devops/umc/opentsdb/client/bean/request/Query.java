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
package com.wl4g.devops.umc.opentsdb.client.bean.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 详见<a>http://opentsdb.net/docs/build/html/api_http/query/index.html</a>
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:45
 * @Version: 1.0
 */
@SuppressWarnings("unused")
public class Query {

	/**
	 * 形如1h-ago
	 */
	private String start;

	/***
	 * 形如1s-ago
	 */
	private String end;

	private Boolean msResolution;

	private Boolean noAnnotations;

	private Boolean globalAnnotations;

	private Boolean showTSUIDs;

	private Boolean showSummary;

	private Boolean showStats;

	private Boolean showQuery;

	private String timezone;

	private Boolean useCalendar;

	private Boolean delete;

	private List<SubQuery> queries;

	public static class Builder {

		private Long startTimestamp;

		private Long endTimestamp;

		private String start;

		private String end;

		private Boolean msResolution;

		private Boolean noAnnotations;

		private Boolean globalAnnotations;

		private Boolean showTSUIDs;

		private Boolean showSummary;

		private Boolean showStats;

		private Boolean showQuery;

		private String timezone;

		private Boolean useCalendar;

		private List<SubQuery> queries = new ArrayList<>();

		public Query build() {
			Query query = new Query();
			if (this.startTimestamp == null && StringUtils.isBlank(this.start)) {
				throw new IllegalArgumentException("the start time must be set");
			}

			if (CollectionUtils.isEmpty(queries)) {
				throw new IllegalArgumentException("the subQueries must be set");
			}
			query.queries = this.queries;

			if (StringUtils.isNoneBlank(start)) {
				query.start = this.start;
			} else if (this.startTimestamp != null) {
				query.start = this.startTimestamp.toString();
			}

			if (StringUtils.isNoneBlank(end)) {
				query.end = this.end;
			} else if (this.endTimestamp != null) {
				query.end = this.endTimestamp.toString();
			}

			query.msResolution = this.msResolution;
			query.noAnnotations = this.noAnnotations;
			query.globalAnnotations = this.globalAnnotations;
			query.showTSUIDs = this.showTSUIDs;
			query.showSummary = this.showSummary;
			query.showStats = this.showStats;
			query.showQuery = this.showQuery;
			query.timezone = this.timezone;
			query.useCalendar = this.useCalendar;
			return query;
		}

		public Builder begin(Long startTimestamp) {
			this.startTimestamp = startTimestamp;
			return this;
		}

		public Builder end(Long endTimestamp) {
			this.endTimestamp = endTimestamp;
			return this;
		}

		public Builder begin(String start) {
			this.start = start;
			return this;
		}

		public Builder end(String end) {
			this.end = end;
			return this;
		}

		public Builder msResolution() {
			this.msResolution = true;
			return this;
		}

		public Builder noAnnotations() {
			this.noAnnotations = true;
			return this;
		}

		public Builder globalAnnotations() {
			this.globalAnnotations = true;
			return this;
		}

		public Builder showTSUIDs() {
			this.showTSUIDs = true;
			return this;
		}

		public Builder showSummary() {
			this.showSummary = true;
			return this;
		}

		public Builder showStats() {
			this.showStats = true;
			return this;
		}

		public Builder showQuery() {
			this.showQuery = true;
			return this;
		}

		public Builder timezone(String timezone) {
			this.timezone = timezone;
			return this;
		}

		public Builder useCalendar() {
			this.useCalendar = true;
			return this;
		}

		public Builder sub(SubQuery subQuery) {
			queries.add(subQuery);
			return this;
		}

		public Builder sub(List<SubQuery> subQueryList) {
			if (!CollectionUtils.isEmpty(subQueryList)) {
				queries.addAll(subQueryList);
			}
			return this;
		}

	}

	public static Builder begin(Long startTimestamp) {
		return new Builder().begin(startTimestamp);
	}

	public static Builder begin(String start) {
		return new Builder().begin(start);
	}

	/***
	 * 设置私有，不允许用户设置delete属性，会在client中通过反射来设置
	 * 
	 * @param delete
	 */
	private void setDelete(Boolean delete) {
		this.delete = delete;
	}
}