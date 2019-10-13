package com.wl4g.devops.ci.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * CICD pipeline process, construction of relevant configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class BuildProperties {

	private Integer jobCleanScan = 30;

	private Integer jobCleanTimeout = 300;

	private Integer jobShareDependencyTryTimeout = 300;

	private String logBaseDir;

	public Integer getJobCleanScan() {
		return jobCleanScan;
	}

	public void setJobCleanScan(Integer jobCleanScan) {
		this.jobCleanScan = jobCleanScan;
	}

	public Integer getJobCleanTimeout() {
		return jobCleanTimeout;
	}

	public void setJobCleanTimeout(Integer jobCleanTimeout) {
		this.jobCleanTimeout = jobCleanTimeout;
	}

	public Integer getJobShareDependencyTryTimeout() {
		return jobShareDependencyTryTimeout;
	}

	public void setJobShareDependencyTryTimeout(Integer jobShareDenpenyTryTimeout) {
		this.jobShareDependencyTryTimeout = jobShareDenpenyTryTimeout;
	}

	public String getLogBaseDir() {
		if (isBlank(logBaseDir)) {// if blank ,user default
			logBaseDir = System.getProperties().getProperty("user.home") + "/git/log";
		}
		return logBaseDir;
	}

	public void setLogBaseDir(String logPath) {
		this.logBaseDir = logPath;
	}
}
