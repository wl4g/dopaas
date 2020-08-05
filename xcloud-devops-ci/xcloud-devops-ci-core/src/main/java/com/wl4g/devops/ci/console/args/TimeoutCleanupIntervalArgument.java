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
package com.wl4g.devops.ci.console.args;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;

import com.wl4g.shell.annotation.ShellOption;

/**
 * @author vjay
 * @date 2019-05-21 16:18:00
 */
public class TimeoutCleanupIntervalArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "t", lopt = "maxIntervalMs", help = "Global jobs timeout finalizer max-intervalMs", required = true)
	private Long maxIntervalMs;

	public Long getMaxIntervalMs() {
		notNull(maxIntervalMs, "Job timeout cleanup max intervalMs must not be null.");
		isTrue(maxIntervalMs > 0, "Job timeout cleanup max intervalMs must greater than 0.");
		return maxIntervalMs;
	}

	public void setMaxIntervalMs(Long maxIntervalMs) {
		notNull(maxIntervalMs, "Job timeout cleanup max intervalMs must not be null.");
		isTrue(maxIntervalMs > 0, "Job timeout cleanup max intervalMs must greater than 0.");
		this.maxIntervalMs = maxIntervalMs;
	}

}