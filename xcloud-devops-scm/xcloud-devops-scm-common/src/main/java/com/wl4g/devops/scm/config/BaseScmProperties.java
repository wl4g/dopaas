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
package com.wl4g.devops.scm.config;

import static com.wl4g.components.common.lang.Assert2.state;
import static java.lang.String.format;

import java.io.Serializable;

/**
 * {@link BaseScmProperties}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-10
 * @since
 */
public abstract class BaseScmProperties implements Serializable {
	private static final long serialVersionUID = -242805976296191411L;

	/**
	 * Watch long-polling timeout on waiting to read data from the SCM Server.
	 */
	private long longPollTimeout = 30 * 1000;

	/** Connect timeout */
	private int connectTimeout = 5 * 1000;

	/** Max response size */
	private int maxResponseSize = 65535;

	public long getLongPollTimeout() {
		return longPollTimeout;
	}

	public void setLongPollTimeout(long longPollingTimeout) {
		state(longPollingTimeout > 0, format("Invalid value for long polling timeout for %s", longPollingTimeout));
		this.longPollTimeout = longPollingTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		state(connectTimeout > 0, String.format("Invalid value for connect timeout for %s", connectTimeout));
		this.connectTimeout = connectTimeout;
	}

	public int getMaxResponseSize() {
		return maxResponseSize;
	}

	public void setMaxResponseSize(int maxResponseSize) {
		state(maxResponseSize > 0, String.format("Invalid value for max response size for %s", maxResponseSize));
		this.maxResponseSize = maxResponseSize;
	}

	@Override
	public String toString() {
		return "AbstractScmProperties [longPollingTimeout=" + longPollTimeout + ", connectTimeout=" + connectTimeout
				+ ", maxResponseSize=" + maxResponseSize + "]";
	}

}