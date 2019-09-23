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
package com.wl4g.devops.ci.task;

import com.wl4g.devops.ci.provider.DockerBuildDeployProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;

/**
 * Maven assemble tar deployments task.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class DockerBuildDeployTask extends AbstractDeployTask {

	private DockerBuildDeployProvider provider;
	private Integer taskDetailId;

	public DockerBuildDeployTask(DockerBuildDeployProvider provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		super(instance, project);
		this.provider = provider;
		Assert.notNull(taskHistoryDetails, "taskHistoryDetails can not be null");
		for (TaskHistoryDetail taskHistoryDetail : taskHistoryDetails) {
			if (taskHistoryDetail.getInstanceId().intValue() == instance.getId().intValue()) {
				this.taskDetailId = taskHistoryDetail.getId();
				break;
			}
		}
	}

	@Override
	public void run() {
		if (log.isInfoEnabled()) {
			log.info("Deploy task is starting ...");
		}

		Assert.notNull(taskDetailId, "taskDetailId can not be null");
		try {
			// Update status
			taskHistoryService.updateDetailStatusAndResult(taskDetailId, TASK_STATUS_RUNNING, null);

			// Pull
			String s = provider.dockerPull(instance.getHostname(), instance.getSshUser(), "wl4g/" + project.getGroupName()
					+ ":master"/*
								 * TODO 要改成动态的
								 * provider.getTaskHistory().getPreCommand()
								 */, instance.getSshKey());
			result.append(s).append("\n");
			// Restart
			String s1 = provider.dockerStop(instance.getHostname(), instance.getSshUser(), project.getGroupName(),
					instance.getSshKey());
			result.append(s1).append("\n");
			// Remove Container
			String s2 = provider.dockerRemoveContainer(instance.getHostname(), instance.getSshUser(), project.getGroupName(),
					instance.getSshKey());
			result.append(s2).append("\n");
			// Run
			String s3 = provider.dockerRun(instance.getHostname(), instance.getSshUser(), "docker run wl4g/"
					+ project.getGroupName()
					+ ":master"/*
								 * TODO 要改成动态的
								 * provider.getTaskHistory().getPostCommand()
								 */, instance.getSshKey());
			result.append(s3).append("\n");

			// Update status
			taskHistoryService.updateDetailStatusAndResult(taskDetailId, TASK_STATUS_SUCCESS, result.toString());

		} catch (Exception e) {
			log.error("Deploy job failed", e);
			taskHistoryService.updateDetailStatusAndResult(taskDetailId, TASK_STATUS_FAIL,
					result.toString() + "\n" + e.toString());
			// throw new RuntimeException(e);
		}

		if (log.isInfoEnabled()) {
			log.info("Deploy task is finished!");
		}
	}

}