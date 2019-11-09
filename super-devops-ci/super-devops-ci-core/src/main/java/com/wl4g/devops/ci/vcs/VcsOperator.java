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
package com.wl4g.devops.ci.vcs;

import java.io.IOException;
import java.util.List;

/**
 * VCS APIs operator.
 *
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public abstract interface VcsOperator {

	/**
	 * VCS type definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月5日
	 * @since
	 */
	public final static class VcsType {

		/** Vcs for GITLAB. */
		final public static String GITLAB = "VcsWithGitlab";

		/** Vcs for github. */
		final public static String GITHUB = "VcsWithGithub";

		/** Vcs for gitee. */
		final public static String GITEE = "VcsWithGitee";

		/** Vcs for alicode. */
		final public static String ALICODE = "VcsWithAlicode";

		/** Vcs for bitbucket. */
		final public static String BITBUCKET = "VcsWithBitbucket";

		/** Vcs for coding. */
		final public static String CODING = "VcsWithCoding";
	}

	/**
	 * VCS type definition.
	 * 
	 * @return
	 */
	default String vcsType() {
		throw new UnsupportedOperationException();
	}

	// --- APIs operator. ---

	/**
	 * Get VCS remote branch names.
	 *
	 * @param projectId
	 * @return
	 */
	List<String> getRemoteBranchNames(int projectId);

	/**
	 * Get VCS remote tag names.
	 *
	 * @param projectId
	 * @return
	 */
	List<String> getRemoteTags(int projectId);

	/**
	 * Find remote project ID by project name.
	 *
	 * @param projectName
	 * @return
	 */
	Integer findRemoteProjectId(String projectName);

	// --- VCS operator. ---

	/**
	 * Clone from remote VCS server.
	 * 
	 * @param <T>
	 * @param credentials
	 *            VCS authentication credentials
	 * @param remoteUrl
	 *            remote VCS server URI
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 * @throws IOException
	 */
	default <T> T clone(Object credentials, String remoteUrl, String projecDir) throws IOException {
		return clone(credentials, remoteUrl, projecDir, null);
	}

	/**
	 * Clone from remote VCS server.
	 * 
	 * @param <T>
	 * @param credentials
	 *            VCS authentication credentials
	 * @param remoteUrl
	 *            remote VCS server URI
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 * @return
	 * @throws IOException
	 */
	<T> T clone(Object credentials, String remoteUrl, String projecDir, String branchName) throws IOException;

	/**
	 * Checkout and pull.
	 * 
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 */
	void checkoutAndPull(Object credentials, String projecDir, String branchName);

	/**
	 * Delete (local) branch.
	 * 
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param branchName
	 * @param force
	 * @return
	 */
	List<String> delLocalBranch(String projecDir, String branchName, boolean force);

	/**
	 * Check project local VCS repository exist, created when not present.
	 * 
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 */
	boolean ensureLocalRepo(String projecDir);

	/**
	 * Get (local) latest committed ID.
	 * 
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @return
	 * @throws Exception
	 */
	String getLatestCommitted(String projecDir) throws Exception;

	/**
	 * Roll-back VCS project local repository(fetch and checkout).
	 * 
	 * @param <T>
	 * @param credentials
	 *            VCS authentication credentials
	 * @param projecDir
	 *            project local VCS repository directory absolute path.
	 * @param sign
	 *            committed ID.
	 * @return
	 */
	<T> T rollback(Object credentials, String projecDir, String sign);

}