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
package com.wl4g.devops.support.concurrent.locks;

import static com.wl4g.devops.components.tools.common.lang.SystemUtils2.GLOBAL_PROCESS_SERIAL;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;

import com.google.common.annotations.Beta;

/**
 * Abstract distributed lock.</br>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年3月21日
 * @since
 */
@Beta
public abstract class AbstractDistributedLock implements Lock, Serializable {
	private static final long serialVersionUID = -3633610156752730462L;

	/** Current locker name. */
	final protected String name;

	/** Current locker request ID. */
	final protected String currentProcessId;

	/** Current locker expired time(MS). */
	final protected long expiredMs;

	public AbstractDistributedLock(String name, String currentProcessId, long expiredMs) {
		hasText(name, "Lock name must not be empty.");
		hasText(currentProcessId, "Lock current processId must not be empty.");
		isTrue(expiredMs > 0, "Lock expiredMs must greater than 0");
		this.name = name;
		this.currentProcessId = currentProcessId;
		this.expiredMs = expiredMs;
	}

	/**
	 * Get current thread unique process ID. </br>
	 * 
	 * <pre>
	 * Host serial + local processId + threadId
	 * </pre>
	 * 
	 * @return
	 */
	public final static String getThreadCurrentProcessId() {
		return GLOBAL_PROCESS_SERIAL + "-" + Thread.currentThread().getId();
	}

}