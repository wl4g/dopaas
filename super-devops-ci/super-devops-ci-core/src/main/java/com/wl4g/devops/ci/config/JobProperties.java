package com.wl4g.devops.ci.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * CICD pipeline process, construction of relevant configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class JobProperties {

	private Integer cleanScan = 30;

	private Integer cleanTimeout = 300;

	private Integer shareDependencyTryTimeout = 300;

	private String baseDir;


	public Integer getCleanScan() {
		return cleanScan;
	}

	public void setCleanScan(Integer cleanScan) {
		this.cleanScan = cleanScan;
	}

	public Integer getCleanTimeout() {
		return cleanTimeout;
	}

	public void setCleanTimeout(Integer cleanTimeout) {
		this.cleanTimeout = cleanTimeout;
	}

	public Integer getShareDependencyTryTimeout() {
		return shareDependencyTryTimeout;
	}

	public void setShareDependencyTryTimeout(Integer jobShareDenpenyTryTimeout) {
		this.shareDependencyTryTimeout = jobShareDenpenyTryTimeout;
	}

	public String getBaseDir() {
		if(isBlank(baseDir)){
			baseDir = System.getProperties().getProperty("user.home") + "/git/jobs";
		}
		return baseDir;
	}

	public String getBaseDir(Integer taskHisId) {
		return getBaseDir()+"/"+taskHisId;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String getLogBaseDir(Integer taskHisId) {
		return getBaseDir(taskHisId);
	}

	public String getBackupDir(Integer taskHisId){
        return getBaseDir(taskHisId);
    }


}
