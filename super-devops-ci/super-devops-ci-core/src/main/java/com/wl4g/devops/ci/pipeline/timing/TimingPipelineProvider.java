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
package com.wl4g.devops.ci.pipeline.timing;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.command.NewCommand;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.pipeline.AbstractPipelineProvider;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.dao.ci.TriggerDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.util.Assert.hasText;

import java.util.List;

/**
 * Timing pipeline handler.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-07-19 10:41:00
 */
public class TimingPipelineProvider extends AbstractPipelineProvider implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected PipelineManager pipeManager;
	@Autowired
	protected TriggerService triggerService;
	@Autowired
	protected TriggerDao triggerDao;

	final protected Task task;
	final protected Project project;
	final protected List<TaskDetail> taskDetails;
	protected Trigger trigger;

	public TimingPipelineProvider(Trigger trigger, Project project, Task task, List<TaskDetail> taskDetails) {
		super(PipelineContext.EMPTY);
		this.trigger = trigger;
		this.project = project;
		this.task = task;
		this.taskDetails = taskDetails;
	}

	@Override
	protected Runnable newDeployer(AppInstance instance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void run() {
		if (log.isInfoEnabled()) {
			log.info("Timing pipeline tasks ... project:{}, task:{}, trigger:{}", project, task, trigger);
		}

		// for update sha from db
		trigger = triggerDao.selectByPrimaryKey(trigger.getId());

		try {
			if (!checkCommittedChanged()) { // Changed?
				log.info("Skip timing tasks pipeline, because commit unchanged, with project:{}, task:{}, trigger:{}", project,
						task, trigger);
			}

			// Creating pipeline task.
			// TODO  traceId???
			pipeManager.newPipeline(new NewCommand(task.getId(), null, null, null));

			// set new sha in db
			String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
			String latestSha = vcsOperator.getLatestCommitted(projectDir);
			hasText(latestSha, String.format("Trigger latest sha can't be empty for %s", projectDir));

			// Update latest sign.
			triggerService.updateSha(trigger.getId(), latestSha);
			trigger.setSha(latestSha);

			if (log.isInfoEnabled()) {
				log.info("Timing pipeline tasks executed successful, with triggerId: {}, projectId:{}, projectName:{} ",
						trigger.getId(), project.getId(), project.getProjectName());
			}
		} catch (Exception e) {
			log.error("", e);
		}

	}

	/**
	 * Check for updates. </br>
	 * When the Sha of the VCS local warehouse is different from the latest Sha
	 * on the server, it indicates that there is code update
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean checkCommittedChanged() throws Exception {
		String projectDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		if (vcsOperator.ensureRepo(projectDir)) {
			vcsOperator.checkoutAndPull(config.getVcs().getGitlab().getCredentials(), projectDir, task.getBranchName());
		} else {
			vcsOperator.clone(config.getVcs().getGitlab().getCredentials(), project.getGitUrl(), projectDir,
					task.getBranchName());
		}
		String newSign = vcsOperator.getLatestCommitted(projectDir);
		return !equalsIgnoreCase(trigger.getSha(), newSign);
	}

}