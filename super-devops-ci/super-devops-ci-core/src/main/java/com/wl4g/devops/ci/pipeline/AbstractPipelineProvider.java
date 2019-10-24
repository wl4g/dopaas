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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.ci.utils.CommandLogHolder;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.cli.SSH2Utils.CommandResult;
import com.wl4g.devops.common.utils.codec.AES;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskHisBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskSignDao;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.cli.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.ci.utils.CommandLogHolder.*;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.PROJECT_PATH;
import static com.wl4g.devops.common.utils.cli.SSH2Utils.executeWithCommand;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

/**
 * Abstract based deploy provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
public abstract class AbstractPipelineProvider implements PipelineProvider {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected JedisLockManager lockManager;
	@Autowired
	protected PipelineJobExecutor jobExecutor;
	@Autowired
	protected ProcessManager processManager;

	@Autowired
	protected DependencyService dependencyService;
	@Autowired
	protected TaskHistoryService taskHistoryService;
	@Autowired
	protected TaskHisBuildCommandDao taskHisBuildCommandDao;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected TaskSignDao taskSignDao;

	/** Pipeline information. */
	final protected PipelineInfo pipeInfo;

	protected String shaGit;
	protected String shaLocal;

	public AbstractPipelineProvider(PipelineInfo info) {
		this.pipeInfo = info;
		String[] a = info.getProject().getTarPath().split("/");
		this.pipeInfo.setTarName(a[a.length - 1]);
	}

	public PipelineInfo getPipelineInfo() {
		return pipeInfo;
	}

	@Override
	public String getShaGit() {
		return shaGit;
	}

	@Override
	public String getShaLocal() {
		return shaLocal;
	}

	public void setShaGit(String shaGit) {
		this.shaGit = shaGit;
	}

	public void setShaLocal(String shaLocal) {
		this.shaLocal = shaLocal;
	}

	public TaskHistoryService getTaskHistoryService() {
		return taskHistoryService;
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
	public void doRemoteCommand(String remoteHost, String user, String command, String sshkey) throws Exception {
		hasText(command, "Commands must not be empty.");
		Integer logId = getPipelineInfo().getTaskHistory().getId();

		// Remote timeout(Ms)
		long timeoutMs = config.getRemoteCommandTimeoutMs(getPipelineInfo().getInstances().size());
		logAdd(logId, "Transfer remote execution for %s@%s, timeout(%s) => command(%s)", user, remoteHost, timeoutMs, command);

		// Execution command.
		CommandResult result = executeWithCommand(remoteHost, user, getUsableCipherSSHKey(sshkey), command, timeoutMs);

		logAdd(logId, "\n----------------- Stdout -------------------\n");
		if (!isBlank(result.getMessage())) {
			logAdd(logId, result.getMessage());
		}
		logAdd(logId, "\n----------------- Stderr -------------------\n");
		if (!isBlank(result.getErrmsg())) {
			logAdd(logId, result.getErrmsg());
		}

	}

	/**
	 * Creating remote directory.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param path
	 * @param sshkey
	 * @throws Exception
	 */
	protected void createRemoteDirectory(String remoteHost, String user, String path, String sshkey) throws Exception {
		String command = "mkdir -p " + path;
		logAdd(getPipelineInfo().getTaskHistory().getId(), "Creating remote directory: %s", command);
		// Do directory creating.
		doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Get log appender.
	 * 
	 * @param logId
	 * @return
	 */
	public LogAppender getLogAppender(Integer logId) {
		notNull(logId, "Log appender id must not be null.");
		return CommandLogHolder.getLogAppender(getClass().getSimpleName() + "-" + logId);
	}

	/**
	 * Append log message.
	 * 
	 * @param logId
	 * @param format
	 * @return
	 */
	public LogAppender logAdd(Integer logId, String format, Object... message) {
		return getLogAppender(logId).logAdd(String.format(format, message));
	}

	/**
	 * Do startup transfer instances jobs.
	 */
	protected void doStartupTransferInstances() {
		// Creating transfer instances jobs.
		List<Runnable> jobs = getPipelineInfo().getInstances().stream().map(instance -> newPipelineJob(instance))
				.collect(toList());

		// Submit jobs for complete.
		if (!isEmpty(jobs)) {
			if (log.isInfoEnabled()) {
				log.info("Transfer jobs starting...  for instances({}), {}", jobs.size(), getPipelineInfo().getInstances());
			}
			jobExecutor.submitForComplete(jobs, config.getTranform().getTransferTimeoutMs());
		}

	}

	/**
	 * Create pipeline job.
	 * 
	 * @param instance
	 * @return
	 */
	protected abstract Runnable newPipelineJob(AppInstance instance);

	protected String commandReplace(String command, String projectPath) {
		command.replaceAll("\\[", "\\[");
		command = command.replaceAll(PROJECT_PATH, projectPath);// projectPath
		// TODO ......
		return command;
	}

	/**
	 * Do decrypted usable cipher SSH2 key.
	 * 
	 * @param sshkey
	 * @return
	 * @throws Exception
	 */
	protected char[] getUsableCipherSSHKey(String sshkey) throws Exception {
		Integer logId = getPipelineInfo().getTaskHistory().getId();

		// Obtain text-plain privateKey(RSA)
		String cipherKey = config.getTranform().getCipherKey();
		char[] sshkeyPlain = new AES(cipherKey).decrypt(sshkey).toCharArray();
		if (log.isInfoEnabled()) {
			log.info("Transfer decrypted sshkey: {} => {}", cipherKey, "******");
		}

		logAdd(logId, "Transfer decrypted sshkey: %s => %s", cipherKey, "******");
		return sshkeyPlain;
	}

}