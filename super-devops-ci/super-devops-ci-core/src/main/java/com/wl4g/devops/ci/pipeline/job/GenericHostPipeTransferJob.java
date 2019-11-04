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

import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import static com.wl4g.devops.ci.utils.LogHolder.logDefault;
import static com.wl4g.devops.ci.utils.PipelineUtils.subPackname;
import static com.wl4g.devops.ci.utils.PipelineUtils.subPacknameWithOutPostfix;
import static com.wl4g.devops.common.utils.cli.SSH2Utils.transferFile;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
public abstract class GenericHostPipeTransferJob<P extends PipelineProvider> extends AbstractPipeTransferJob<P> {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	public GenericHostPipeTransferJob(P provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		super(provider, project, instance, taskHistoryDetails);
	}

	/**
	 * Creating replace remote directory.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param remoteDir
	 * @param sshkey
	 * @throws Exception
	 */
	protected void createReplaceRemoteDirectory(String remoteHost, String user, String remoteDir, String sshkey)
			throws Exception {
		String command = "mkdir -p " + remoteDir;
		logDefault("Creating replace remote directory for %s@%s -> [%s]", user, remoteHost, command);

		// Do directory creating.
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
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
	protected void transferToRemoteTmpDir(String remoteHost, String user, String sshkey, String localFile) throws Exception {
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
	protected void decompressRemoteProgram(String remoteHost, String user, String sshkey, String path) throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		String command = "tar -zxvf " + remoteTmpDir + "/" + subPackname(path) + " -C " + remoteTmpDir; // TODO?
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
	protected void unInstallOlderRemoteProgram(String remoteHost, String user, String sshkey, String path, String targetPath)
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
	protected void installNewerRemoteProgram(String remoteHost, String user, String path, String targetPath, String sshkey)
			throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		String command = "mv " + remoteTmpDir + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
				+ subPacknameWithOutPostfix(path);
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

	/**
	 * Cleanup remote temporary program file.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param sshkey
	 * @param suffix
	 * @throws Exception
	 */
	protected void cleanupRemoteTmpProgramFile(String remoteHost, String user, String sshkey, String suffix) throws Exception {
		File remoteTmpFile = config.getTransferRemoteProgramTmpFile(provider.getPipelineInfo().getProject().getProjectName(),
				suffix);
		String command = "rm -Rf " + remoteTmpFile.getAbsolutePath();
		provider.doRemoteCommand(remoteHost, user, command, sshkey);
	}

}