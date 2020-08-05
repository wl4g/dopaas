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

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.io.FileIOUtils;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.components.core.bean.ci.PipeStepInstanceCommand;
import com.wl4g.components.core.bean.ci.PipelineHistoryInstance;
import com.wl4g.components.core.bean.erm.AppCluster;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.erm.Ssh;
import com.wl4g.components.core.exception.ci.PipelineDeployingException;
import com.wl4g.components.core.exception.ci.PipelineIntegrationBuildingException;
import com.wl4g.components.support.cli.DestroableProcessManager;
import com.wl4g.components.support.cli.command.RemoteDestroableCommand;
import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.ci.service.PipelineHistoryService;
import com.wl4g.devops.dao.erm.ClusterConfigDao;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.io.FileIOUtils.writeALineFile;
import static com.wl4g.components.common.lang.DateUtils2.getDate;
import static com.wl4g.components.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.components.core.constants.CiDevOpsConstants.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.*;

/**
 * Abstract deploying transfer job.
 *
 * @param <P>
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月23日
 * @since
 */
public abstract class AbstractPipeDeployer<P extends PipelineProvider> implements Runnable {
	final protected Logger log = SmartLoggerFactory.getLogger(getClass());

	/**
	 * Pipeline CICD properties configuration.
	 */
	@Autowired
	protected CiCdProperties config;

	/**
	 * Command-line process manager.
	 */
	@Autowired
	protected DestroableProcessManager pm;

	@Autowired
	protected PipelineHistoryService pipelineHistoryService;

	@Autowired
	protected ClusterConfigDao clusterConfigDao;

	@Value("${spring.profiles.active}")
	protected String profile;

	/**
	 * Pipeline provider.
	 */
	final protected P provider;

	/**
	 * Pipeline deploy instance.
	 */
	final protected AppInstance instance;

	/**
	 * Pipeline taskDetailId.
	 */
	final protected Integer pipeHisInstanceId;

	public AbstractPipeDeployer(P provider, AppInstance instance, List<PipelineHistoryInstance> pipelineHistoryInstances) {
		notNull(provider, "Pipeline provider must not be null.");
		notNull(instance, "Pipeline job instance must not be null.");
		notEmpty(pipelineHistoryInstances, "Pipeline task historyDetails must not be null.");
		this.provider = provider;
		this.instance = instance;

		// Task details.
		Optional<PipelineHistoryInstance> pipelineHistoryInstance = pipelineHistoryInstances.stream()
				.filter(detail -> detail.getInstanceId().intValue() == instance.getId().intValue()).findFirst();
		isTrue(pipelineHistoryInstance.isPresent(), "Not found taskDetailId by details.");
		this.pipeHisInstanceId = pipelineHistoryInstance.get().getId();
	}

	@Override
	public void run() {
		notNull(pipeHisInstanceId, "Transfer job for taskDetailId inmust not be null");
		Integer projectId = getContext().getProject().getId();
		String projectName = getContext().getProject().getProjectName();
		AppCluster appCluster = getContext().getAppCluster();
		Ssh ssh = appCluster.getSsh();
		PipeStepInstanceCommand pipeStepInstanceCommand = provider.getContext().getPipeStepInstanceCommand();
		log.info("Starting transfer job for instanceId:{}, projectId:{}, projectName:{} ...", instance.getId(), projectId,
				projectName);

		try {
			// Update status to running.
			pipelineHistoryService.updatePipeHisInstanceStatus(pipeHisInstanceId, TASK_STATUS_RUNNING);
			log.info("[PRE] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{} ...",
					TASK_STATUS_RUNNING, pipeHisInstanceId, instance.getId(), projectId, projectName);

			// PRE commands.
			if (pipeStepInstanceCommand.getEnable() == 1 && !isBlank(pipeStepInstanceCommand.getPreCommand())
					&& appCluster.getDeployType() == 1) {
				doRemoteCommand(instance.getHostname(), ssh.getUsername(), pipeStepInstanceCommand.getPreCommand(),
						ssh.getSshKey());
			}

			// Deploying distribute to remote.
			doRemoteDeploying(instance.getHostname(), ssh.getUsername(), ssh.getSshKey());

			// Post remote commands.(e.g: restart)
			if (pipeStepInstanceCommand.getEnable() == 1 && !isBlank(pipeStepInstanceCommand.getPostCommand())
					&& appCluster.getDeployType() == 1) {
				doRemoteCommand(instance.getHostname(), ssh.getUsername(), pipeStepInstanceCommand.getPostCommand(),
						ssh.getSshKey());
			}

			// Update status to success.
			pipelineHistoryService.updatePipeHisInstanceStatus(pipeHisInstanceId, TASK_STATUS_SUCCESS);

			log.info("[SUCCESS] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
					TASK_STATUS_SUCCESS, pipeHisInstanceId, instance.getId(), projectId, projectName);

		} catch (Exception e) {
			log.info("[FAILED] Updated transfer status to {} for taskDetailId:{}, instance:{}, projectId:{}, projectName:{}",
					TASK_STATUS_FAIL, pipeHisInstanceId, instance.getId(), projectId, projectName);

			pipelineHistoryService.updatePipeHisInstanceStatus(pipeHisInstanceId, TASK_STATUS_FAIL);
			throw new PipelineDeployingException(
					String.format("Failed to deploying for taskDetailId: %s, instance: %s, projectName: %s, \nCaused by:\n%s",
							pipeHisInstanceId, instance.getId(), projectName, getStackTraceAsString(e)));
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
		char[] sshkeyPlain = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(sshkey)).toString().toCharArray();
		log.info("Transfer plain sshkey: {} => {}", cipherKey, "******");

		File jobDeployerLog = config.getJobDeployerLog(provider.getContext().getPipelineHistory().getId(), instance.getId());
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
				getContext().getPipelineHistory().getId(), instance.getClusterId(), instance.getId(), content);

		File jobDeployerLog = config.getJobDeployerLog(provider.getContext().getPipelineHistory().getId(), instance.getId());
		writeALineFile(jobDeployerLog, message);
		return content;
	}

}