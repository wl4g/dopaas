package com.wl4g.devops.scm.config;

import java.io.Serializable;

import org.springframework.util.Assert;

public abstract class AbstractScmProperties implements Serializable {

	private static final long serialVersionUID = -242805976296191411L;

	/**
	 * Watch long-polling timeout on waiting to read data from the SCM Server.
	 */
	private long longPollingTimeout = 30 * 1000;

	/** Connect timeout */
	private int connectTimeout = 5 * 1000;

	/** Max response size */
	private int maxResponseSize = 65535;

	public long getLongPollingTimeout() {
		return longPollingTimeout;
	}

	public void setLongPollingTimeout(long longPollingTimeout) {
		Assert.state(longPollingTimeout > 0, String.format("Invalid value for long polling timeout for %s", longPollingTimeout));
		this.longPollingTimeout = longPollingTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		Assert.state(connectTimeout > 0, String.format("Invalid value for connect timeout for %s", connectTimeout));
		this.connectTimeout = connectTimeout;
	}

	public int getMaxResponseSize() {
		return maxResponseSize;
	}

	public void setMaxResponseSize(int maxResponseSize) {
		Assert.state(maxResponseSize > 0, String.format("Invalid value for max response size for %s", maxResponseSize));
		this.maxResponseSize = maxResponseSize;
	}

	@Override
	public String toString() {
		return "AbstractScmProperties [longPollingTimeout=" + longPollingTimeout + ", connectTimeout=" + connectTimeout
				+ ", maxResponseSize=" + maxResponseSize + "]";
	}

}
