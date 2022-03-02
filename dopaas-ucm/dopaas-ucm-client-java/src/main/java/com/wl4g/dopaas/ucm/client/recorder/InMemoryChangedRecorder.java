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
package com.wl4g.dopaas.ucm.client.recorder;

import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static java.util.Collections.synchronizedList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.wl4g.dopaas.common.bean.ucm.model.ReportChangedRequest.ChangedRecord;

/**
 * {@link InMemoryChangedRecorder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-20
 * @since
 */
public class InMemoryChangedRecorder implements ChangedRecorder {

    /** Capacity size */
    private final int capacity;

    public InMemoryChangedRecorder() {
        this(16);
    }

    public InMemoryChangedRecorder(int capacity) {
        isTrueOf(capacity > 0, "capacity");
        this.capacity = capacity;
    }

    // --- Release config source. ---

    @Override
    public void save(ReleasedWrapper wrapper) {
        if (changedStore.size() <= capacity) {
            changedStore.add(wrapper);
        }
    }

    @Override
    public ReleasedWrapper last() {
        if (changedStore.isEmpty()) {
            return null;
        }
        int size = changedStore.size();
        if (size > 1) {
            return changedStore.get(size - 2);
        }
        return changedStore.get(0);
    }

    @Override
    public ReleasedWrapper current() {
        if (changedStore.isEmpty()) {
            return null;
        }
        return changedStore.get(changedStore.size() - 1);
    }

    // --- Changed records. ---

    @Override
    public Collection<ChangedRecord> pollAll() {
        try {
            return changedRecordStore;
        } finally {
            changedRecordStore.clear();
        }
    }

    @Override
    public Collection<ChangedRecord> getAll() {
        return changedRecordStore;
    }

    @Override
    public void save(Set<String> changedKeys, ReleasedWrapper wrap) {
        changedRecordStore.add(new ChangedRecord(changedKeys, null, null));
    }

    /** Refresh configuration source cache registry. */
    private static final List<ReleasedWrapper> changedStore = synchronizedList(new LinkedList<>());

    /** Refreshed configuration changed records. */
    private static final List<ChangedRecord> changedRecordStore = synchronizedList(new LinkedList<>());

}