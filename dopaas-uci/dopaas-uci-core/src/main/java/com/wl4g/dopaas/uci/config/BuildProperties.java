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
package com.wl4g.dopaas.uci.config;

import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * CI/CD pipeline build processing, construction of relevant configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class BuildProperties {

	/** Clean for timeouts job interval time (Ms). */
	private Long jobCleanMaxIntervalMs = 30 * 1000L;

	/**
	 * Timeout practice of building a system completely, including multiple
	 * sub-projects (Ms).
	 */
	private Long jobTimeoutMs = 10 * 60 * 1000L;

	/**
	 * Shared dependent projects wait timeout when building concurrently.</br>
	 * {@link com.wl4g.dopaas.uci.config.CiProperties#applyDefaultProperties()}
	 */
	private Long sharedDependencyTryTimeoutMs;

	public Long getJobCleanMaxIntervalMs() {
		notNull(jobCleanMaxIntervalMs, "Job clean max interval must not be null.");
		isTrue(jobCleanMaxIntervalMs > 0, "Job clean max interval must greater than 0.");
		return jobCleanMaxIntervalMs;
	}

	public void setJobCleanMaxIntervalMs(Long jobCleanMaxIntervalMs) {
		if (nonNull(jobCleanMaxIntervalMs)) {
			isTrue(jobCleanMaxIntervalMs > 0, "Job clean max interval must greater than 0.");
			this.jobCleanMaxIntervalMs = jobCleanMaxIntervalMs;
		}
	}

	public Long getJobTimeoutMs() {
		notNull(jobTimeoutMs, "Job timeout must not be null.");
		isTrue(jobTimeoutMs > 0, "Job timeout must greater than 0.");
		return jobTimeoutMs;
	}

	public Long getJobTimeoutSec() {
		return TimeUnit.MILLISECONDS.toSeconds(getJobTimeoutMs());
	}

	public void setJobTimeoutMs(Long jobTimeoutMs) {
		if (nonNull(jobTimeoutMs)) {
			isTrue(jobTimeoutMs > 0, "Job timeout must greater than 0.");
			this.jobTimeoutMs = jobTimeoutMs;
		}
	}

	public Long getSharedDependencyTryTimeoutMs() {
		// notNull(sharedDependencyTryTimeoutMs, "Shared dependency try
		// timeoutMs must not be null.");
		// isTrue(sharedDependencyTryTimeoutMs > 0, "Shared dependency try
		// timeoutMs must greater than 0.");
		return sharedDependencyTryTimeoutMs;
	}

	public void setSharedDependencyTryTimeoutMs(Long sharedDependencyTryTimeoutMs) {
		if (nonNull(sharedDependencyTryTimeoutMs)) {
			isTrue(sharedDependencyTryTimeoutMs > 0, "Shared dependency try timeoutMs must greater than 0.");
			this.sharedDependencyTryTimeoutMs = sharedDependencyTryTimeoutMs;
		}
	}

}