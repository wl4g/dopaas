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
package com.wl4g.devops.ci.pipeline.deploy;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.exception.ci.PipelineDeployingException;
import com.wl4g.devops.common.utils.cli.SSH2Utils.CommandResult;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.common.utils.io.FileIOUtils;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.common.utils.cli.SSH2Utils.executeWithCommand;
import static com.wl4g.devops.common.utils.io.FileIOUtils.writeFile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.*;

/**
 * Abstract deploying transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月23日
 * @since
 * @param <P>
 */
public abstract class AbstractPipeDeployer<P extends PipelineProvider> implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Pipeline CICD properties configuration. */
	@Autowired
	protected CiCdProperties config;

	/** Command-line process manager. */
	@Autowired
	protected DestroableProcessManager processManager;

	/** Task history service. */
	@Autowired
	protected TaskHistoryService taskHistoryService;

	/** Pipeline provider. */
	final protected P provider;

	/** Pipeline deploy instance. */
	final protected AppInstance instance;

	/** Pipeline taskDetailId. */
	final protected Integer taskDetailId;

	public AbstractPipeDeployer(P provider, AppInstance instance, List<TaskHistoryInstance> taskHistoryInstances) {
		notNull(provider, "Pipeline provider must not be null.");
		notNull(instance, "Pipeline job instance must not be null.");
		notEmpty(taskHistoryInstances, "Pipeline task historyDetails must not be null.");
		this.provider = provider;
		this.instance = instance;

		// Task details.
		Optional<TaskHistoryInstance> taskHisyDetail = taskHistoryInstances.stream()
				.filter(detail -> detail.getInstanceId().intValue() == instance.getId().intValue()).findFirst();
		isTrue(taskHisyDetail.isPresent(), "Not found taskDetailId by details.");
		this.taskDetailId = taskHisyDetail.get().getId();
	}

	@Override
	public void run() {
		notNull(taskDetailId, "Transfer job for taskDetailId inmust not be null");

		Integer projectId = getContext().getProject().getId();
		String projectName = getContext().getProject().getProjectName();
		if (log.isInfoEnabled()) {
			log.info("Starting transfer job for instanceId:{}, projectId:{}, projectName:{} ...", instance.getId(), projectId,
					projectName);
		}
		try {
			TaskHistory taskHisy = provider.getContext().getTaskHistory();
			// Update status to running.
			taskHistoryService.updateDetailStatusAndResult(taskDetailId, TASK_STATUS_RUNNING, null);
			if (log.isInfoEnabled()) {
				log.info("[PRE] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
						TASK_STATUS_RUNNING, taskDetailId, instance.getId(), projectId, projectName);
			}

			// PRE commands.
			if (!isBlank(taskHisy.getPreCommand())) {
				doRemoteCommand(instance.getHostname(), instance.getSshUser(), taskHisy.getPreCommand(), instance.getSshKey());
			}

			// Deploying distribute to remote.
			doRemoteDeploying(instance.getHostname(), instance.getSshUser(), instance.getSshKey());

			// Post remote commands.(e.g: restart)
			if (!isBlank(taskHisy.getPostCommand())) {
				doRemoteCommand(instance.getHostname(), instance.getSshUser(), taskHisy.getPostCommand(), instance.getSshKey());
			}

			// Update status to success.
			taskHistoryService.updateDetailStatusAndResult(taskDetailId, TASK_STATUS_SUCCESS, null); // TODO
			if (log.isInfoEnabled()) {
				log.info("[SUCCESS] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
						TASK_STATUS_SUCCESS, taskDetailId, instance.getId(), projectId, projectName);
			}
		} catch (Exception e) {
			if (log.isInfoEnabled()) {
				log.info("[FAILED] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
						TASK_STATUS_FAIL, taskDetailId, instance.getId(), projectId, projectName);
			}
			taskHistoryService.updateDetailStatusAndResult(taskDetailId, TASK_STATUS_FAIL, null); // TODO
			throw new PipelineDeployingException(
					String.format("Failed to deploying for taskDetailId:%s, instance:%s, projectId:%s, projectName:%s",
							instance.getId(), projectId, projectName),
					e);
		}

		if (log.isInfoEnabled()) {
			log.info("Completed of transfer job for instanceId:{}, projectId:{}, projectName:{}", instance.getId(), projectId,
					projectName);
		}
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
	 * Get provider pipeline context.
	 * 
	 * @return
	 */
	protected PipelineContext getContext() {
		return provider.getContext();
	}

	/**
	 * Execution remote commands
	 *
	 * @param remoteHost
	 * @param user
	 * @param command
	 * @param sshkey
	 * @return
	 * @throws Exception
	 */
	protected void doRemoteCommand(String remoteHost, String user, String command, String sshkey) throws Exception {
		hasText(command, "Commands must not be empty.");
		File jobDeployerLog = config.getJobDeployerLog(provider.getContext().getTaskHistory().getId(), instance.getId());

		// Remote timeout(Ms)
		long timeoutMs = config.getRemoteCommandTimeoutMs(getContext().getInstances().size());
		writeFile(jobDeployerLog,
				String.format("Deploying to remote %s@%s, timeout(%s) => command(%s)", user, remoteHost, timeoutMs, command));

		// Do execution.
		CommandResult result = executeWithCommand(remoteHost, user, getUsableCipherSshKey(sshkey), command, timeoutMs);
		if (!isBlank(result.getMessage())) {
			writeFile(jobDeployerLog, String.format("\n%s@%s -> [stdout]\n", user, remoteHost));
			writeFile(jobDeployerLog, result.getMessage());
		}
		if (!isBlank(result.getErrmsg())) {
			writeFile(jobDeployerLog, String.format("\n%s@%s -> [stderr]\n", user, remoteHost));
			writeFile(jobDeployerLog, result.getErrmsg());
		}

	}

	/**
	 * Deciphering usable cipher SSH2 key.
	 *
	 * @param sshkey
	 * @return
	 * @throws Exception
	 */
	protected char[] getUsableCipherSshKey(String sshkey) throws Exception {
		// Obtain text-plain privateKey(RSA)
		String cipherKey = config.getDeploy().getCipherKey();
		char[] sshkeyPlain = new AES(cipherKey).decrypt(sshkey).toCharArray();
		if (log.isInfoEnabled()) {
			log.info("Transfer plain sshkey: {} => {}", cipherKey, "******");
		}
		File jobDeployerLog = config.getJobDeployerLog(provider.getContext().getTaskHistory().getId(), instance.getId());
		FileIOUtils.writeBLineFile(jobDeployerLog, String.format("Transfer plain sshkey: %s => %s", cipherKey, "******"));
		return sshkeyPlain;
	}

}