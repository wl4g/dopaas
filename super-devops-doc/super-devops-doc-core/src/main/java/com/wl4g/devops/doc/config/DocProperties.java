package com.wl4g.devops.doc.config;

/**
 * @author vjay
 * @date 2020-01-15 16:17:00
 */
public class DocProperties {

	private String basePath;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getFilePath(String subPath) {
		return basePath + subPath;
	}
}
