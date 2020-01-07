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
package com.wl4g.devops.ci.core.context;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Default deploy information implements.
 *
 * @author Wangl.sir
 * @version v1.0.0 2019-10-07
 * @since
 */
public class DefaultPipelineContext implements PipelineContext {

	final private Project project;
	final private String projectSourceDir;
	final private AppCluster appCluster;
	final private List<AppInstance> instances;
	final private TaskHistory taskHistory;
	final private TaskHistory refTaskHistory;
	final private List<TaskHistoryInstance> taskHistoryInstances;

	public DefaultPipelineContext(Project project, String projectSourceDir, AppCluster appCluster, List<AppInstance> instances,
			TaskHistory taskHistory, TaskHistory refTaskHistory, List<TaskHistoryInstance> taskHistoryInstances) {
		notNull(project, "project must not be null");
		hasText(projectSourceDir, "projectSourceDir must not be empty");
		notNull(appCluster, "AppCluster must not be empty");
		notNull(taskHistory, "taskHistory must not be null");
		// notNull(refTaskHistory, "refTaskHistory must not be null");
		this.project = project;
		this.projectSourceDir = projectSourceDir;
		this.appCluster = appCluster;
		this.taskHistory = taskHistory;
		this.refTaskHistory = refTaskHistory;
		this.instances = !isEmpty(instances) ? instances : emptyList();
		this.taskHistoryInstances = !isEmpty(taskHistoryInstances) ? taskHistoryInstances : emptyList();
	}

	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public String getProjectSourceDir() {
		return projectSourceDir;
	}

	@Override
	public AppCluster getAppCluster() {
		return appCluster;
	}

	@Override
	public List<AppInstance> getInstances() {
		return instances;
	}

	@Override
	public TaskHistory getTaskHistory() {
		return taskHistory;
	}

	@Override
	public TaskHistory getRefTaskHistory() {
		return refTaskHistory;
	}

	@Override
	public List<TaskHistoryInstance> getTaskHistoryInstances() {
		return taskHistoryInstances;
	}

}