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
	 * Do executable to instances transfer.</br>
	 * SCP & move & decompression to target directory.
	 * 
	 * @param path
	 * @param remoteHost
	 * @param user
	 * @param targetPath
	 * @param rsa
	 * @throws Exception
	 */
	public void doExecutableTransfer(String path, String remoteHost, String user, String targetPath, String rsa)
			throws Exception {
		createRemoteDirectory(remoteHost, user, "/home/" + user + "/tmp", rsa);
		// scp
		scpToTmpDir(path, remoteHost, user, rsa);
		// tar
		tarToTmp(remoteHost, user, path, rsa);
		// remove
		removeTarPath(remoteHost, user, path, targetPath, rsa);
		// move
		moveToTarPath(remoteHost, user, path, targetPath, rsa);
	}

	public void scpToTmpDir(String path, String remoteHost, String user, String sshkey) throws Exception {
		// Transfer file to remote.
		transferFile(remoteHost, user, provider.getUsableCipherSSHKey(sshkey), new File(path), "/home/" + user + "/tmp");
	}

	/**
	 * Unzip in tmp
	 */
	public void tarToTmp(String remoteHost, String user, String path, String rsa) throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		String command = "tar -xvf " + remoteTmpDir + "/" + subPackname(path) + " -C " + remoteTmpDir;
		provider.doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * remove tar path
	 */
	public void removeTarPath(String remoteHost, String user, String path, String targetPath, String rsa) throws Exception {
		String s = targetPath + "/" + subPacknameWithOutPostfix(path);
		if (isBlank(s) || s.trim().equals("/")) {
			throw new RuntimeException("bad command");
		}
		String command = "rm -Rf " + targetPath + "/" + subPacknameWithOutPostfix(path);
		provider.doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * Move to tar path
	 */
	public void moveToTarPath(String remoteHost, String user, String path, String targetPath, String rsa) throws Exception {
		String remoteTmpDir = config.getTranform().getRemoteHomeTmpDir();
		String command = "mv " + remoteTmpDir + "/" + subPacknameWithOutPostfix(path) + " " + targetPath + "/"
				+ subPacknameWithOutPostfix(path);
		provider.doRemoteCommand(remoteHost, user, command, rsa);
	}

}
