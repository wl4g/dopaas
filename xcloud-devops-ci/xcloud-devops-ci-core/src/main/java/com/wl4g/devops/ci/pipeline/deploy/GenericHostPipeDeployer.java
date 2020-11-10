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

import com.wl4g.components.common.cli.ssh2.SSH2Holders;
import com.wl4g.components.core.bean.ci.PipelineHistoryInstance;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.devops.ci.pipeline.provider.PipelineProvider;

import java.io.File;
import java.util.List;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static org.springframework.util.Assert.hasText;

/**
 * Generic based host deployer.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月23日
 * @since
 * @param <P>
 */
public abstract class GenericHostPipeDeployer<P extends PipelineProvider> extends AbstractPipeDeployer<P> {

	public GenericHostPipeDeployer(P provider, AppInstance instance, List<PipelineHistoryInstance> pipelineHistoryInstances) {
		super(provider, instance, pipelineHistoryInstances);
	}

	@Override
	protected void doRemoteDeploying(String remoteHost, String user, String sshkey) throws Exception {
		hasTextOf(remoteHost, "remoteHost");
		hasTextOf(user, "remoteUser");
		hasTextOf(sshkey, "sshPubkey");

		// Ensure remote home temporary dir.
		createReplaceRemoteDirectory(remoteHost, user, sshkey, config.getDeploy().getRemoteHomeTmpDir());

		// Transfer to remote home temporary dir.
		transferToRemoteTmpDir(remoteHost, user, sshkey);

		// Ensure remote app install dir(parent appHome).
		createReplaceRemoteDirectory(remoteHost, user, sshkey, getProgramInstallDir());

		// UnInstall older remote executable program.
		unInstallRemoteOlderProgram(remoteHost, user, sshkey);

		// Uncompress remote program.
		decompressRemoteProgram(remoteHost, user, sshkey);

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
		writeDeployLog("Creating remote directory: %s@%s [%s]", user, remoteHost, command);

		// Directory creating.
		doRemoteCommand(remoteHost, user, command, sshkey);
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
		String localFile = config.getJobBackupDir(getContext().getPipelineHistory().getId()) + "/" + getPrgramInstallFileName()
				+ "." + DEFAULT_ASSETS_SUFFIX;

		String remoteTmpDir = config.getDeploy().getRemoteHomeTmpDir();
		writeDeployLog(String.format("Transfer to remote tmpdir: %s@%s [%s]", user, remoteHost, localFile));

		SSH2Holders.getDefault().scpPutFile(remoteHost, user, provider.getUsableCipherSshKey(sshkey), null, new File(localFile),
				remoteTmpDir);
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
		String command = "tar -xvf " + getRemoteTmpFilePath() + " -C " + config.getDeploy().getRemoteHomeTmpDir();
		writeDeployLog("Decompress remote program: %s@%s [%s]", user, remoteHost, command);

		doRemoteCommand(remoteHost, user, command, sshkey);
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
		writeDeployLog("Uninstall remote older program: %s@%s [%s]", user, remoteHost, command);

		doRemoteCommand(remoteHost, user, command, sshkey);
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
		String decompressedTmpFile = config.getDeploy().getRemoteHomeTmpDir() + "/" + getPrgramInstallFileName();
		String command = "mv " + decompressedTmpFile + " " + getProgramInstallDir();
		writeDeployLog("Install remote newer program: %s@%s [%s]", user, remoteHost, command);

		doRemoteCommand(remoteHost, user, command, sshkey);
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
		writeDeployLog("Cleanup remote temporary program file: %s@%s [%s]", user, remoteHost, command);

		doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Get project program absolute path directory.
	 * 
	 * @return
	 */
	protected String getProgramInstallDir() {
		return getContext().getPipeline().getParentAppHome();
	}

	/**
	 * Get project program install file name.
	 *
	 * @return
	 */
	protected String getPrgramInstallFileName() {
		return config.getPrgramInstallFileName(getContext().getAppCluster().getName());
	}

	/**
	 * Get remote temporary executable file absolute path.
	 * 
	 * @returns
	 */
	protected String getRemoteTmpFilePath() {
		String result = config.getDeploy().getRemoteHomeTmpDir() + "/" + getPrgramInstallFileName() + "." + DEFAULT_ASSETS_SUFFIX;
		return result;
	}

	/** Default executable file suffix. */
	public static final String DEFAULT_ASSETS_SUFFIX = "tar";

}