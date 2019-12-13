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
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.ci.vcs.CompositeVcsOperateAdapter;
import com.wl4g.devops.ci.vcs.VcsOperator;
import com.wl4g.devops.ci.vcs.VcsOperator.VcsProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.exception.ci.BadCommandScriptException;
import com.wl4g.devops.common.exception.ci.PipelineIntegrationBuildingException;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.TaskHistoryBuildCommandDao;
import com.wl4g.devops.dao.ci.TaskSignDao;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.tool.common.cli.SshUtils.CommandResult;
import com.wl4g.devops.tool.common.crypto.AES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOG_FILE_END;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.LOG_FILE_START;
import static com.wl4g.devops.tool.common.cli.SshUtils.execWithSsh2;
import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static com.wl4g.devops.tool.common.io.FileIOUtils.writeALineFile;
import static com.wl4g.devops.tool.common.io.FileIOUtils.writeBLineFile;
import static com.wl4g.devops.tool.common.lang.DateUtils2.getDate;
import static com.wl4g.devops.tool.common.lang.Exceptions.getStackTraceAsString;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Abstract basic developments pipeline provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-08-05 17:17:00
 */
public abstract class AbstractPipelineProvider implements PipelineProvider {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Pipeline context. */
	final protected PipelineContext context;

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected PipelineJobExecutor jobExecutor;
	@Autowired
	protected BeanFactory beanFactory;
	@Autowired
	protected JedisLockManager lockManager;
	@Autowired
	protected DestroableProcessManager pm;
	@Autowired
	protected CompositeVcsOperateAdapter vcsAdapter;

	@Autowired
	protected DependencyService dependencyService;
	@Autowired
	protected TaskHistoryService taskHistoryService;
	@Autowired
	protected TaskHistoryBuildCommandDao taskHistoryBuildCommandDao;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected TaskSignDao taskSignDao;

	/**
	 * Pull project source from VCS files fingerprint.
	 */
	private String sourceFingerprint;

	/**
	 * Build project assets files fingerprint.
	 */
	private String assetsFingerprint;

	public AbstractPipelineProvider(PipelineContext context) {
		notNull(context, "Pipeline context must not be null.");
		this.context = context;
	}

	/**
	 * Basic pipeline.
	 */
	public PipelineContext getContext() {
		return context;
	}

	/**
	 * Get VCS operator for specific project.
	 * 
	 * @param project
	 * @return
	 */
	protected VcsOperator getVcsOperator(Project project) {
		notNull(project, "Project can't be null.");
		notNull(project.getVcs(), "Project.vcs can't be null.");
		return getVcsOperator(project.getVcs().getProvider());
	}

	/**
	 * Get VCS operator for specific provider.
	 * 
	 * @param vcsProvider
	 * @return
	 */
	protected VcsOperator getVcsOperator(Integer vcsProvider) {
		return vcsAdapter.forAdapt(VcsProvider.of(vcsProvider));
	}

	// --- Fingerprint's. ---

	/**
	 * Get pull project source from VCS files fingerprint.
	 */
	@Override
	public String getSourceFingerprint() {
		return sourceFingerprint;
	}

	/**
	 * Get build project assets files fingerprint.
	 */
	@Override
	public String getAssetsFingerprint() {
		return assetsFingerprint;
	}

	/**
	 * Setup pull project source from VCS files fingerprint.
	 * 
	 * @param sourceFingerprint
	 */
	protected void setupSourceFingerprint(String sourceFingerprint) {
		hasText(sourceFingerprint, "sourceFingerprint must not be empty.");
		this.sourceFingerprint = sourceFingerprint;
	}

	/**
	 * Setup build project assets files fingerprint.
	 * 
	 * @param assetsFingerprint
	 */
	protected void setupAssetsFingerprint(String assetsFingerprint) {
		hasText(assetsFingerprint, "assetsFingerprint must not be empty.");
		this.assetsFingerprint = assetsFingerprint;
	}

	// --- Function's. ---

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
	@Override
	public void doRemoteCommand(String remoteHost, String user, String command, String sshkey) throws Exception {
		hasText(command, "Commands must not be empty.");

		// Remote timeout(Ms)
		long timeoutMs = config.getRemoteCommandTimeoutMs(getContext().getInstances().size());
		writeBuildLog("Execute remote of %s@%s, timeout: %s, command: [%s]", user, remoteHost, timeoutMs, command);

		// Execution command.
		CommandResult result = execWithSsh2(remoteHost, user, getUsableCipherSshKey(sshkey), command, timeoutMs);
		if (!isBlank(result.getMessage())) {
			String logmsg = writeBuildLog("%s@%s, command:[%s], \n\t----- Stdout: -----\n%s", user, remoteHost, command,
					result.getMessage());
			if (log.isInfoEnabled()) {
				log.info(logmsg);
			}
		}
		if (!isBlank(result.getErrmsg())) {
			String logmsg = writeBuildLog("%s@%s, command:[%s], \n\t----- Stderr: -----\n%s", user, remoteHost, command,
					result.getErrmsg());
			if (log.isInfoEnabled()) {
				log.info(logmsg);
			}
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
	@Override
	public char[] getUsableCipherSshKey(String sshkey) throws Exception {
		// Obtain text-plain privateKey(RSA)
		String cipherKey = config.getDeploy().getCipherKey();
		char[] sshkeyPlain = new AES(cipherKey).decrypt(sshkey).toCharArray();
		if (log.isInfoEnabled()) {
			log.info("Decryption plain sshkey: {} => {}", cipherKey, "******");
		}
		writeBuildLog("Decryption plain sshkey: %s => %s", cipherKey, "******");
		return sshkeyPlain;
	}

	/**
	 * Execution distribution transfer to remote instances for deployments.
	 */
	protected final void executeRemoteDeploying() {
		// Creating transfer instances jobs.
		List<Runnable> jobs = safeList(getContext().getInstances()).stream().map(i -> {
			return (Runnable) () -> {
				File jobDeployerLog = config.getJobDeployerLog(context.getTaskHistory().getId(), i.getId());
				try {
					writeALineFile(jobDeployerLog, LOG_FILE_START);

					// Do deploying.
					newDeployer(i).run();

					// Print successful.
					writeBuildLog("Deployed pipeline successfully, with cluster: '%s', remote instance: '%s@%s'",
							getContext().getAppCluster().getName(), i.getSshUser(), i.getHostname());
				} catch (Exception e) {
					String logmsg = writeBuildLog("Failed to deployed to remote!\nCaused by: \n", getStackTraceAsString(e));
					log.error(logmsg);
				} finally {
					writeBLineFile(jobDeployerLog, LOG_FILE_END);
				}
			};
		}).collect(toList());

		// Submit jobs for complete.
		if (!isEmpty(jobs)) {
			String logmsg = writeBuildLog("Start to deploying cluster: '%s' to remote instances: '%s@%s' ... ",
					getContext().getAppCluster().getName(), jobs.size());
			if (log.isInfoEnabled()) {
				log.info(logmsg);
			}
			jobExecutor.submitForComplete(jobs, config.getDeploy().getTransferTimeoutMs());
		}

	}

	/**
	 * Create pipeline transfer job.
	 * 
	 * @param instance
	 * @return
	 */
	protected abstract Runnable newDeployer(AppInstance instance);

	/**
	 * Write provider building log to file.
	 * 
	 * @param format
	 * @param args
	 * @return Returns the actual log content, excluding time prefixes such as
	 *         append.
	 */
	protected String writeBuildLog(String format, Object... args) {
		String content = String.format(format, args);
		String message = String.format("%s - pipe(%s) : %s", getDate("yy/MM/dd HH:mm:ss"), getContext().getTaskHistory().getId(),
				content);
		writeBLineFile(config.getJobLog(context.getTaskHistory().getId()), message);
		return content;
	}

	/**
	 * Resolve commands placeholder variables.
	 * 
	 * @param commands
	 * @return
	 */
	protected String resolveCmdPlaceholderVariables(String commands) {
		return new PlaceholderVariableResolver(commands).resolve().get();
	}

	/**
	 * Placeholder variables resolver.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	class PlaceholderVariableResolver {

		/** Placeholder for projectDir. */
		final public static String PH_PROJECT_DIR = "{pipe.projectDir}";

		/** Placeholder for workspaceDir. */
		final public static String PH_WORKSPACE_DIR = "{pipe.workspaceDir}";

		/** Placeholder for temporary scripts file. */
		final public static String PH_TMP_SCRIPT_FILE = "{pipe.tmpScriptFile}";

		/** Placeholder for backupDir. */
		final public static String PH_BACKUP_DIR = "{pipe.backupDir}";

		/** Placeholder for logPath. */
		final public static String PH_LOG_FILE = "{pipe.logFile}";

		/** Placeholder for remoteTmpDir. */
		final public static String PH_REMOTE_TMP_DIR = "{pipe.remoteTmpDir}";

		/** Resolving commands. */
		private String commands;

		public PlaceholderVariableResolver(String commands) {
			hasText(commands, "Resolving pipeline commands must not be empty.");
			this.commands = commands;
		}

		/**
		 * Resolving placeholder variables.
		 * 
		 * @param context
		 * @param command
		 * @return
		 */
		PlaceholderVariableResolver resolve() {
			// Replace for workspace.
			commands = replace(commands, PH_WORKSPACE_DIR, config.getWorkspace());

			// Replace for projectDir.
			String projectDir = config.getProjectSourceDir(getContext().getProject().getProjectName()).getAbsolutePath();
			commands = replace(commands, PH_PROJECT_DIR, projectDir);

			// Replace for backupDir.
			File tmpScriptFile = config.getJobTmpCommandFile(getContext().getTaskHistory().getId(),
					getContext().getProject().getId());
			commands = replace(commands, PH_TMP_SCRIPT_FILE, tmpScriptFile.getAbsolutePath());

			// Replace for backupDir.
			File backupDir = config.getJobBackup(getContext().getTaskHistory().getId());
			commands = replace(commands, PH_BACKUP_DIR, backupDir.getAbsolutePath());

			// Replace for logPath.
			File logFile = config.getJobLog(getContext().getTaskHistory().getId());
			commands = replace(commands, PH_LOG_FILE, logFile.getAbsolutePath());

			// Replace for remoteTmpDir.
			String remoteTmpDir = config.getDeploy().getRemoteHomeTmpDir();
			commands = replace(commands, PH_REMOTE_TMP_DIR, remoteTmpDir);

			return this;
		}

		/**
		 * Get safety asserted commands.
		 * 
		 * @return
		 */
		String get() {
			// Invalid placeholder ?
			if (containsAny(commands, "{", "}")) {
				if (contains(commands, "{") && contains(commands, "}")) {
					String invalidVar = commands.substring(commands.indexOf("{"), commands.indexOf("}") + 1);
					throw new BadCommandScriptException(String.format(
							"Bad placeholder '%s' in commands script. See:https://github.com/wl4g/super-devops/blob/master/super-devops-ci/README.md",
							invalidVar));
				} else {
					throw new BadCommandScriptException(String.format("Bad commands script for: %s", commands));
				}
			}

			return commands;
		}

	}

}