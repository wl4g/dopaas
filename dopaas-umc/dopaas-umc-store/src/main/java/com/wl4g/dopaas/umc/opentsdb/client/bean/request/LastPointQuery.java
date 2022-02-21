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
package com.wl4g.dopaas.umc.opentsdb.client.bean.request;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午2:48
 * @Version: 1.0
 */
public class LastPointQuery {

	private List<LastPointSubQuery> queries;

	private boolean resolveNames;

	private int backScan;

	public static class Builder {

		private List<LastPointSubQuery> queries = new ArrayList<>();

		private boolean resolveNames = true;

		private int backScan;

		public LastPointQuery build() {
			LastPointQuery query = new LastPointQuery();
			query.queries = this.queries;
			query.resolveNames = this.resolveNames;
			query.backScan = this.backScan;
			return query;
		}

		public Builder(LastPointSubQuery query) {
			this.queries.add(query);
		}

		public Builder(List<LastPointSubQuery> queries) {
			this.queries = queries;
		}

		public Builder resolveNames(boolean resolveNames) {
			this.resolveNames = resolveNames;
			return this;
		}

		public Builder backScan(int backScan) {
			this.backScan = backScan;
			return this;
		}

	}

	public List<LastPointSubQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<LastPointSubQuery> queries) {
		this.queries = queries;
	}

	public boolean isResolveNames() {
		return resolveNames;
	}

	public void setResolveNames(boolean resolveNames) {
		this.resolveNames = resolveNames;
	}

	public int getBackScan() {
		return backScan;
	}

	public void setBackScan(int backScan) {
		this.backScan = backScan;
	}

	public static Builder sub(LastPointSubQuery query) {
		return new Builder(query);
	}

	public static Builder sub(List<LastPointSubQuery> queries) {
		return new Builder(queries);
	}

}