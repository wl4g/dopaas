package com.wl4g.devops.ci.config;

import static java.util.Objects.nonNull;

/**
 * CICD pipeline process, construction of relevant configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class JobProperties {

	private Long cleanScan = 30L;

	private Integer jobTimeout = 300;

	private Long sharedDependencyTryTimeoutMs = 300L;

	public Long getCleanScan() {
		return cleanScan;
	}

	public void setCleanScan(Long cleanScan) {
		if (nonNull(cleanScan)) {
			this.cleanScan = cleanScan;
		}
	}

	public Integer getJobTimeout() {
		return jobTimeout;
	}

	public void setJobTimeout(Integer jobTimeout) {
		if (nonNull(jobTimeout)) {
			this.jobTimeout = jobTimeout;
		}
	}

	public Long getSharedDependencyTryTimeoutMs() {
		return sharedDependencyTryTimeoutMs;
	}

	public void setSharedDependencyTryTimeoutMs(Long sharedDependencyTryTimeoutMs) {
		if (nonNull(sharedDependencyTryTimeoutMs)) {
			this.sharedDependencyTryTimeoutMs = sharedDependencyTryTimeoutMs;
		}
	}

}
