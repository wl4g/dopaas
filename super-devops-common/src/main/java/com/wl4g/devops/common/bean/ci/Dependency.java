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
import java.util.Objects;

public class Dependency extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Integer id;

	private Integer projectId;

	private Integer dependentId;

	private String branch;

	private String projectName;

	private String parentName;

	public Dependency() {

	}

	public Dependency(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getDependentId() {
		return dependentId;
	}

	public void setDependentId(Integer dependentId) {
		this.dependentId = dependentId;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Dependency that = (Dependency) o;
		return Objects.equals(dependentId, that.dependentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dependentId);
	}

	@Override
	public String toString() {
		return "Dependency{" + "id=" + id + ", projectId=" + projectId + ", dependentId=" + dependentId + ", branch='" + branch
				+ '\'' + ", projectName='" + projectName + '\'' + ", parentName='" + parentName + '\'' + '}';
	}
}