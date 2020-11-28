/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.ci.pipeline.provider;

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.common.exception.ci.BadCommandScriptException;
import com.wl4g.devops.common.exception.ci.PipelineIntegrationBuildingException;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.support.cli.DestroableProcessManager;
import com.wl4g.components.support.cli.command.RemoteDestroableCommand;
import com.wl4g.components.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.ci.config.CiProperties;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.data.PipeStageBuildingProjectDao;
import com.wl4g.devops.ci.data.ProjectDao;
import com.wl4g.devops.ci.pipeline.deploy.CossPipeDeployer;
import com.wl4g.devops.ci.pipeline.deploy.DockerNativePipeDeployer;
import com.wl4g.devops.ci.pipeline.flow.FlowManager;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.bean.erm.SshBean;
import com.wl4g.devops.vcs.operator.VcsOperator;
import com.wl4g.devops.vcs.operator.VcsOperator.VcsProviderKind;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.io.FileIOUtils.writeALineFile;
import static com.wl4g.components.common.io.FileIOUtils.writeBLineFile;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.DateUtils2.getDate;
import static com.wl4g.components.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.common.constant.CiConstants.LOG_FILE_END;
import static com.wl4g.devops.common.constant.CiConstants.LOG_FILE_START;
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

	final protected SmartLogger log = getLogger(getClass());

	/** Pipeline context. */
	final protected PipelineContext context;

	@Autowired
	protected BeanFactory beanFactory;
	@Autowired
	protected NamingPrototypeBeanFactory namingBeanFactory;

	@Autowired
	protected CiProperties config;
	@Autowired
	protected GenericOperatorAdapter<VcsProviderKind, VcsOperator> vcsManager;
	@Autowired
	protected JedisLockManager lockManager;
	@Autowired
	protected DestroableProcessManager pm;
	@Autowired
	protected PipelineJobExecutor jobExecutor;
	@Autowired
	protected FlowManager flowManager;

	@Autowired
	protected DependencyService dependencyService;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected PipeStageBuildingProjectDao pipeStepBuildingProjectDao;

	/**
	 * Pull project source from VCS files fingerprint.
	 */
	private String sourceFingerprint;

	/**
	 * Build project assets files fingerprint.
	 */
	private String assetsFingerprint;

	public AbstractPipelineProvider(PipelineContext context) {
		this.context = notNullOf(context, "pipelineContext");
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
		return getVcsOperator(project.getVcs().getProviderKind());
	}

	/**
	 * Get VCS operator for specific provider.
	 * 
	 * @param vcsKind
	 * @return
	 */
	protected VcsOperator getVcsOperator(String vcsKind) {
		return vcsManager.forOperator(vcsKind);
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
	protected void setSourceFingerprint(String sourceFingerprint) {
		hasText(sourceFingerprint, "sourceFingerprint must not be empty.");
		this.sourceFingerprint = sourceFingerprint;
	}

	/**
	 * Setup build project assets files fingerprint.
	 * 
	 * @param assetsFingerprint
	 */
	protected void setAssetsFingerprint(String assetsFingerprint) {
		hasText(assetsFingerprint, "assetsFingerprint must not be empty.");
		this.assetsFingerprint = assetsFingerprint;
	}

	// --- Remote deployment's. ---

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

		try {
			RemoteDestroableCommand cmd = new RemoteDestroableCommand(command, timeoutMs, user, remoteHost,
					getUsableCipherSshKey(sshkey));
			// Execution command.
			String outmsg = pm.execWaitForComplete(cmd);

			log.info(writeBuildLog("%s@%s, command: [%s], \n\t----- Stdout: -----\n%s", user, remoteHost, command, outmsg));
		} catch (Exception e) {
			String logmsg = writeBuildLog("%s@%s, command: [%s], \n\t----- Stderr: -----\n%s", user, remoteHost, command,
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
	@Override
	public char[] getUsableCipherSshKey(String sshkey) throws Exception {
		// Obtain text-plain privateKey(RSA)
		byte[] cipherKey = config.getDeploy().getCipherKey().getBytes(UTF_8);
		char[] sshkeyPlain = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(sshkey)).toString().toCharArray();

		log.info(writeBuildLog("Decryption plain sshkey: %s => %s", cipherKey, "******"));
		return sshkeyPlain;
	}

	/**
	 * Execution distribution transfer to remote instances for deployments.
	 */
	protected final void startExecutionDeploying() {
		// Creating transfer instances jobs.
		SshBean ssh = getContext().getAppCluster().getSsh();
		List<Runnable> jobs = safeList(getContext().getInstances()).stream().map(i -> {
			return (Runnable) () -> {
				File jobDeployerLog = config.getJobDeployerLog(context.getPipelineHistory().getId(), i.getId());
				try {
					writeBLineFile(jobDeployerLog, LOG_FILE_START);

					// Do deploying.
					createPipeDeployer(i).run();

					// Print successful.
					writeBuildLog("Deployed pipeline successfully, with cluster: '%s', remote instance: '%s@%s'",
							getContext().getAppCluster().getName(), ssh.getUsername(), i.getHostname());
				} catch (Throwable e) {
					String logmsg = writeBuildLog("Failed to deployed to remote! Caused by: \n%s", getStackTraceAsString(e));
					log.error(logmsg);
					// TODO
				} finally {
					writeALineFile(jobDeployerLog, LOG_FILE_END);
				}
			};
		}).collect(toList());

		// Submit jobs for complete.
		if (!isEmpty(jobs)) {
			List<String> instanceStrs = getContext().getInstances().stream().map(i -> i.getHostname() + ":" + i.getEndpoint())
					.collect(toList());

			log.info(writeBuildLog("Start to deploying cluster: '%s' to remote instances: '%s' ... ",
					getContext().getAppCluster().getName(), instanceStrs));

			jobExecutor.getWorker().submitForComplete(jobs, config.getDeploy().getTransferTimeoutMs());
		}

	}

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
		String message = String.format("%s - pipe(%s) : %s", getDate("yy/MM/dd HH:mm:ss"),
				getContext().getPipelineHistory().getId(), content);
		writeALineFile(config.getJobLog(context.getPipelineHistory().getId()), message);
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
	 * Create pipeline task deployer.
	 * 
	 * @param instance
	 * @return
	 */
	protected abstract Runnable newPipeDeployer(AppInstance instance);

	/**
	 * Create distrbuted pipeline deployer.
	 * 
	 * @param instance
	 * @return
	 */
	private Runnable createPipeDeployer(AppInstance instance) {
		switch (getContext().getAppCluster().getDeployType()) {
		case 2:// docker
			Object[] args2 = { this, instance, getContext().getPipelineHistoryInstances() };
			return beanFactory.getBean(DockerNativePipeDeployer.class, args2);
		case 4:// coss
			Object[] args4 = { this, instance, getContext().getPipelineHistoryInstances() };
			return beanFactory.getBean(CossPipeDeployer.class, args4);
		default:
			return newPipeDeployer(instance);
		}

	};

	/**
	 * Placeholder variables resolver.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	class PlaceholderVariableResolver {

		/** Placeholder for projectDir. */
		public static final String PH_PROJECT_DIR = "{pipe.projectDir}";

		/** Placeholder for workspaceDir. */
		public static final String PH_WORKSPACE_DIR = "{pipe.workspaceDir}";

		/** Placeholder for temporary scripts file. */
		public static final String PH_TMP_SCRIPT_FILE = "{pipe.tmpScriptFile}";

		/** Placeholder for backupDir. */
		public static final String PH_BACKUP_DIR = "{pipe.backupDir}";

		/** Placeholder for logPath. */
		public static final String PH_LOG_FILE = "{pipe.logFile}";

		/** Placeholder for remoteTmpDir. */
		public static final String PH_REMOTE_TMP_DIR = "{pipe.remoteTmpDir}";

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
			File tmpScriptFile = config.getJobTmpCommandFile(getContext().getPipelineHistory().getId(),
					getContext().getProject().getId());
			commands = replace(commands, PH_TMP_SCRIPT_FILE, tmpScriptFile.getAbsolutePath());

			// Replace for backupDir.
			File backupDir = config.getJobBackupDir(getContext().getPipelineHistory().getId());
			commands = replace(commands, PH_BACKUP_DIR, backupDir.getAbsolutePath());

			// Replace for logPath.
			File logFile = config.getJobLog(getContext().getPipelineHistory().getId());
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