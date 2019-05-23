package com.wl4g.devops.ci.devtool;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author vjay
 * @date 2019-05-08 09:51:00
 */

@Component
@ConfigurationProperties(prefix="devconf")
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
		this.gitBasePath = gitBasePath;
	}

	public String getGitAccount() {
		return gitAccount;
	}

	public void setGitAccount(String gitAccount) {
		this.gitAccount = gitAccount;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		this.gitPassword = gitPassword;
	}

	public static CredentialsProvider getCp() {
		if(null==cp){
			cp = new UsernamePasswordCredentialsProvider(gitAccount,gitPassword);
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
