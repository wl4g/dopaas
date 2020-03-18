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
package com.wl4g.devops.ci.config;

import java.io.Serializable;

/**
 * Log cleaner properties configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-13
 * @since
 */
public class LogCleanerProperties implements Serializable {
	private static final long serialVersionUID = -7007748978859003620L;

	/**
	 * For example, the time (in seconds) to wait for initialization after
	 * restart.
	 */
	private long initialDelaySec = 3 * 60;

	/**
	 * Interval between log cleaner scans (seconds).
	 */
	private long periodSec = 30 * 60;

	/**
	 * Pipeline construction history, maximum age hours saved, overdue will be
	 * cleared.
	 */
	private long pipeHistoryRetainHour = 30 * 24;

	public long getInitialDelaySec() {
		return initialDelaySec;
	}

	public void setInitialDelaySec(long initialDelaySec) {
		this.initialDelaySec = initialDelaySec;
	}

	public long getPeriodSec() {
		return periodSec;
	}

	public void setPeriodSec(long periodSec) {
		this.periodSec = periodSec;
	}

	public long getPipeHistoryRetainHour() {
		return pipeHistoryRetainHour;
	}

	public void setPipeHistoryRetainHour(long pipeHistoryRetainHour) {
		this.pipeHistoryRetainHour = pipeHistoryRetainHour;
	}

}