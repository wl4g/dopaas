package com.wl4g.devops.ci.config;

import static org.apache.commons.lang3.StringUtils.isBlank;

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

	private GitlabProperties gitlab = new GitlabProperties();

	public GitlabProperties getGitlab() {
		return gitlab;
	}

	public void setGitlab(GitlabProperties git) {
		this.gitlab = git;
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
		 * Git check out path
		 */
		private String workspace;

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

		public void setWorkspace(String workspace) {
			this.workspace = workspace;
		}

		public String getWorkspace() {
			if (isBlank(workspace)) {// if blank ,user default
				workspace = System.getProperties().getProperty("user.home") + "/git";
			}
			return workspace;
		}

		public CredentialsProvider getCredentials() {
			if (null == credentials) {
				credentials = new UsernamePasswordCredentialsProvider(username, password);
			}
			return credentials;
		}
	}

}
