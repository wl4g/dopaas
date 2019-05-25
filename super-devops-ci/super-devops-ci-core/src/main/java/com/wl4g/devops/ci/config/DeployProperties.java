/*
 * Copyright 2015 the original author or authors.
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

	public static String gitBasePath;

	public static String gitUsername;

	public static String gitPassword;

	public static String backupPath;

	public static String linkPath;

	public static String rsaKey;

	//
	// Temporary
	//

	private static CredentialsProvider credentials;

	public String getGitBasePath() {
		return gitBasePath;
	}

	public void setGitBasePath(String gitBasePath) {
		DeployProperties.gitBasePath = gitBasePath;
	}

	public String getGitUsername() {
		return gitUsername;
	}

	public void setGitUsername(String gitAccount) {
		DeployProperties.gitUsername = gitAccount;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		DeployProperties.gitPassword = gitPassword;
	}

	public static String getBackupPath() {
		return backupPath;
	}

	public void setBackupPath(String bakPath) {
		DeployProperties.backupPath = bakPath;
	}

	public static String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		DeployProperties.linkPath = linkPath;
	}

	public static String getRsaKey() {
		return rsaKey;
	}

	public void setRsaKey(String rsaKey) {
		DeployProperties.rsaKey = rsaKey;
	}

	public static CredentialsProvider getCredentials() {
		if (null == credentials) {
			credentials = new UsernamePasswordCredentialsProvider(gitUsername, gitPassword);
		}
		return credentials;
	}

}