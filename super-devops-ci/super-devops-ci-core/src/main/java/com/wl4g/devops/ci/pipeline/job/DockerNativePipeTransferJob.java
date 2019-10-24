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

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.pipeline.DockerNativePipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;

/**
 * Docker native deployments task.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class DockerNativePipeTransferJob extends AbstractPipeTransferJob<DockerNativePipelineProvider> {

	public DockerNativePipeTransferJob(CiCdProperties config, DockerNativePipelineProvider provider, Project project,
			AppInstance instance, List<TaskHistoryDetail> taskHistoryDetails) {
		super(config, provider, project, instance, taskHistoryDetails);
	}

	@Override
	public void run() {
		if (log.isInfoEnabled()) {
			log.info("Deploy task is starting ...");
		}

		Assert.notNull(taskDetailId, "taskDetailId can not be null");
		StringBuffer result = new StringBuffer();
		try {
			// Update status
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_RUNNING, null);

			// Pull
			provider.dockerPull(instance.getHostname(), instance.getSshUser(), "wl4g/" + project.getGroupName()
					+ ":master"/*
								 * TODO 要改成动态的
								 * provider.getTaskHistory().getPreCommand()
								 */, instance.getSshKey());
			// Restart
			provider.dockerStop(instance.getHostname(), instance.getSshUser(), project.getGroupName(), instance.getSshKey());

			// Remove Container
			provider.dockerRemoveContainer(instance.getHostname(), instance.getSshUser(), project.getGroupName(),
					instance.getSshKey());
			// Run
			provider.dockerRun(instance.getHostname(), instance.getSshUser(), "docker run wl4g/" + project.getGroupName()
					+ ":master"/*
								 * TODO 要改成动态的
								 * provider.getTaskHistory().getPostCommand()
								 */, instance.getSshKey());
			// Update status
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_SUCCESS, result.toString());

		} catch (Exception e) {
			log.error("Deploy job failed", e);
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_FAIL,
					result.toString() + "\n" + e.toString());
			// throw new RuntimeException(e);
		}

		if (log.isInfoEnabled()) {
			log.info("Deploy task is finished!");
		}
	}

}