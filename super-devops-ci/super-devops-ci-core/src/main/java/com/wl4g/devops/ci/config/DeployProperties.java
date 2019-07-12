/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.ci.config;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * Deployments configuration properties.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public class DeployProperties {

	private String gitBasePath;

	private String gitUsername;

	private String gitPassword;

	private String backupPath;

	//private String linkPath;

	private String cipherKey;

	//
	// Temporary
	//

	private CredentialsProvider credentials;

	public String getGitBasePath() {
		if(StringUtils.isBlank(gitBasePath)){
			gitBasePath = System.getProperties().getProperty("user.home")+"/git";
		}
		return gitBasePath;
	}

	public void setGitBasePath(String gitBasePath) {
		this.gitBasePath = gitBasePath;
	}

	public String getGitUsername() {
		return gitUsername;
	}

	public void setGitUsername(String gitAccount) {
		this.gitUsername = gitAccount;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		this.gitPassword = gitPassword;
	}

	public String getBackupPath() {
		if(StringUtils.isBlank(backupPath)){
			backupPath = System.getProperties().getProperty("user.home")+"/git/bak";
		}
		return backupPath;
	}

	public void setBackupPath(String bakPath) {
		this.backupPath = bakPath;
	}

	public String getCipherKey() {
		return cipherKey;
	}

	public void setCipherKey(String cipherKey) {
		this.cipherKey = cipherKey;
	}

	public CredentialsProvider getCredentials() {
		if (null == credentials) {
			credentials = new UsernamePasswordCredentialsProvider(gitUsername, gitPassword);
		}
		return credentials;
	}


}