package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.io.Serializable;
import java.util.List;

public class Task extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String taskName;

	private Integer appClusterId;

	private Integer projectId;

	private String branchName;

	private Integer tarType;

	private String branchType;

	private String preCommand;

	private String postCommand;

	private List<AppInstance> instances;

	private List<TaskDetail> taskDetails;

	/* other */
	private String groupName;

	public String getPreCommand() {
		return preCommand;
	}

	public void setPreCommand(String preCommand) {
		this.preCommand = preCommand == null ? null : preCommand.trim();
	}

	public String getPostCommand() {
		return postCommand;
	}

	public void setPostCommand(String postCommand) {
		this.postCommand = postCommand == null ? null : postCommand.trim();
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName == null ? null : taskName.trim();
	}

	public Integer getAppClusterId() {
		return appClusterId;
	}

	public void setAppClusterId(Integer appClusterId) {
		this.appClusterId = appClusterId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName == null ? null : branchName.trim();
	}

	public Integer getTarType() {
		return tarType;
	}

	public void setTarType(Integer tarType) {
		this.tarType = tarType;
	}

	public String getBranchType() {
		return branchType;
	}

	public void setBranchType(String branchType) {
		this.branchType = branchType == null ? null : branchType.trim();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public List<TaskDetail> getTaskDetails() {
		return taskDetails;
	}

	public void setTaskDetails(List<TaskDetail> taskDetails) {
		this.taskDetails = taskDetails;
	}

	@Override
	public String toString() {
		return "Task{" + "taskName='" + taskName + '\'' + ", appClusterId=" + appClusterId + ", projectId=" + projectId
				+ ", branchName='" + branchName + '\'' + ", tarType=" + tarType + ", branchType='" + branchType + '\''
				+ ", preCommand='" + preCommand + '\'' + ", postCommand='" + postCommand + '\'' + ", instances=" + instances
				+ ", taskDetails=" + taskDetails + ", groupName='" + groupName + '\'' + '}';
	}
}