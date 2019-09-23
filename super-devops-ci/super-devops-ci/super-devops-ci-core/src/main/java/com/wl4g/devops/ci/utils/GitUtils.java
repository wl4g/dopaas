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
package com.wl4g.devops.ci.utils;

import com.wl4g.devops.common.annotation.Unused;
import com.wl4g.devops.shell.utils.ShellContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * GIT utility tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-06 09:54:00
 */
public abstract class GitUtils {
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

			ShellContextHolder.printfQuietly("Cloning from " + remoteUrl + " to " + git.getRepository());
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
				String branchNameHad = getBranchName(ref);
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
			ShellContextHolder.printfQuietly("checkout branch success;branchName=" + branchName + " localPath=" + localPath);
		} catch (Exception e) {
			String errmsg = String.format("checkout branch failure. branchName=%s, localPath=%s", branchName, localPath);
			ShellContextHolder.printfQuietly(errmsg);
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
	@Unused
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
	@Unused
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

	private static String getBranchName(Ref ref) {
		String name = ref.getName();
		if ("HEAD".equals(name)) {
			ObjectId objectId = ref.getObjectId();
			name = objectId.getName();
		} else {
			int index = name.lastIndexOf("/");
			name = name.substring(index + 1);
		}

		return name;
	}

	public static boolean checkGitPahtExist(String path) {
		File file = new File(path + "/.git");
		return file.exists();
	}

	public static String getOldestCommitSha(String localPath) throws Exception {
		Git git = Git.open(new File(localPath));
		Iterable<RevCommit> iterable = git.log().setMaxCount(1).call();// 拿最新的comit-sha
		Iterator<RevCommit> iter = iterable.iterator();
		if (iter.hasNext()) {
			RevCommit commit = iter.next();
			String commitID = commit.getName(); // 这个应该就是提交的版本号
			log.info("OldestCommitSha={} localPath={}", commitID, localPath);
			return commitID;
		}
		return null;
	}

	/**
	 * Checkout and pull
	 */
	public static void roolback(CredentialsProvider credentials, String localPath, String sha) {
		String projectURL = localPath + "/.git";
		Git git = null;
		try {
			git = Git.open(new File(projectURL));
			git.fetch().setCredentialsProvider(credentials).call();
			git.checkout().setName(sha).call();
			if (log.isInfoEnabled()) {
				log.info("checkout branch success;sha={} localPath={}", sha, localPath);
			}
			ShellContextHolder.printfQuietly("rollback branch success;sha=" + sha + " localPath=" + localPath);
		} catch (Exception e) {
			String errmsg = String.format("rollback branch failure. sha=%s, localPath=%s", sha, localPath);
			ShellContextHolder.printfQuietly(errmsg);
			log.error(errmsg, e);
			throw new RuntimeException(e);
		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

}