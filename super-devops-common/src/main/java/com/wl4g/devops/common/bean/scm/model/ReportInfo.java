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
package com.wl4g.devops.common.bean.scm.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReportInfo extends GetRelease {
	final private static long serialVersionUID = 2523769504519533902L;

	private Collection<ChangedRecord> changedRecords;

	public ReportInfo(Collection<ChangedRecord> changedRecords) {
		super();
		this.changedRecords = changedRecords;
	}

	public Collection<ChangedRecord> getChangedRecords() {
		return changedRecords;
	}

	public void setChangedRecords(Collection<ChangedRecord> changedRecords) {
		this.changedRecords = changedRecords;
	}

	@Override
	public String toString() {
		return "ReportInfo [changedRecords=" + changedRecords + "]";
	}

	public static class ChangedRecord {

		private Set<String> changedKeys = new HashSet<>();

		private ReleaseMeta meta = new ReleaseMeta();

		public ChangedRecord() {
			super();
		}

		public ChangedRecord(Set<String> changedKeys, ReleaseMeta meta) {
			super();
			this.changedKeys = changedKeys;
			this.meta = meta;
		}

		public Set<String> getChangedKeys() {
			return changedKeys;
		}

		public void setChangedKeys(Set<String> changedKeys) {
			this.changedKeys = changedKeys;
		}

		public ReleaseMeta getMeta() {
			return meta;
		}

		public void setMeta(ReleaseMeta meta) {
			this.meta = meta;
		}

		@Override
		public String toString() {
			return "ChangedInfo [changedKeys=" + changedKeys + ", meta=" + meta + "]";
		}

	}

}