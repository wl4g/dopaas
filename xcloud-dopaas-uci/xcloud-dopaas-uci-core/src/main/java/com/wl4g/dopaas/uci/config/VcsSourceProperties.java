/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.config;

import static java.util.Objects.nonNull;

/**
 * CICD pipeline process, acquiring project source code-related configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class VcsSourceProperties {

	/**
	 * Request VCS server read timeout.
	 */
	private Integer readTimeout = 60_000;

	/**
	 * Request VCS server connect timeout.
	 */
	private Integer connectTimeout = 10_000;

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

	/**
	 * Request VCS server read max body bytes.
	 */
	private Integer maxResponseSize = 1024 * 1024 * 10;

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Integer getMaxResponseSize() {
		return maxResponseSize;
	}

	public void setMaxResponseSize(Integer maxResponseSize) {
		this.maxResponseSize = maxResponseSize;
	}

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

		/**
		 * Search projects pages limit size.
		 */
		private Integer searchProjectsDefaultPageLimit = 20;

		public Integer getSearchProjectsDefaultPageLimit() {
			return searchProjectsDefaultPageLimit;
		}

		public void setSearchProjectsDefaultPageLimit(Integer defaultSearchProjectsPageLimit) {
			if (nonNull(defaultSearchProjectsPageLimit)) {
				this.searchProjectsDefaultPageLimit = defaultSearchProjectsPageLimit;
			}
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