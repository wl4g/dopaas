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
package com.wl4g.devops.ci.bean;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @author vjay
 * @date 2020-03-06 17:17:00
 */
public class GraphTask implements Serializable {
	private static final long serialVersionUID = 8940373806493080114L;

	// private String runId;//RUN-{flowId|pipeId}-{timestamp}
	private String graphTaskId;

	private Long createTime;// timestamp

	private Set<Project> projects;

	public static class Project {

		private Integer projectId;

		private String status;// WAITING|RUNNING|FAILED|SUCCESS

		private String ref;// branch|tag

		private Long startTime;

		private Long endTime;

		private Set<Project> childrenTree;

		private Set<Project> projectSet;

		// TODO ...

		public Integer getProjectId() {
			return projectId;
		}

		public void setProjectId(Integer projectId) {
			this.projectId = projectId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getRef() {
			return ref;
		}

		public void setRef(String ref) {
			this.ref = ref;
		}

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public Set<Project> getChildrenTree() {
			return childrenTree;
		}

		public void setChildrenTree(Set<Project> childrenTree) {
			this.childrenTree = childrenTree;
		}

		public Set<Project> getProjectSet() {
			return projectSet;
		}

		public void setProjectSet(Set<Project> projectSet) {
			this.projectSet = projectSet;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			Project project = (Project) o;
			return Objects.equals(projectId, project.projectId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(projectId);
		}

		public boolean isRefDifferent(Project newProject) {
			// 检查是否有project一样,但分支|tag不一样的
			for (Project project : projectSet) {
				if (project.getProjectId().equals(newProject.getProjectId())
						&& !StringUtils.equals(project.getRef(), newProject.getRef())) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isRefDifferent(Project newProject) {
		boolean allDifferent = true;
		for (Project project : projects) {
			if (!project.isRefDifferent(newProject)) {
				allDifferent = false;
				break;
			}
		}
		return allDifferent;
	}

	public void addProject(Project newProject) {

		for (Project project : projects) {
			if (project.equals(newProject)) {

				// TODO
				return;

			}
		}

	}

	public Project mergeTree(Project newProject) {
		// Step1 找新树top在旧树的哪个节点，找到直接返回旧节点，找不到则创建当前节点，并把children也逐个去匹配旧树
		Project match = match(newProject);
		if (Objects.isNull(match)) {
			projects.add(newProject);
			List<Project> needRemove = new ArrayList<>();
			List<Project> needAdd = new ArrayList<>();
			for (Project child : newProject.getChildrenTree()) {
				Project matchChild = match(child);
				if (Objects.nonNull(matchChild)) {
					needRemove.add(child);
					needAdd.add(matchChild);
				} else {

				}
			}
			newProject.getChildrenTree().removeAll(needRemove);
			newProject.getChildrenTree().addAll(needAdd);
			return match;
		} else {
			return newProject;
		}
	}

	/**
	 * 找新树top在旧树的哪个节点
	 *
	 * @param newProject
	 * @return
	 */
	public Project match(Project newProject) {
		for (Project project : projects) {
			if (project.equals(newProject)) {
				return project;
			}
		}
		return null;
	}

	public static void main(String[] args) {

		Set<Project> projects = new HashSet<>();
		Project project1 = new Project();
		project1.setProjectId(1);
		project1.setRef("1");

		Project project2 = new Project();
		project2.setProjectId(1);
		project2.setRef("2");

		projects.add(project1);
		projects.add(project2);
		System.out.println(projects);
	}

}