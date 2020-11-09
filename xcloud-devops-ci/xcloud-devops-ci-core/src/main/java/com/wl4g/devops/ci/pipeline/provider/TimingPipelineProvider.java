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
package com.wl4g.devops.ci.pipeline.provider;

import com.wl4g.components.core.bean.ci.Project;
import com.wl4g.components.core.bean.ci.Task;
import com.wl4g.components.core.bean.ci.TaskInstance;
import com.wl4g.components.core.bean.ci.Trigger;
import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.core.param.RunParameter;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.vcs.operator.VcsOperator;
import com.wl4g.devops.vcs.operator.VcsOperator.VcsAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wl4g.components.common.lang.Assert2.hasText;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Timing pipeline jobs provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-08-19 10:41:00
 */
public class TimingPipelineProvider extends AbstractPipelineProvider implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected PipelineManager pipeManager;
	@Autowired
	protected TriggerService triggerService;

	protected final Task task;
	protected final Project project;
	protected final List<TaskInstance> taskInstances;
	protected Trigger trigger;

	public TimingPipelineProvider(Trigger trigger, Project project, Task task, List<TaskInstance> taskInstances) {
		super(PipelineContext.EMPTY);
		this.trigger = trigger;
		this.project = project;
		this.task = task;
		this.taskInstances = taskInstances;
	}

	@Override
	public void run() {
		log.info("Timing pipeline... project:{}, task:{}, trigger:{}", project, task, trigger);
		trigger = triggerService.getById(trigger.getId());

		// Gets VCS operator.
		VcsOperator operator = vcsManager.forOperator(project.getVcs().getProviderKind());
		try {
			if (!checkCommittedChanged(operator)) { // Changed?
				log.info("Skip timing tasks pipeline, because commit unchanged, with project:{}, task:{}, trigger:{}", project,
						task, trigger);
				return;
			}

			// Creating pipeline task.
			// TODO traceId???
			PipelineModel pipeModel = flowManager.buildPipeline(task.getId());
			pipeManager.runPipeline(new RunParameter(task.getId(), "rollback", "1", "1", null), pipeModel);

			// set new sha in db
			String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
			String latestSha = operator.getLatestCommitted(projectDir);
			hasText(latestSha, "Trigger latest sha can't be empty for %s", projectDir);

			// Update latest sign.
			triggerService.updateSha(trigger.getId(), latestSha);
			trigger.setSha(latestSha);

			log.info("Timing pipeline tasks executed successful, with triggerId: {}, projectId: {}, projectName: {} ",
					trigger.getId(), project.getId(), project.getProjectName());
		} catch (Exception e) {
			log.error("", e);
		}

	}

	/**
	 * Check for updates. </br>
	 * When the Sha of the VCS local warehouse is different from the latest Sha
	 * on the server, it indicates that there is code update
	 * 
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	private boolean checkCommittedChanged(VcsOperator operator) throws Exception {
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		if (operator.hasLocalRepository(projectDir)) {
			operator.checkoutAndPull(project.getVcs(), projectDir, task.getBranchName(), VcsAction.safeOf(task.getBranchType()));
		} else {
			operator.clone(project.getVcs(), project.getHttpUrl(), projectDir, task.getBranchName());
		}
		String newSign = operator.getLatestCommitted(projectDir);
		return !equalsIgnoreCase(trigger.getSha(), newSign);
	}

}