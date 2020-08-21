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
package com.wl4g.devops.scm.common.config;

import static com.wl4g.components.common.lang.Assert2.stateOf;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

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
	 * Watching timeout on waiting to read data from the SCM Server.
	 */
	private long longPollTimeout = DEF_WATCH_R_TIMEOUT_MS;

	/** Connect timeout */
	private long connectTimeout = DEF_CONN_TIMEOUT_MS;

	/** Max response size */
	private long maxResponseSize = 65535;

	public long getLongPollTimeout() {
		return longPollTimeout;
	}

	public void setLongPollTimeout(long longPollingTimeout) {
		stateOf(longPollingTimeout > 0, "longPollingTimeout > 0");
		this.longPollTimeout = longPollingTimeout;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(long connectTimeout) {
		stateOf(connectTimeout > 0, "connectTimeout > 0");
		this.connectTimeout = connectTimeout;
	}

	public long getMaxResponseSize() {
		return maxResponseSize;
	}

	public void setMaxResponseSize(long maxResponseSize) {
		stateOf(maxResponseSize > 0, "maxResponseSize > 0");
		this.maxResponseSize = maxResponseSize;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

	/**
	 * Default Fetch timeout on waiting to read data from the SCM Server.
	 */
	public final static long DEF_WATCH_R_TIMEOUT_MS = 30 * 1000L;

	/**
	 * Default connect timeoutMs.
	 */
	public final static long DEF_CONN_TIMEOUT_MS = 6 * 1000L;

}