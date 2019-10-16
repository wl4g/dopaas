
/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

	private Integer tarType;

	private String result;

	private String projectName;

	private String groupName;

	private Integer contactGroupId;

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

	public Integer getTarType() {
		return tarType;
	}

	public void setTarType(Integer tarType) {
		this.tarType = tarType;
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
}