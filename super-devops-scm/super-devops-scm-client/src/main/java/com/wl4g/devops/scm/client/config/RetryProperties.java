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