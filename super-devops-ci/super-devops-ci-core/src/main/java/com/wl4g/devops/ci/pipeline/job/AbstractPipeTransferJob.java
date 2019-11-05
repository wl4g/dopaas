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
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.support.cli.ProcessManager;

import static com.wl4g.devops.ci.utils.LogHolder.cleanupDefault;
import static com.wl4g.devops.ci.utils.LogHolder.getDefault;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_FAIL;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_RUNNING;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_SUCCESS;
import static com.wl4g.devops.common.utils.Exceptions.getStackTraceAsString;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract deploying transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月23日
 * @since
 * @param <P>
 */
public abstract class AbstractPipeTransferJob<P extends PipelineProvider> implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Pipeline CICD properties configuration. */
	@Autowired
	protected CiCdProperties config;

	/** Command-line process manager. */
	@Autowired
	protected ProcessManager processManager;

	/** Pipeline provider. */
	final protected P provider;

	/** Pipeline deploy project. */
	final protected Project project;

	/** Pipeline deploy instance. */
	final protected AppInstance instance;

	/** Pipeline taskDetailId. */
	final protected Integer taskDetailId;

	public AbstractPipeTransferJob(P provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		notNull(provider, "Pipeline provider must not be null.");
		notNull(project, "Pipeline job project must not be null.");
		notNull(instance, "Pipeline job instance must not be null.");
		notEmpty(taskHistoryDetails, "Pipeline task historyDetails must not be null.");
		this.provider = provider;
		this.project = project;
		this.instance = instance;

		// Task details.
		Optional<TaskHistoryDetail> taskHisyDetail = taskHistoryDetails.stream()
				.filter(detail -> detail.getInstanceId().intValue() == instance.getId().intValue()).findFirst();
		isTrue(taskHisyDetail.isPresent(), "Not found taskDetailId by details.");
		this.taskDetailId = taskHisyDetail.get().getId();
	}

	@Override
	public void run() {
		notNull(taskDetailId, "Transfer job for taskDetailId inmust not be null");
		log.info("Starting transfer job for instanceId:{}, projectId:{}, projectName:{} ...", instance.getId(), project.getId(),
				project.getProjectName());

		try {
			TaskHistory taskHisy = provider.getPipelineInfo().getTaskHistory();
			// Update status to running.
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_RUNNING, null);
			log.info("[PRE]Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
					TASK_STATUS_RUNNING, taskDetailId, instance.getId(), project.getId(), project.getProjectName());

			// Call PRE commands.
			provider.doRemoteCommand(instance.getHostname(), instance.getSshUser(), taskHisy.getPreCommand(),
					instance.getSshKey());

			// Distributed deploying to remote.
			doRemoteDeploying(instance.getHostname(), instance.getSshUser(), instance.getSshKey());

			// Call post remote commands (e.g. restart)
			provider.doRemoteCommand(instance.getHostname(), instance.getSshUser(), taskHisy.getPostCommand(),
					instance.getSshKey());

			// Update status to success.
			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_SUCCESS, getLogMessage(null));
			log.info("[SUCCESS]Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
					TASK_STATUS_SUCCESS, taskDetailId, instance.getId(), project.getId(), project.getProjectName());

		} catch (Exception ex) {
			log.error("Failed to transfer job", ex);

			provider.getTaskHistoryService().updateDetailStatusAndResult(taskDetailId, TASK_STATUS_FAIL, getLogMessage(ex));
			log.error("[FAILED]Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
					TASK_STATUS_FAIL, taskDetailId, instance.getId(), project.getId(), project.getProjectName());
		} finally {
			cleanupDefault(); // Help GC
		}

		log.info("Completed of transfer job for instanceId:{}, projectId:{}, projectName:{}", instance.getId(), project.getId(),
				project.getProjectName());
	}

	/**
	 * Deploying executable to remote host instances.</br>
	 * e.g. SCP & Uncompress & cleanup.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected abstract void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception;

	/**
	 * Obtain log message text.
	 * 
	 * @param ex
	 * @return
	 */
	protected String getLogMessage(Exception ex) {
		StringBuffer message = getDefault().getMessage();
		if (nonNull(ex)) {
			message.append("\nat cause:\n");
			message.append(getStackTraceAsString(ex));
		}
		return message.toString();
	}

}