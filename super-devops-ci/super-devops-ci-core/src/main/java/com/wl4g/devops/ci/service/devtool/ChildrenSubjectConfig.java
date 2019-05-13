package com.wl4g.devops.ci.service.devtool;

/**
 * @author vjay
 * @date 2019-05-07 09:44:00
 */
public class ChildrenSubjectConfig {


	private String path;
	private String tarName;
	private String alias;

	public ChildrenSubjectConfig(String path, String tarName, String alias) {
		this.path = path;
		this.tarName = tarName;
		this.alias = alias;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTarName() {
		return tarName;
	}

	public void setTarName(String tarName) {
		this.tarName = tarName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
