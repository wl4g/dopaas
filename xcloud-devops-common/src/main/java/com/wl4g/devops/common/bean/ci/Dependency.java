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
package com.wl4g.devops.common.bean.ci;

import java.io.Serializable;
import java.util.Objects;

import com.wl4g.component.core.bean.BaseBean;

public class Dependency extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private Long projectId;

	private Long dependentId;

	private String projectName;

	private String parentName;

	public Dependency() {

	}

	public Dependency(Long projectId) {
		this.projectId = projectId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getDependentId() {
		return dependentId;
	}

	public void setDependentId(Long dependentId) {
		this.dependentId = dependentId;
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

}