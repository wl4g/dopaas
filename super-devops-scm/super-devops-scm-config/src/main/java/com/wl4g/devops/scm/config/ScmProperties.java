/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.config;

import com.wl4g.devops.support.task.GenericTaskRunner.TaskProperties;

public class ScmProperties extends TaskProperties {
	private static final long serialVersionUID = -4696830861294720221L;

	private int corePoolSize = 10;
	private int maxPoolSize = 25;
	private int queueCapacity = 100;
	private long scanDelay = (long) (0.2 * 1_000L);
	private long defaultTimeout = 30_000L;

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public long getScanDelay() {
		return scanDelay;
	}

	public void setScanDelay(long scanDelayTime) {
		this.scanDelay = scanDelayTime;
	}

	public long getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setDefaultTimeout(long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

}
