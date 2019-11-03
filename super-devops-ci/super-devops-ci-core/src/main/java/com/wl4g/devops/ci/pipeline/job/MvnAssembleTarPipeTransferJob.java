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
package com.wl4g.devops.ci.pipeline.job;

import com.wl4g.devops.ci.pipeline.MvnAssembleTarPipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

import static com.wl4g.devops.ci.utils.LogHolder.cleanupDefault;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

/**
 * MAVEN assemble tar deployments task.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class MvnAssembleTarPipeTransferJob extends BasedMavenPipeTransferJob<MvnAssembleTarPipelineProvider> {

	final protected String path;
	final protected String tarPath;

	public MvnAssembleTarPipeTransferJob(MvnAssembleTarPipelineProvider provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails, String tarPath, String path) {
		super(provider, project, instance, taskHistoryDetails);
		hasText(path, "path must not be empty.");
		hasText(tarPath, "tarPath must not be empty.");
		this.path = path;
		this.tarPath = tarPath;
	}

	@Override
	public void run() {
		notNull(taskDetailId, "taskDetailId can not be null");
		log.info("Deploy task is starting for instance:{}, projectId:{}, projectName:{} ...", instance.getId(), project.getId(),
				project.getProjectName());

		try {
			TaskHistory taskHisy = provider.getPipelineInfo().getTaskHistory();
			// Update status to running.
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_RUNNING, null);
			log.info("Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
					TASK_STATUS_RUNNING, taskDetailId, instance.getId(), project.getId(), project.getProjectName());

			// Call PRE commands.
			provider.doRemoteCommand(instance.getHostname(), instance.getSshUser(), taskHisy.getPreCommand(),
					instance.getSshKey());

			// Distributed deploying to remote.
			doRemoteDeploying(path + tarPath, instance.getHostname(), instance.getSshUser(), project.getParentAppHome(),
					instance.getSshKey());

			// Call post remote commands (e.g. restart)
			provider.doRemoteCommand(instance.getHostname(), instance.getSshUser(), taskHisy.getPostCommand(),
					instance.getSshKey());

			// Update status to success.
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_SUCCESS, getLogMessage(null));
			log.error("Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
					TASK_STATUS_SUCCESS, taskDetailId, instance.getId(), project.getId(), project.getProjectName());

		} catch (Exception ex) {
			log.error("Failed to transfer job", ex);

			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_FAIL, getLogMessage(ex));
			log.error("Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
					TASK_STATUS_FAIL, taskDetailId, instance.getId(), project.getId(), project.getProjectName());
		} finally {
			cleanupDefault(); // Help GC
		}

		log.info("Mvn transfer task of finished!");
	}

}