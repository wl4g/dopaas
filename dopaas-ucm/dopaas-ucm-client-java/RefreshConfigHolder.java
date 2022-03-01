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
package com.wl4g.dopaas.ucm.client;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Queues;
import com.wl4g.dopaas.ucm.common.command.WatchCommandResult;
import com.wl4g.dopaas.ucm.common.command.ReportCommand.ChangedRecord;

/**
 * Refresh configuration holder.
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月11日
 * @since
 */
public abstract class RefreshConfigHolder {

	/**
	 * Refresh current release config source
	 */
	private final static ThreadLocal<WatchCommandResult> releaseConfig = new InheritableThreadLocal<>();

	/**
	 * Refresh changed records.
	 */
	private final static Queue<ChangedRecord> changedQueue = Queues.newArrayBlockingQueue(32);

	private RefreshConfigHolder() {
		throw new IllegalStateException("Cannot instantiate");
	}

	/**
	 * Gets & validate release meta
	 * 
	 * @param valid
	 * @return
	 */
	public static WatchCommandResult getReleaseConfig(boolean valid) {
		WatchCommandResult meta = releaseConfig.get();
		if (valid) {
			notNull(meta, "No available refresh releaseMeta");
			meta.validation(valid, valid);
		}
		return meta;
	}

	/**
	 * Sets release config source
	 * 
	 * @param newMeta
	 * @return
	 */
	static WatchCommandResult setReleaseConfig(WatchCommandResult result) {
		if (!isNull(result)) {
			releaseConfig.set(result);
		} else {
			pollReleaseConfig();
		}
		return result;
	}

	/**
	 * Poll release config source.
	 * 
	 * @return
	 */
	public static WatchCommandResult pollReleaseConfig() {
		try {
			return releaseConfig.get();
		} finally {
			releaseConfig.remove();
		}
	}

	// --- Changed records .---

	/**
	 * Poll chanaged keys all.
	 * 
	 * @return
	 */
	public static Collection<ChangedRecord> pollChangedAll() {
		try {
			return changedQueue;
		} finally {
			changedQueue.clear();
		}
	}

	/**
	 * Add changed keys.
	 * 
	 * @param changedKeys
	 */
	public static void addChanged(Set<String> changedKeys) {
		changedQueue.add(new ChangedRecord(changedKeys, getReleaseConfig(false)));
	}

	/**
	 * Gets changed keys
	 * 
	 * @return
	 */
	public static Queue<ChangedRecord> getChangedQueues() {
		return changedQueue;
	}

}