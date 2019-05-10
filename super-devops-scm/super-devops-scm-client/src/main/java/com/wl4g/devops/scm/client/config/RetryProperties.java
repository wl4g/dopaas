/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.client.config;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.devops.scm.client.retry")
public class RetryProperties implements Serializable {
	private static final long serialVersionUID = -3652519510077023579L;

	private long minPeriod = 3_000L;
	private long maxPeriod = 8_000L;
	private int maxAttempts = 4;

	public long getRandomSleepPeriod() {
		return (long) (Math.random() * this.getMaxPeriod() + this.getMinPeriod());
	}

	public long getMinPeriod() {
		return minPeriod;
	}

	public void setMinPeriod(long minPeriod) {
		this.minPeriod = minPeriod;
	}

	public long getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(long maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

}