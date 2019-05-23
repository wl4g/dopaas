package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;

public class Task extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer type;

	private Integer projectId;

	private Integer status;

	private String branchName;

	private String sha;

	private Integer parentId;

	private String command;

	private Integer tarType;

	private String result;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Integer getTarType() {
		return tarType;
	}

	public void setTarType(Integer tarType) {
		this.tarType = tarType;
	}
}