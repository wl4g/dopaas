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
package com.wl4g.devops.ci.pipeline.handler;

import com.wl4g.devops.ci.pipeline.MvnAssembleTarPipelineProvider;
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
public class MvnAssembleTarPipelineHandler extends AbstractPipelineHandler {

	private MvnAssembleTarPipelineProvider provider;
	private String path;
	private String tarPath;
	private Integer taskDetailId;

	public MvnAssembleTarPipelineHandler(MvnAssembleTarPipelineProvider provider, Project project, String path,
			AppInstance instance, String tarPath, List<TaskHistoryDetail> taskHistoryDetails) {
		super(instance, project);
		this.provider = provider;
		this.path = path;
		this.tarPath = tarPath;
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
		StringBuffer result = new StringBuffer();
		try {
			// Update status
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_RUNNING, null);

			// pre command
			String s4 = provider.exceCommand(instance.getHostname(), instance.getSshUser(),
					provider.getPipelineInfo().getTaskHistory().getPreCommand(), instance.getSshKey());
			result.append(s4).append("\n");

			// Boolean detailSuccess = new Boolean(false);
			// Scp to tmp,rename,move to webapps
			String s = provider.scpAndTar(path + tarPath, instance.getHostname(), instance.getSshUser(),
					project.getParentAppHome(), instance.getSshKey());
			result.append(s).append("\n");

			// Change link
			/*
			 * String s1 = provider.relink(instance.getHostname(),
			 * project.getParentAppHome(), instance.getSshUser(), path +
			 * tarPath, instance.getSshKey()); result.append(s1).append("\n");
			 */

			// post command (restart command)
			String s2 = provider.exceCommand(instance.getHostname(), instance.getSshUser(),
					provider.getPipelineInfo().getTaskHistory().getPostCommand(), instance.getSshKey());
			result.append(s2).append("\n");

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