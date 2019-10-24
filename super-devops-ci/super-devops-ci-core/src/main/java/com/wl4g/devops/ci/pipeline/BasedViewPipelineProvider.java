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

import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.common.utils.codec.AES;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.common.utils.cli.SSH2Utils.transferFile;

import java.io.File;
import java.util.Date;

/**
 * Based abstract view deploy provider.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-05 17:17:00
 */
public abstract class BasedViewPipelineProvider extends AbstractPipelineProvider {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	public BasedViewPipelineProvider(PipelineInfo info) {
		super(info);
	}

	/**
	 * 
	 * @param targetHost
	 * @param userName
	 * @param rsa
	 * @return
	 * @throws Exception
	 */
	public String handOut(String targetHost, String userName, String rsa) throws Exception {
		String result = createRemoteDirectory(targetHost, userName, "/home/" + userName + "/tmp", rsa) + "\n";

		// scp
		result += scpToTmp(targetHost, userName, rsa) + "\n";
		// backup
		// result += backupOnServer(targetHost, userName, rsa) + "\n";
		// mkdir
		result += createRemoteDirectory(targetHost, userName, getPipelineInfo().getProject().getParentAppHome(), rsa) + "\n";
		// tar
		result += tar(targetHost, userName, rsa) + "\n";

		// remove
		// result += removeTar(targetHost, userName, rsa);
		return result;
	}

	/**
	 * Scp To Tmp
	 */
	public String scpToTmp(String targetHost, String userName, String rsa) throws Exception {
		// Obtain text-plain privateKey(RSA)
		String rsaKey = config.getTranform().getCipherKey();
		char[] rsaReal = new AES(rsaKey).decrypt(rsa).toCharArray();

		// Transfer file to remote.
		String localFile = config.getJobBackup(getPipelineInfo().getTaskHistory().getId()) + "/"
				+ getPipelineInfo().getProject().getProjectName() + ".tar.gz";
		transferFile(targetHost, userName, rsaReal, new File(localFile), "/home/" + userName + "/tmp");

		return "SCP to tmp succesful for " + localFile;
	}

	/**
	 * backup on server
	 */
	public String backupOnServer(String targetHost, String userName, String rsa) throws Exception {
		String command = "mv " + getPipelineInfo().getProject().getParentAppHome() + " "
				+ getPipelineInfo().getProject().getParentAppHome() + new Date().getTime();
		return doRemoteCommand(targetHost, userName, command, rsa);
	}

	/**
	 * Unzip in tmp
	 */
	public String tar(String targetHost, String userName, String rsa) throws Exception {
		String command = "tar -zxvf /home/" + userName + "/tmp" + "/" + getPipelineInfo().getProject().getProjectName()
				+ ".tar.gz -C " + getPipelineInfo().getProject().getParentAppHome();
		return doRemoteCommand(targetHost, userName, command, rsa);
	}

	/**
	 * remove tar path
	 */
	public String removeTar(String targetHost, String userName, String rsa) throws Exception {
		String path = "/home/" + userName + "/tmp" + getPipelineInfo().getProject().getProjectName() + ".tar.gz";
		if (StringUtils.isBlank(path) || path.trim().equals("/")) {
			throw new RuntimeException("bad command");
		}
		String command = "rm -f " + path;
		return doRemoteCommand(targetHost, userName, command, rsa);
	}

}