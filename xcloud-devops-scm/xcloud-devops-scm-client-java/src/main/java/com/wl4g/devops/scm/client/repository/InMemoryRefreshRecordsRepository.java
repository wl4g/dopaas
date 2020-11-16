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
package com.wl4g.devops.scm.client.repository;

import static com.wl4g.components.common.lang.Assert2.isTrueOf;
import static java.util.Collections.synchronizedList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.wl4g.devops.scm.common.model.ReportChangedRequest.ChangedRecord;

/**
 * {@link InMemoryRefreshRecordsRepository}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-20
 * @since
 */
public class InMemoryRefreshRecordsRepository implements RefreshRecordsRepository {

	/** Capacity size */
	private final int capacity;

	public InMemoryRefreshRecordsRepository() {
		this(10);
	}

	public InMemoryRefreshRecordsRepository(int capacity) {
		isTrueOf(capacity > 0, "capacity");
		this.capacity = capacity;
	}

	// --- Release config source. ---

	@Override
	public void saveReleaseSource(ReleaseConfigSourceWrapper wrapper) {
		if (refreshConfigStore.size() <= capacity) {
			refreshConfigStore.add(wrapper);
		}
	}

	@Override
	public ReleaseConfigSourceWrapper getLastReleaseSource() {
		if (refreshConfigStore.isEmpty()) {
			return null;
		}
		int size = refreshConfigStore.size();
		if (size > 1) {
			return refreshConfigStore.get(size - 2);
		}
		return refreshConfigStore.get(0);
	}

	@Override
	public ReleaseConfigSourceWrapper getCurrentReleaseSource() {
		if (refreshConfigStore.isEmpty()) {
			return null;
		}
		return refreshConfigStore.get(refreshConfigStore.size() - 1);
	}

	// --- Changed records. ---

	@Override
	public Collection<ChangedRecord> pollChangedAll() {
		try {
			return changeRecordStore;
		} finally {
			changeRecordStore.clear();
		}
	}

	@Override
	public Collection<ChangedRecord> getChangedAll() {
		return changeRecordStore;
	}

	@Override
	public void saveChanged(Set<String> changedKeys, ReleaseConfigSourceWrapper wrapper) {
		changeRecordStore.add(new ChangedRecord(changedKeys, wrapper.getRelease()));
	}

	/** Refresh configuration source cache registry. */
	private static final List<ReleaseConfigSourceWrapper> refreshConfigStore = synchronizedList(new LinkedList<>());

	/** Refreshed configuration changed records. */
	private static final List<ChangedRecord> changeRecordStore = synchronizedList(new LinkedList<>());

}