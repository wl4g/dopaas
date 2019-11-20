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

import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.common.bean.ci.TaskHistoryInstance;
import com.wl4g.devops.common.bean.share.AppInstance;

import static com.wl4g.devops.ci.utils.LogHolder.logDefault;
import static com.wl4g.devops.ci.utils.PipelineUtils.getUnExtensionFilename;
import static com.wl4g.devops.common.utils.cli.SSH2Utils.transferFile;
import static org.springframework.util.Assert.hasText;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic based host deploying transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月23日
 * @since
 * @param <P>
 */
public abstract class GenericHostPipeDeployer<P extends PipelineProvider> extends AbstractPipeDeployer<P> {
	/** Default executable file suffix. */
	final public static String DEFAULT_FILE_SUFFIX = "tar";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	public GenericHostPipeDeployer(P provider, AppInstance instance, List<TaskHistoryInstance> taskHistoryInstances) {
		super(provider, instance, taskHistoryInstances);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		// Ensure remote home temporary dir.
		createReplaceRemoteDirectory(remoteHost, user, sshkey, config.getDeploy().getRemoteHomeTmpDir());

		// Transfer to remote home temporary dir.
		transferToRemoteTmpDir(remoteHost, user, sshkey);

		// Ensure remote app install dir(parent appHome).
		createReplaceRemoteDirectory(remoteHost, user, sshkey, getProgramInstallDir());

		// Uncompress remote program.
		decompressRemoteProgram(remoteHost, user, sshkey);

		// UnInstall older remote executable program.
		unInstallRemoteOlderProgram(remoteHost, user, sshkey);

		// Install newer executable program.
		installRemoteNewerProgram(remoteHost, user, sshkey);

		// Cleanup temporary program file.
		cleanupRemoteProgramTmpFile(remoteHost, user, sshkey);
	}

	/**
	 * Creating replace remote directory.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param remoteDir
	 * @throws Exception
	 */
	protected void createReplaceRemoteDirectory(String remoteHost, String user, String sshkey, String remoteDir)
			throws Exception {
		hasText(remoteDir, "Creating remote directory path must not be empty.");
		String command = "mkdir -p " + remoteDir;
		logDefault("Creating replace remote directory for %s@%s -> [%s]", user, remoteHost, command);
		// Directory creating.
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Transfer executable file to remote directory.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected void transferToRemoteTmpDir(String remoteHost, String user, String sshkey) throws Exception {
		String localFile = getContext().getProjectSourceDir() + getContext().getProject().getAssetsPath();
		logDefault("Transfer to remote tmpdir for %s@%s -> [%s]", user, remoteHost, localFile);
		String remoteTmpDir = config.getDeploy().getRemoteHomeTmpDir();
		transferFile(remoteHost, user, provider.getUsableCipherSshKey(sshkey), new File(localFile), remoteTmpDir);
	}

	/**
	 * Decompression executable program assets file.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected void decompressRemoteProgram(String remoteHost, String user, String sshkey) throws Exception {
		String command = "tar -zxvf " + getRemoteTmpFilePath() + " -C " + config.getDeploy().getRemoteHomeTmpDir();
		logDefault("Decompress remote program for %s@%s -> [%s]", user, remoteHost, command);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * UnInstall older last remote program.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected void unInstallRemoteOlderProgram(String remoteHost, String user, String sshkey) throws Exception {
		String command = "rm -Rf " + getProgramInstallDir() + "/" + getPrgramInstallFileName();
		logDefault("UnInstall remote older program for %s@%s -> [%s]", user, remoteHost, command);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Install newer remote program to location.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected void installRemoteNewerProgram(String remoteHost, String user, String sshkey) throws Exception {
		String decompressedTmpFile = config.getDeploy().getRemoteHomeTmpDir() + getPrgramInstallFileName();
		String command = "mv " + decompressedTmpFile + " " + getProgramInstallDir();
		logDefault("Install remote newer program for %s@%s -> [%s]", user, remoteHost, command);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Cleanup remote program temporary file.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @throws Exception
	 */
	protected void cleanupRemoteProgramTmpFile(String remoteHost, String user, String sshkey) throws Exception {
		String command = "rm -Rf " + getRemoteTmpFilePath();
		logDefault("Cleanup remote temporary program file for %s@%s -> [%s]", user, remoteHost, command);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Get project program absolute path directory.
	 * 
	 * @return
	 */
	protected String getProgramInstallDir() {
		return getContext().getProject().getParentAppHome();
	}

	/**
	 * Get project program install file name.
	 * 
	 * @return
	 */
	protected String getPrgramInstallFileName() {
		String distFilePath = getContext().getProject().getAssetsPath();
		return getUnExtensionFilename(distFilePath);
	}

	/**
	 * Get remote temporary executable file absolute path.
	 * 
	 * @return
	 */
	protected String getRemoteTmpFilePath() {
		return config.getTransferRemoteHomeTmpFile(getContext().getProject().getProjectName(), DEFAULT_FILE_SUFFIX)
				.getAbsolutePath();
	}

}