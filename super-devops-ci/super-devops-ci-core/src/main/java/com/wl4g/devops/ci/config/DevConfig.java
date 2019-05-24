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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2019-05-08 09:51:00
 */
@Component
@ConfigurationProperties(prefix = "devconf")
public class DevConfig {

	public static String gitBasePath;

	public static String gitAccount;

	public static String gitPassword;

	public static String bakPath;

	public static String linkPath;

	public static String rsaKey;

	private static CredentialsProvider cp;

	public String getGitBasePath() {
		return gitBasePath;
	}

	public void setGitBasePath(String gitBasePath) {
		DevConfig.gitBasePath = gitBasePath;
	}

	public String getGitAccount() {
		return gitAccount;
	}

	public void setGitAccount(String gitAccount) {
		DevConfig.gitAccount = gitAccount;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		DevConfig.gitPassword = gitPassword;
	}

	public static CredentialsProvider getCp() {
		if (null == cp) {
			cp = new UsernamePasswordCredentialsProvider(gitAccount, gitPassword);
		}
		return cp;
	}

	public static String getBakPath() {
		return bakPath;
	}

	public void setBakPath(String bakPath) {
		DevConfig.bakPath = bakPath;
	}

	public static String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		DevConfig.linkPath = linkPath;
	}

	public static String getRsaKey() {
		return rsaKey;
	}

	public void setRsaKey(String rsaKey) {
		DevConfig.rsaKey = rsaKey;
	}
}