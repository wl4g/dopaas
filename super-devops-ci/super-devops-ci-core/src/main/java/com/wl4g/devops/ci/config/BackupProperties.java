package com.wl4g.devops.ci.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * CICD pipeline process, build the relevant configuration class for backup and
 * save after completion.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class BackupProperties {

	private String baseDir;

	public String getBaseDir() {
		if (isBlank(baseDir)) {// if blank ,user default
			baseDir = System.getProperties().getProperty("user.home") + "/git/bak";
		}
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
}
