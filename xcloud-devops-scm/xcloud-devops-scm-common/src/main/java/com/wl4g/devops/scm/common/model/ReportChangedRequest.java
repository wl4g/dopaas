/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.scm.common.model;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

/**
 * {@link ReportChangedRequest}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-18
 * @since
 */
@Getter
@Setter
@Wither
public class ReportChangedRequest extends FetchReleaseConfigRequest {
	final private static long serialVersionUID = 2523769504519533902L;

	private Collection<ChangedRecord> changedRecords;

	public ReportChangedRequest(Collection<ChangedRecord> changedRecords) {
		super();
		this.changedRecords = changedRecords;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	/**
	 * {@link ChangedRecord}
	 *
	 * @since
	 */
	@Getter
	@Setter
	@Wither
	public static class ChangedRecord {

		private Set<String> changedKeys = new HashSet<>();
		private ReleaseConfigInfo sources = new ReleaseConfigInfo();

		public ChangedRecord() {
			super();
		}

		public ChangedRecord(Set<String> changedKeys, ReleaseConfigInfo sources) {
			this.changedKeys = changedKeys;
			this.sources = sources;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

	}

}