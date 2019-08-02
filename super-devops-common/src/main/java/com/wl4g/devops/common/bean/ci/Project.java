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
import java.util.List;

/**
 * Project bean entity.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月29日
 * @since
 */
public class Project extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String projectName;

	private String gitUrl;

	private Integer appClusterId;

	private String tarPath;

	private String parentAppHome;

	private String linkAppHome;

	private String groupName;

	private Integer lockStatus;

	private List<Dependency> dependencies;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Integer getAppClusterId() {
		return appClusterId;
	}

	public void setAppClusterId(Integer appClusterId) {
		this.appClusterId = appClusterId;
	}

	public String getTarPath() {
		return tarPath;
	}

	public void setTarPath(String tarPath) {
		this.tarPath = tarPath;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getParentAppHome() {
		return parentAppHome;
	}

	public void setParentAppHome(String parentAppHome) {
		this.parentAppHome = parentAppHome;
	}

	public String getLinkAppHome() {
		return linkAppHome;
	}

	public void setLinkAppHome(String linkAppHome) {
		this.linkAppHome = linkAppHome;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(Integer lockStatus) {
		this.lockStatus = lockStatus;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public String toString() {
		return "Project{" +
				"projectName='" + projectName + '\'' +
				", gitUrl='" + gitUrl + '\'' +
				", appClusterId=" + appClusterId +
				", tarPath='" + tarPath + '\'' +
				", parentAppHome='" + parentAppHome + '\'' +
				", linkAppHome='" + linkAppHome + '\'' +
				", groupName='" + groupName + '\'' +
				", lockStatus=" + lockStatus +
				", dependencies=" + dependencies +
				'}';
	}
}