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
package com.wl4g.devops.ci.utils;

import com.wl4g.devops.shell.utils.ShellConsoleHolder;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Git utility tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-06 09:54:00
 */
public class GitUtils {
	public static final Logger log = LoggerFactory.getLogger(GitUtils.class);

	/**
	 * Clone
	 */
	public static void clone(CredentialsProvider credentials, String remoteUrl, String localPath) throws IOException {
		File path = new File(localPath);
		if (!path.exists()) {
			path.mkdirs();
		}

		try {
			Git git = Git.cloneRepository().setURI(remoteUrl).setDirectory(path).setCredentialsProvider(credentials).call();

			if (log.isInfoEnabled()) {
				log.info("Cloning from " + remoteUrl + " to " + git.getRepository());
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	/**
	 * Clone
	 */
	public static void clone(CredentialsProvider credentials, String remoteUrl, String localPath, String branchName)
			throws IOException {
		File path = new File(localPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		try {
			Git git = Git.cloneRepository().setURI(remoteUrl).setDirectory(path).setCredentialsProvider(credentials)
					.setBranch(branchName).call();
			if (log.isInfoEnabled()) {
				log.info("Cloning from " + remoteUrl + " to " + git.getRepository());
			}

			ShellConsoleHolder.printfQuietly("Cloning from " + remoteUrl + " to " + git.getRepository());
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checkout and pull
	 */
	public static void checkout(CredentialsProvider credentials, String localPath, String branchName) {
		String projectURL = localPath + "/.git";
		Git git = null;
		try {
			git = Git.open(new File(projectURL));
			List<Ref> refs = git.branchList().call();
			boolean exist = false;// is branch exist
			for (Ref ref : refs) {
				String branchNameHad = ref.getName().substring(11);
				if (StringUtils.equals(branchName, branchNameHad)) {
					exist = true;
				}
			}
			if (exist) {// if exist --checkout
				git.checkout().setName(branchName).call();
			} else {// if not exist --checkout and create local branch
				git.checkout().setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName)
						.setForceRefUpdate(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
			}
			// pull -- get newest code
			git.pull().setCredentialsProvider(credentials).call();

			if (log.isInfoEnabled()) {
				log.info("checkout branch success;branchName=" + branchName + " localPath=" + localPath);
			}
			ShellConsoleHolder.printfQuietly("checkout branch success;branchName=" + branchName + " localPath=" + localPath);
		} catch (Exception e) {
			String errmsg = String.format("checkout branch failure. branchName=%s, localPath=%s", branchName, localPath);
			ShellConsoleHolder.printfQuietly(errmsg);
			log.error(errmsg, e);
			throw new RuntimeException(e);
		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	/**
	 * Delete branch
	 */
	public static void delbranch(String localPath, String branchName) {
		String projectURL = localPath + "/.git";
		Git git = null;
		try {
			git = Git.open(new File(projectURL));
			git.branchDelete().setForce(true).setBranchNames(branchName).call();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	/**
	 * Get local branch list
	 */
	public static void branchlist(String localPath) {
		String projectURL = localPath + "/.git";
		Git git = null;
		try {
			git = Git.open(new File(projectURL));
			List<Ref> refs = git.branchList().call();
			for (Ref ref : refs) {
				System.out.println(ref.getName().substring(11));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// git远程url地址
		// String url =
		// "http://code.anjiancloud.owner/devops-team/safecloud-devops.git";
		// String localPath = "/Users/vjay/gittest/safecloud-devops";

		// String url = "https://github.com/wl4g/super-devops.git";
		String url = "http://code.anjiancloud.owner:8443/biz-team/android-team/portal-for-android.git";
		String localPath = "/Users/vjay/gittest/super-devops";
		String branchName = "master";

		CredentialsProvider cp = new UsernamePasswordCredentialsProvider("", "");
		GitUtils.clone(cp, url, localPath);
		GitUtils.checkout(cp, localPath, branchName);

		// GitUtils.delbranch(localPath,branchName);
		GitUtils.branchlist(localPath);
	}

}