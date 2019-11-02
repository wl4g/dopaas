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
package com.wl4g.devops.ci.pipeline.model;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import static java.util.Collections.emptyList;

import java.util.List;

/**
 * Default deploy information implements.
 *
 * @author Wangl.sir
 * @version v1.0.0 2019-10-07
 * @since
 */
public class DefaultPipelineInfo implements PipelineInfo {

	private Project project;
	private String tarType;
	private String path;
	private String branch;
	private String alias;
	private String tarName;
	private List<AppInstance> instances = emptyList();
	private TaskHistory taskHistory;
	private TaskHistory refTaskHistory;
	private List<TaskHistoryDetail> taskHistoryDetails = emptyList();

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getTarType() {
		return tarType;
	}

	public void setTarType(String tarType) {
		this.tarType = tarType;
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

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public TaskHistory getTaskHistory() {
		return taskHistory;
	}

	public void setTaskHistory(TaskHistory taskHistory) {
		this.taskHistory = taskHistory;
	}

	public TaskHistory getRefTaskHistory() {
		return refTaskHistory;
	}

	public void setRefTaskHistory(TaskHistory refTaskHistory) {
		this.refTaskHistory = refTaskHistory;
	}

	public List<TaskHistoryDetail> getTaskHistoryDetails() {
		return taskHistoryDetails;
	}

	public void setTaskHistoryDetails(List<TaskHistoryDetail> taskHistoryDetails) {
		this.taskHistoryDetails = taskHistoryDetails;
	}
}