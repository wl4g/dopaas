/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.common.bean.uci;

import java.util.List;

import com.wl4g.infra.core.bean.BaseBean;

public class PipeStageBuildingProject extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Long buildingId;

	private Long projectId;

	private String buildCommand;

	private String ref;

	private Integer sort;

	// ext
	private String projectName;

	List<String> branchs;

	public Long getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getBuildCommand() {
		return buildCommand;
	}

	public void setBuildCommand(String buildCommand) {
		this.buildCommand = buildCommand == null ? null : buildCommand.trim();
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref == null ? null : ref.trim();
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public List<String> getBranchs() {
		return branchs;
	}

	public void setBranchs(List<String> branchs) {
		this.branchs = branchs;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "PipeStepBuildingProject{" + "buildingId=" + buildingId + ", projectId=" + projectId + ", buildCommand='"
				+ buildCommand + '\'' + ", ref='" + ref + '\'' + ", projectName='" + projectName + '\'' + ", branchs=" + branchs
				+ '}';
	}
}