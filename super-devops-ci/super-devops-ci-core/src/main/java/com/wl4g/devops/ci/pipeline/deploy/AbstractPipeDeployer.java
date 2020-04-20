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
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.exception.ci.PipelineDeployingException;
import com.wl4g.devops.common.exception.ci.PipelineIntegrationBuildingException;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.command.RemoteDestroableCommand;
import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.crypto.symmetric.AESCryptor;
import com.wl4g.devops.tool.common.io.FileIOUtils;
import com.wl4g.devops.tool.common.log.SmartLoggerFactory;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.tool.common.io.FileIOUtils.writeALineFile;
import static com.wl4g.devops.tool.common.lang.DateUtils2.*;
import static com.wl4g.devops.tool.common.lang.Exceptions.getStackTraceAsString;
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
	final protected Logger log = SmartLoggerFactory.getLogger(getClass());

	/** Pipeline CICD properties configuration. */
	@Autowired
	protected CiCdProperties config;

	/** Command-line process manager. */
	@Autowired
	protected DestroableProcessManager pm;

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
		log.info("Starting transfer job for instanceId:{}, projectId:{}, projectName:{} ...", instance.getId(), projectId,
				projectName);

		try {
			TaskHistory taskHisy = provider.getContext().getTaskHistory();
			// Update status to running.
			taskHistoryService.updateDetailStatus(taskDetailId, TASK_STATUS_RUNNING);
			log.info("[PRE] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
					TASK_STATUS_RUNNING, taskDetailId, instance.getId(), projectId, projectName);

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
			taskHistoryService.updateDetailStatus(taskDetailId, TASK_STATUS_SUCCESS);

			log.info("[SUCCESS] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
					TASK_STATUS_SUCCESS, taskDetailId, instance.getId(), projectId, projectName);

		} catch (Exception e) {
			log.info("[FAILED] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
					TASK_STATUS_FAIL, taskDetailId, instance.getId(), projectId, projectName);

			taskHistoryService.updateDetailStatus(taskDetailId, TASK_STATUS_FAIL);
			throw new PipelineDeployingException(
					String.format("Failed to deploying for taskDetailId: %s, instance: %s, projectName: %s, \nCaused by:\n%s",
							taskDetailId, instance.getId(), projectName, getStackTraceAsString(e)));
		}

		log.info("Completed of transfer job for instanceId:{}, projectId:{}, projectName:{}", instance.getId(), projectId,
				projectName);
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

		// Remote timeout(Ms)
		long timeoutMs = config.getRemoteCommandTimeoutMs(getContext().getInstances().size());
		writeDeployLog("Execute remote of %s@%s, timeout: %s, command: [%s]", user, remoteHost, timeoutMs, command);

		try {
			RemoteDestroableCommand cmd = new RemoteDestroableCommand(command, timeoutMs, user, remoteHost,
					getUsableCipherSshKey(sshkey));
			// Execution command.
			String outmsg = pm.execWaitForComplete(cmd);

			log.info(writeDeployLog("%s@%s, command: [%s], \n\t----- Stdout: -----\n%s", user, remoteHost, command, outmsg));
		} catch (Exception e) {
			String logmsg = writeDeployLog("%s@%s, command: [%s], \n\t----- Stderr: -----\n%s", user, remoteHost, command,
					e.getMessage());
			log.info(logmsg);

			// Strictly handle, as long as there is error message in remote
			// command execution, throw error.
			throw new PipelineIntegrationBuildingException(logmsg);
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
		byte[] cipherKey = config.getDeploy().getCipherKey().getBytes(UTF_8);
		char[] sshkeyPlain = new AESCryptor().decrypt(cipherKey, CrypticSource.fromHex(sshkey)).toString().toCharArray();
		log.info("Transfer plain sshkey: {} => {}", cipherKey, "******");

		File jobDeployerLog = config.getJobDeployerLog(provider.getContext().getTaskHistory().getId(), instance.getId());
		FileIOUtils.writeALineFile(jobDeployerLog, String.format("Transfer plain sshkey: %s => %s", cipherKey, "******"));
		return sshkeyPlain;
	}

	/**
	 * Write deploying log to file.
	 * 
	 * @param format
	 * @param args
	 */
	protected String writeDeployLog(String format, Object... args) {
		String content = String.format(format, args);
		String message = String.format("%s - pipe(%s), c(%s), i(%s) : %s", getDate("yy/MM/dd HH:mm:ss"),
				getContext().getTaskHistory().getId(), instance.getClusterId(), instance.getId(), content);

		File jobDeployerLog = config.getJobDeployerLog(provider.getContext().getTaskHistory().getId(), instance.getId());
		writeALineFile(jobDeployerLog, message);
		return content;
	}

}