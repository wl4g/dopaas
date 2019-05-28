package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;

public class Project extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String projectName;

	private String gitUrl;

	private Integer appGroupId;

	private String tarPath;

	private String parentAppHome;

	private String linkAppHome;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Integer getAppGroupId() {
		return appGroupId;
	}

	public void setAppGroupId(Integer appGroupId) {
		this.appGroupId = appGroupId;
	}

	public String getTarPath() {
		return tarPath;
	}

	public void setTarPath(String tarPath) {
		this.tarPath = tarPath;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getParentAppHome() {
		return parentAppHome;
	}

	public void setParentAppHome(String parentAppHome) {
		this.parentAppHome = parentAppHome;
	}

	public String getLinkAppHome() {
		return linkAppHome;
	}

	public void setLinkAppHome(String linkAppHome) {
		this.linkAppHome = linkAppHome;
	}
}