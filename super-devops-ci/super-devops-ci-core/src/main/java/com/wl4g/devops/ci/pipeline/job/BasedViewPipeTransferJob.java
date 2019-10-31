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

import static com.wl4g.devops.common.utils.cli.SSH2Utils.transferFile;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.ci.pipeline.AbstractPipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.utils.codec.AES;

/**
 * Based view deploy transfer job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月25日
 * @since
 * @param <P>
 */
public abstract class BasedViewPipeTransferJob<P extends AbstractPipelineProvider> extends AbstractPipeTransferJob<P> {

	public BasedViewPipeTransferJob(P provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		super(provider, project, instance, taskHistoryDetails);
	}

	/**
	 * 
	 * @param remoteHost
	 * @param user
	 * @param rsa
	 * @return
	 * @throws Exception
	 */
	protected void handOut(String remoteHost, String user, String rsa) throws Exception {
		createRemoteDirectory(remoteHost, user, "/home/" + user + "/tmp", rsa);
		// scp
		scpToTmp(remoteHost, user, rsa);
		// mkdir
		createRemoteDirectory(remoteHost, user, provider.getPipelineInfo().getProject().getParentAppHome(), rsa);
		// tar
		tar(remoteHost, user, rsa);
		// remove
		// removeTar(remoteHost, user, rsa);
	}

	/**
	 * Scp To Tmp
	 */
	protected void scpToTmp(String remoteHost, String user, String rsa) throws Exception {
		// Obtain text-plain privateKey(RSA)
		String rsaKey = config.getTranform().getCipherKey();
		char[] rsaReal = new AES(rsaKey).decrypt(rsa).toCharArray();

		// Transfer file to remote.
		String localFile = config.getJobBackup(provider.getPipelineInfo().getTaskHistory().getId()) + "/"
				+ provider.getPipelineInfo().getProject().getProjectName() + ".tar.gz";
		transferFile(remoteHost, user, rsaReal, new File(localFile), "/home/" + user + "/tmp");

		// return "SCP to tmp succesful for " + localFile;
	}

	/**
	 * backup on server
	 */
	protected void backupOnServer(String remoteHost, String user, String rsa) throws Exception {
		String command = "mv " + provider.getPipelineInfo().getProject().getParentAppHome() + " "
				+ provider.getPipelineInfo().getProject().getParentAppHome() + new Date().getTime();
		provider.doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * Unzip in tmp
	 */
	protected void tar(String remoteHost, String user, String rsa) throws Exception {
		String command = "tar -zxvf /home/" + user + "/tmp" + "/" + provider.getPipelineInfo().getProject().getProjectName()
				+ ".tar.gz -C " + provider.getPipelineInfo().getProject().getParentAppHome();
		provider.doRemoteCommand(remoteHost, user, command, rsa);
	}

	/**
	 * remove tar path
	 */
	protected void removeTar(String remoteHost, String user, String rsa) throws Exception {
		String path = "/home/" + user + "/tmp" + provider.getPipelineInfo().getProject().getProjectName() + ".tar.gz";
		if (StringUtils.isBlank(path) || path.trim().equals("/")) {
			throw new RuntimeException("bad command");
		}
		String command = "rm -f " + path;
		provider.doRemoteCommand(remoteHost, user, command, rsa);
	}

}
