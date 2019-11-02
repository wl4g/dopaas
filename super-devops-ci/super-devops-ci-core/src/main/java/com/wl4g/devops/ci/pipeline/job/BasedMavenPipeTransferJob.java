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

import static com.wl4g.devops.ci.utils.PipelineUtils.subPackname;
import static com.wl4g.devops.ci.utils.PipelineUtils.subPacknameWithOutPostfix;
import static com.wl4g.devops.common.utils.cli.SSH2Utils.transferFile;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.util.List;

import com.wl4g.devops.ci.pipeline.BasedMavenPipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

/**
 * Based MAVEN deploy transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月25日
 * @since
 * @param <P>
 */
public abstract class BasedMavenPipeTransferJob<P extends BasedMavenPipelineProvider> extends AbstractPipeTransferJob<P> {

	public BasedMavenPipeTransferJob(P provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		super(provider, project, instance, taskHistoryDetails);
	}

	/**
	 * Deploying executable to remote host instances.</br>
	 * SCP & Uncompress & cleanup.
	 * 
	 * @param path
	 * @param remoteHost
	 * @param user
	 * @param targetPath
	 * @param sshkey
	 * @throws Exception
	 */
	public void doRemoteDeploying(String path, String remoteHost, String user, String targetPath, String sshkey)
			throws Exception {
		// Create replace remote directory.
		createReplaceRemoteDirectory(remoteHost, user, config.getTranform().getRemoteHomeTmpDir(), sshkey);

		// Transfer to remote temporary directory.
		transferToRemoteTmpDir(remoteHost, user, sshkey, path);

		// Uncompress program.
		decompressExecutableProgram(remoteHost, user, sshkey, path);

		// UnInstall older remote executable program.
		unInstallOlderRemoteProgram(remoteHost, user, path, targetPath, sshkey);

		// Install newer executable program.
		installNewerRemoteProgram(remoteHost, user, path, targetPath, sshkey);
	}

	/**
	 * Transfer executable file to remote directory.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param localFile
	 * @throws Exception
	 */
	private void transferToRemoteTmpDir(String remoteHost, String user, String sshkey, String localFile) throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		transferFile(remoteHost, user, provider.getUsableCipherSSHKey(sshkey), new File(localFile), remoteTmpDir);
	}

	/**
	 * Decompression executable program file.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param path
	 * @throws Exception
	 */
	private void decompressExecutableProgram(String remoteHost, String user, String sshkey, String path) throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		String command = "tar -xvf " + remoteTmpDir + "/" + subPackname(path) + " -C " + remoteTmpDir;
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * UnInstall older last remote program.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param path
	 * @param targetPath
	 * @throws Exception
	 */
	private void unInstallOlderRemoteProgram(String remoteHost, String user, String sshkey, String path, String targetPath)
			throws Exception {
		String s = targetPath + "/" + subPacknameWithOutPostfix(path);
		if (isBlank(s) || s.trim().equals("/")) {
			throw new IllegalArgumentException("Bad command");
		}
		String command = "rm -Rf " + targetPath + "/" + subPacknameWithOutPostfix(path);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Install newer remote program to location.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param path
	 * @param targetPath
	 * @param sshkey
	 * @throws Exception
	 */
	private void installNewerRemoteProgram(String remoteHost, String user, String path, String targetPath, String sshkey)
			throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		String command = "mv " + remoteTmpDir + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
				+ subPacknameWithOutPostfix(path);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

}
