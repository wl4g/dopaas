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
 * Project collaboration management system configuration.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class PcmProperties {

	/**
	 * Request PCM server read timeout.
	 */
	private Integer readTimeout = 60_000;

	/**
	 * Request PCM server connect timeout.
	 */
	private Integer connectTimeout = 10_000;

	/**
	 * Request PCM server read max body bytes.
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

	/** Jira properties. */
	private JiraProperties jira = new JiraProperties();

	/** Redmine properties. */
	private RedmineProperties redmine = new RedmineProperties();

	public JiraProperties getJira() {
		return jira;
	}

	public void setJira(JiraProperties jira) {
		this.jira = jira;
	}

	public RedmineProperties getRedmine() {
		return redmine;
	}

	public void setRedmine(RedmineProperties redmine) {
		this.redmine = redmine;
	}

	/**
	 * JIRA properties.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2020-01-02
	 * @since
	 */
	public static class JiraProperties {

	}

	/**
	 * Redmine properties.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-01-02
	 * @since
	 */
	public static class RedmineProperties {

		/**
		 * Search projects pages limit size.
		 */
		private Integer searchProjectsDefaultPageLimit = 200;

		public Integer getSearchProjectsDefaultPageLimit() {
			return searchProjectsDefaultPageLimit;
		}

		public void setSearchProjectsDefaultPageLimit(Integer searchProjectsDefaultPageLimit) {
			if (nonNull(searchProjectsDefaultPageLimit)) {
				this.searchProjectsDefaultPageLimit = searchProjectsDefaultPageLimit;
			}
		}

	}

}