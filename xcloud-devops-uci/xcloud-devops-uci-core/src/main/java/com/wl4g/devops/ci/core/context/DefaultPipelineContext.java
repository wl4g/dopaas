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
package com.wl4g.devops.ci.core.context;

import com.wl4g.devops.common.bean.ci.PipeStageBuilding;
import com.wl4g.devops.common.bean.ci.PipeStageInstanceCommand;
import com.wl4g.devops.common.bean.ci.PipeStageNotification;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.model.ActionControl;
import com.wl4g.devops.common.bean.ci.model.PipelineModel;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppEnvironment;
import com.wl4g.devops.common.bean.erm.AppInstance;

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
	final private Pipeline pipeline;
	final private PipelineHistory pipelineHistory;
	final private List<PipelineHistoryInstance> pipelineHistoryInstances;
	final private PipelineModel pipelineModel;
	final private PipeStageInstanceCommand pipeStepInstanceCommand;
	final private PipeStageNotification pipeStepNotification;
	final private PipeStageBuilding pipeStepBuilding;
	final private AppEnvironment environment;
	final private ActionControl actionControl;

	public DefaultPipelineContext(Project project, String projectSourceDir, AppCluster appCluster, List<AppInstance> instances,
			PipelineHistory pipelineHistory, List<PipelineHistoryInstance> pipelineHistoryInstances, PipelineModel pipelineModel,
			PipeStageInstanceCommand pipeStepInstanceCommand, Pipeline pipeline, PipeStageNotification pipeStepNotification,
			PipeStageBuilding pipeStepBuilding, AppEnvironment environment, ActionControl actionControl) {
		notNull(project, "project must not be null");
		hasText(projectSourceDir, "projectSourceDir must not be empty");
		notNull(appCluster, "AppCluster must not be empty");
		notNull(pipelineHistory, "taskHistory must not be null");
		// notNull(refTaskHistory, "refTaskHistory must not be null");
		this.project = project;
		this.projectSourceDir = projectSourceDir;
		this.appCluster = appCluster;
		this.pipelineHistory = pipelineHistory;
		this.instances = !isEmpty(instances) ? instances : emptyList();
		this.pipelineHistoryInstances = !isEmpty(pipelineHistoryInstances) ? pipelineHistoryInstances : emptyList();
		this.pipelineModel = pipelineModel;
		this.pipeStepInstanceCommand = pipeStepInstanceCommand;
		this.pipeline = pipeline;
		this.pipeStepNotification = pipeStepNotification;
		this.pipeStepBuilding = pipeStepBuilding;
		this.environment = environment;
		this.actionControl = actionControl;
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

	public PipelineHistory getPipelineHistory() {
		return pipelineHistory;
	}

	public List<PipelineHistoryInstance> getPipelineHistoryInstances() {
		return pipelineHistoryInstances;
	}

	@Override
	public PipelineModel getPipelineModel() {
		return pipelineModel;
	}

	@Override
	public PipeStageInstanceCommand getPipeStepInstanceCommand() {
		return pipeStepInstanceCommand;
	}

	@Override
	public Pipeline getPipeline() {
		return pipeline;
	}

	@Override
	public PipeStageNotification getPipeStepNotification() {
		return pipeStepNotification;
	}

	@Override
	public PipeStageBuilding getPipeStepBuilding() {
		return pipeStepBuilding;
	}

	@Override
	public AppEnvironment getEnvironment() {
		return environment;
	}

	public ActionControl getActionControl() {
		return actionControl;
	}
}