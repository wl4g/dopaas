/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.erm.AppInstance;

import java.io.Serializable;
import java.util.List;

public class Task extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String taskName;

	private Integer appClusterId;

	private Integer projectId;

	private String branchName;

	private String providerKind;

	private String branchType;

	private String buildCommand;

	private String preCommand;

	private String postCommand;

	private Integer contactGroupId;

	private String envType;

	private Integer pcmId;

	private String pmPlatform;

	private String parentAppHome;

	/** 构建的文件/目录路径（maven项目的target目录，vue项目的dist目录） */
	private String assetsPath;

	private List<AppInstance> instances;

	private List<TaskInstance> taskInstances;

	private Integer[] instance;

	private List<TaskBuildCommand> taskBuildCommands;

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

	public String getProviderKind() {
		return providerKind;
	}

	public void setProviderKind(String providerKind) {
		this.providerKind = providerKind;
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

	public List<TaskInstance> getTaskInstances() {
		return taskInstances;
	}

	public void setTaskInstances(List<TaskInstance> taskInstances) {
		this.taskInstances = taskInstances;
	}

	public Integer getContactGroupId() {
		return contactGroupId;
	}

	public void setContactGroupId(Integer contactGroupId) {
		this.contactGroupId = contactGroupId;
	}

	public String getBuildCommand() {
		return buildCommand;
	}

	public void setBuildCommand(String buildCommand) {
		this.buildCommand = buildCommand;
	}

	public List<TaskBuildCommand> getTaskBuildCommands() {
		return taskBuildCommands;
	}

	public void setTaskBuildCommands(List<TaskBuildCommand> taskBuildCommands) {
		this.taskBuildCommands = taskBuildCommands;
	}

	public Integer[] getInstance() {
		return instance;
	}

	public void setInstance(Integer[] instance) {
		this.instance = instance;
	}

	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType;
	}

	public String getPmPlatform() {
		return pmPlatform;
	}

	public Integer getPcmId() {
		return pcmId;
	}

	public void setPcmId(Integer pcmId) {
		this.pcmId = pcmId;
	}

	public void setPmPlatform(String pmPlatform) {
		this.pmPlatform = pmPlatform;
	}

	public String getParentAppHome() {
		return parentAppHome;
	}

	public void setParentAppHome(String parentAppHome) {
		this.parentAppHome = parentAppHome;
	}

	public String getAssetsPath() {
		return assetsPath;
	}

	public void setAssetsPath(String assetsPath) {
		this.assetsPath = assetsPath;
	}

	@Override
	public String toString() {
		return "Task{" + "taskName='" + taskName + '\'' + ", appClusterId=" + appClusterId + ", projectId=" + projectId
				+ ", branchName='" + branchName + '\'' + ", providerKind=" + providerKind + ", branchType='" + branchType + '\''
				+ ", preCommand='" + preCommand + '\'' + ", postCommand='" + postCommand + '\'' + ", instances=" + instances
				+ ", taskInstances=" + taskInstances + ", groupName='" + groupName + '\'' + '}';
	}
}