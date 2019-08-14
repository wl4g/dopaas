package com.wl4g.devops.common.bean.ci;

import java.io.Serializable;

import com.wl4g.devops.common.bean.BaseBean;

public class TaskSign extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private Integer taskId;

	private Integer dependencyId;

	private String shaGit;

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getDependenvyId() {
		return dependencyId;
	}

	public void setDependenvyId(Integer dependenvyId) {
		this.dependencyId = dependenvyId;
	}

	public String getShaGit() {
		return shaGit;
	}

	public void setShaGit(String shaGit) {
		this.shaGit = shaGit == null ? null : shaGit.trim();
	}
}