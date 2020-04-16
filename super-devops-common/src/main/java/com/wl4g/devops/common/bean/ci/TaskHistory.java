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

import java.io.Serializable;

public class TaskHistory extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer type;

	private Integer projectId;

	private Integer status;

	private String branchName;

	private String shaGit;

	private String shaLocal;

	private Integer refId;

	private String buildCommand;

	private String preCommand;

	private String postCommand;

	private String providerKind;

	private String branchType;

	private String result;

	private String projectName;

	private String groupName;

	private Integer contactGroupId;

	private String trackId;

	private Integer trackType;

	private Long costTime;

	private String envType;

	private String createByName;

	private String annex;

	private String parentAppHome;

	/** 构建的文件/目录路径（maven项目的target目录，vue项目的dist目录） */
	private String assetsPath;

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

	public String getShaGit() {
		return shaGit;
	}

	public void setShaGit(String shaGit) {
		this.shaGit = shaGit;
	}

	public Integer getRefId() {
		return refId;
	}

	public void setRefId(Integer refId) {
		this.refId = refId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getPreCommand() {
		return preCommand;
	}

	public void setPreCommand(String preCommand) {
		this.preCommand = preCommand;
	}

	public String getPostCommand() {
		return postCommand;
	}

	public void setPostCommand(String postCommand) {
		this.postCommand = postCommand;
	}

	public String getProviderKind() {
		return providerKind;
	}

	public void setProviderKind(String providerKind) {
		this.providerKind = providerKind;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getShaLocal() {
		return shaLocal;
	}

	public void setShaLocal(String shaLocal) {
		this.shaLocal = shaLocal;
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

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public Integer getTrackType() {
		return trackType;
	}

	public void setTrackType(Integer trackType) {
		this.trackType = trackType;
	}

	public Long getCostTime() {
		return costTime;
	}

	public void setCostTime(Long costTime) {
		this.costTime = costTime;
	}

	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType;
	}

	public String getCreateByName() {
		return createByName;
	}

	public void setCreateByName(String createByName) {
		this.createByName = createByName;
	}

	public String getAnnex() {
		return annex;
	}

	public void setAnnex(String annex) {
		this.annex = annex;
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

	public String getBranchType() {
		return branchType;
	}

	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}
}