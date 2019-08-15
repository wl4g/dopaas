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
public class CiCdProperties {

	/**
	 * Git check out url
	 */
	private String gitUrl;

	/**
	 * Git check out path
	 */
	private String gitBasePath;

	/**
	 * Git check out username
	 */
	private String gitUsername;

	/**
	 * Git check out password
	 */
	private String gitPassword;

	/**
	 * Gitlab token, for get branch
	 */
	private String gitToken;

	/**
	 * After build tar,save the tar in backup path
	 */
	private String backupPath;

	/**
	 * Cipher key , for decrypt the ssh ase
	 */
	private String cipherKey;

	/**
	 * build task time out
	 */
	private Integer taskTimeout;

	/**
	 * docker push username
	 */
	private String dockerPushUsername;

	/**
	 * docker push password
	 */
	private String dockerPushPasswd;

	/**
	 * credentials for git
	 */
	private CredentialsProvider credentials;

	public String getGitBasePath() {// if blank ,user default
		if (StringUtils.isBlank(gitBasePath)) {
			gitBasePath = System.getProperties().getProperty("user.home") + "/gittest";
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
		if (StringUtils.isBlank(backupPath)) {// if blank ,user default
			backupPath = System.getProperties().getProperty("user.home") + "/git/bak";
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

	public Integer getTaskTimeout() {
		return taskTimeout;
	}

	public void setTaskTimeout(Integer taskTimeout) {
		this.taskTimeout = taskTimeout;
	}

	public String getDockerPushUsername() {
		return dockerPushUsername;
	}

	public void setDockerPushUsername(String dockerPushUsername) {
		this.dockerPushUsername = dockerPushUsername;
	}

	public String getDockerPushPasswd() {
		return dockerPushPasswd;
	}

	public void setDockerPushPasswd(String dockerPushPasswd) {
		this.dockerPushPasswd = dockerPushPasswd;
	}

	public String getGitUrl() {
		return gitUrl;
	}

	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

	public String getGitToken() {
		return gitToken;
	}

	public void setGitToken(String gitToken) {
		this.gitToken = gitToken;
	}
}