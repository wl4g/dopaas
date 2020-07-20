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
import java.util.List;

import static java.util.Objects.nonNull;

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

	private Integer vcsId;

	private String httpUrl;

	private String sshUrl;

	private Integer appClusterId;

	/** 构建的文件/目录路径（maven项目的target目录，vue项目的dist目录） */
	// private String assetsPath;

	// private String parentAppHome;

	private String groupName;

	private Integer lockStatus;

	private Integer isBoot;

	private List<Dependency> dependencies;

	/**
	 * Project mapping to Vcs credentials.</br>
	 * Many(Project)-to-One(Vcs)
	 */
	private Vcs vcs = new Vcs();

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

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
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

	public Vcs getVcs() {
		return vcs;
	}

	public Integer getVcsId() {
		return vcsId;
	}

	public void setVcsId(Integer vcsId) {
		this.vcsId = vcsId;
	}

	public String getSshUrl() {
		return sshUrl;
	}

	public void setSshUrl(String sshUrl) {
		this.sshUrl = sshUrl;
	}

	public void setVcs(Vcs vcs) {
		if (nonNull(vcs)) {
			this.vcs = vcs;
		}
	}

	public Integer getIsBoot() {
		return isBoot;
	}

	public void setIsBoot(Integer isBoot) {
		this.isBoot = isBoot;
	}

	@Override
	public String toString() {
		return "Project{" + "projectName='" + projectName + '\'' + ", vcsId=" + vcsId + ", httpUrl='" + httpUrl + '\''
				+ ", sshUrl='" + sshUrl + '\'' + ", appClusterId=" + appClusterId + ", groupName='" + groupName + '\''
				+ ", lockStatus=" + lockStatus + ", isBoot=" + isBoot + ", dependencies=" + dependencies + ", vcs=" + vcs + '}';
	}
}