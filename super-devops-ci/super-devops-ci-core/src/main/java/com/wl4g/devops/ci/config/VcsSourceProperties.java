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
package com.wl4g.devops.ci.config;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * CICD pipeline process, acquiring project source code-related configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class VcsSourceProperties {

	/** Gitlab properties. */
	private GitlabProperties gitlab = new GitlabProperties();

	/** Github properties. */
	private GithubProperties github = new GithubProperties();

	/** Gitee properties. */
	private GiteeProperties gitee = new GiteeProperties();

	/** Bitbucket properties. */
	private BitbucketProperties bitbucket = new BitbucketProperties();

	/** Coding properties. */
	private CodingProperties coding = new CodingProperties();

	/** Alicode properties. */
	private AlicodeProperties alicode = new AlicodeProperties();

	public GitlabProperties getGitlab() {
		return gitlab;
	}

	public void setGitlab(GitlabProperties git) {
		this.gitlab = git;
	}

	public GithubProperties getGithub() {
		return github;
	}

	public void setGithub(GithubProperties github) {
		this.github = github;
	}

	public GiteeProperties getGitee() {
		return gitee;
	}

	public void setGitee(GiteeProperties gitee) {
		this.gitee = gitee;
	}

	public BitbucketProperties getBitbucket() {
		return bitbucket;
	}

	public void setBitbucket(BitbucketProperties bitbucket) {
		this.bitbucket = bitbucket;
	}

	public CodingProperties getCoding() {
		return coding;
	}

	public void setCoding(CodingProperties coding) {
		this.coding = coding;
	}

	public AlicodeProperties getAlicode() {
		return alicode;
	}

	public void setAlicode(AlicodeProperties alicode) {
		this.alicode = alicode;
	}

	/**
	 * GITLAB properties.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-10-13
	 * @since
	 */
	public static class GitlabProperties {
		private String baseUrl;
		private String username;
		private String password;
		private String token;

		/**
		 * credentials for git
		 */
		private CredentialsProvider credentials;

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		// --- Function's. ---

		public CredentialsProvider getCredentials() {
			if (null == credentials) {
				credentials = new UsernamePasswordCredentialsProvider(username, password);
			}
			return credentials;
		}
	}

	/**
	 * Github properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static class GithubProperties {

	}

	/**
	 * Gitee properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static class GiteeProperties {

	}

	/**
	 * Bitbucket properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static class BitbucketProperties {

	}

	/**
	 * Coding properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static class CodingProperties {

	}

	/**
	 * Aliyun code pipeline service properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public static class AlicodeProperties {

	}

}