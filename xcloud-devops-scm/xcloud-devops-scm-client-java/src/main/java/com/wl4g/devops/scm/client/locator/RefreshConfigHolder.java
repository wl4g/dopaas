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
package com.wl4g.devops.scm.client.locator;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Queues;
import com.wl4g.devops.scm.common.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.scm.common.model.ReportInfo.ChangedRecord;

/**
 * Refresh configuration holder.
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月11日
 * @since
 */
public abstract class RefreshConfigHolder {

	/**
	 * Refresh current release meta
	 */
	final private static ThreadLocal<ReleaseMeta> releaseMeta = new InheritableThreadLocal<>();

	/**
	 * Refresh changed records.
	 */
	final private static Queue<ChangedRecord> changedQueue = Queues.newArrayBlockingQueue(32);

	public RefreshConfigHolder() {
		throw new IllegalStateException("Can't instantiate a utility class");
	}

	/**
	 * Gets & validate release meta
	 * 
	 * @param validate
	 * @return
	 */
	public static ReleaseMeta getReleaseMeta(boolean validate) {
		ReleaseMeta meta = releaseMeta.get();
		if (validate) {
			notNull(meta, "No available refresh releaseMeta");
			meta.validation(validate, validate);
		}
		return meta;
	}

	/**
	 * Sets release meta
	 * 
	 * @param newMeta
	 * @return
	 */
	public static ReleaseMeta setReleaseMeta(ReleaseMeta newMeta) {
		if (!isNull(newMeta)) {
			releaseMeta.set(newMeta);
		} else {
			pollReleaseMeta();
		}
		return newMeta;
	}

	/**
	 * Poll release meta.
	 * 
	 * @return
	 */
	public static ReleaseMeta pollReleaseMeta() {
		try {
			return releaseMeta.get();
		} finally {
			releaseMeta.remove();
		}
	}

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
		changedQueue.add(new ChangedRecord(changedKeys, getReleaseMeta(false)));
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