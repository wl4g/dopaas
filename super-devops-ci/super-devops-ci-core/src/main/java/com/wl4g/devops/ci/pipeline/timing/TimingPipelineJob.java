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
import com.wl4g.devops.ci.core.Pipeline;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.bean.ci.TaskDetail;
import com.wl4g.devops.common.bean.ci.Trigger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Timing pipeline handler.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-07-19 10:41:00
 */
public class TimingPipelineJob implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected Pipeline pipelineProcessor;
	@Autowired
	protected TriggerService triggerService;

	final protected Task task;
	final protected Trigger trigger;
	final protected Project project;
	final protected List<TaskDetail> taskDetails;

	public TimingPipelineJob(Trigger trigger, Project project, Task task, List<TaskDetail> taskDetails) {
		this.trigger = trigger;
		this.project = project;
		this.task = task;
		this.taskDetails = taskDetails;
	}

	@Override
	public void run() {
		log.info("Timing tasks start");
		if (check()) {// check
			log.info("Code had modify , create build task now triggetId={} ", trigger.getId());
			List<String> instancesStr = new ArrayList<>();
			for (TaskDetail taskDetail : taskDetails) {
				instancesStr.add(String.valueOf(taskDetail.getInstanceId()));
			}
			pipelineProcessor.startup(task.getId());

			// set new sha in db
			String path = config.getWorkspace() + "/" + project.getProjectName();
			try {
				String newSha = GitUtils.getLatestCommitted(path);
				if (StringUtils.isNotBlank(newSha)) {
					triggerService.updateSha(trigger.getId(), newSha);
					trigger.setSha(newSha);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			log.info("Cron Create TaskHistory triggerId={} projectId={} projectName={} time={}", trigger.getId(), project.getId(),
					project.getProjectName(), new Date());
		}
	}

	/**
	 * check need or not build -- 当本地git仓库的sha和服务器上的不一致时(有代码提交)，则需要更新
	 */
	private boolean check() {
		String sha = trigger.getSha();
		String path = config.getWorkspace() + "/" + project.getProjectName();
		try {
			if (GitUtils.checkGitPath(path)) {
				GitUtils.checkout(config.getVcs().getGitlab().getCredentials(), path, task.getBranchName());
			} else {
				GitUtils.clone(config.getVcs().getGitlab().getCredentials(), project.getGitUrl(), path, task.getBranchName());
			}
			String oldestSha = GitUtils.getLatestCommitted(path);
			return !StringUtils.equals(sha, oldestSha);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}