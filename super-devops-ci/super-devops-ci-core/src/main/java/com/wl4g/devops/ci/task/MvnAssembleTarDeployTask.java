/*
 * Copyright 2015 the original author or authors.
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

import com.wl4g.devops.ci.provider.MvnAssembleTarDeployProvider;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.scm.AppInstance;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Maven assemble tar deployments task.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class MvnAssembleTarDeployTask extends AbstractDeployTask {
	private Logger log = LoggerFactory.getLogger(getClass());

	private MvnAssembleTarDeployProvider provider;
	private String path;
	private String tarPath;
	private Integer taskDetailId;
	private String alias;

	public MvnAssembleTarDeployTask(MvnAssembleTarDeployProvider provider, String path, AppInstance instance, String tarPath,
			List<TaskDetail> taskDetails, String alias) {
		super(instance);

		this.provider = provider;
		this.path = path;
		this.tarPath = tarPath;
		this.alias = alias;
		Assert.notNull(taskDetails, "taskDetails can not be null");
		for (TaskDetail taskDetail : taskDetails) {
			if (taskDetail.getInstanceId().intValue() == instance.getId().intValue()) {
				this.taskDetailId = taskDetail.getId();
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
			taskService.updateTaskDetailStatus(taskDetailId, TASK_STATUS_RUNNING);

			// scp to tmp,rename,move to webapps
			provider.scpAndTar(path + tarPath, instance.getHost(), instance.getServerAccount(),
					instance.getBasePath() + "/" + alias + "-package", instance.getSshRsa());

			// change link
			provider.relink(instance.getHost(), instance.getBasePath() + "/" + alias + "-package", instance.getServerAccount(),
					path + tarPath, instance.getSshRsa());

			// restart
			provider.restart(instance.getHost(), instance.getServerAccount(), instance.getSshRsa());

			// update status
			taskService.updateTaskDetailStatus(taskDetailId, TASK_STATUS_SUCCESS);

		} catch (Exception e) {
			log.error("scp thread error");
			taskService.updateTaskDetailStatus(taskDetailId, TASK_STATUS_FAIL);
			throw new RuntimeException(e);
		}

		if (log.isInfoEnabled()) {
			log.info("Deploy task is finished!");
		}
	}

}