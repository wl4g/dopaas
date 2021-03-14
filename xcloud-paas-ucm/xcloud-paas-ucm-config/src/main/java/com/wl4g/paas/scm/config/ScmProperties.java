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
package com.wl4g.paas.scm.config;

import com.wl4g.paas.scm.common.BaseScmProperties;

public class ScmProperties extends BaseScmProperties {
	private static final long serialVersionUID = -4696830861294720221L;

	/** Deferred long-polling properties */

	private int corePoolSize = 10;
	private int maxPoolSize = 25;
	private int queueCapacity = 1024;

	/** Watching delay */
	private long watchDelay = 200L;

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

	public long getWatchDelay() {
		return watchDelay;
	}

	public void setWatchDelay(long watchDelay) {
		this.watchDelay = watchDelay;
	}

	@Override
	public String toString() {
		return "ScmProperties [corePoolSize=" + corePoolSize + ", maxPoolSize=" + maxPoolSize + ", queueCapacity=" + queueCapacity
				+ ", watchDelay=" + watchDelay + "]";
	}

}