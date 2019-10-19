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
package com.wl4g.devops.ci.utils;

import static com.wl4g.devops.shell.utils.ShellContextHolder.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
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
	final protected static Logger log = LoggerFactory.getLogger(GitUtils.class);

	/**
	 * Clone from remote GIT URI.
	 *
	 * @param credentials
	 * @param remoteUrl
	 * @param localPath
	 * @return
	 * @throws IOException
	 */
	public static Git clone(CredentialsProvider credentials, String remoteUrl, String localPath) throws IOException {
		return clone(credentials, remoteUrl, localPath, null);
	}

	/**
	 * Clone from remote GIT URI.
	 *
	 * @param credentials
	 * @param remoteUrl
	 * @param localPath
	 * @param branchName
	 * @return
	 * @throws IOException
	 */
	public static Git clone(CredentialsProvider credentials, String remoteUrl, String localPath, String branchName)
			throws IOException {
		File path = new File(localPath);
		if (!path.exists()) {
			path.mkdirs();
		}
		try {
			CloneCommand cmd = Git.cloneRepository().setURI(remoteUrl).setDirectory(path).setCredentialsProvider(credentials);
			if (!isBlank(branchName)) {
				cmd.setBranch(branchName);
			}
			Git git = cmd.call();

			String msg = "Cloning from '" + remoteUrl + "' to " + git.getRepository();
			if (log.isInfoEnabled()) {
				log.info(msg);
			}
			printfQuietly(msg);
			return git;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Checkout and pull
	 */
	public static void checkout(CredentialsProvider credentials, String projecDir, String branchName) {
		String projectURL = projecDir + "/.git";
		try (Git git = Git.open(new File(projectURL))) {
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
				log.info("checkout branch success;branchName=" + branchName + " localPath=" + projecDir);
			}
			printfQuietly("checkout branch success;branchName=" + branchName + " localPath=" + projecDir);
		} catch (Exception e) {
			String errmsg = String.format("checkout branch failure. branchName=%s, localPath=%s", branchName, projecDir);
			printfQuietly(errmsg);
			log.error(errmsg, e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Delete local branch.
	 *
	 * @param localProjectPath
	 * @param branchName
	 * @param force
	 * @return
	 */
	public static List<String> delLocalBranch(String localProjectPath, String branchName, boolean force) {
		String gitPath = localProjectPath + "/.git";
		try (Git git = Git.open(new File(gitPath))) {
			return git.branchDelete().setForce(force).setBranchNames(branchName).call();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get local branch list.
	 *
	 * @param projecDir
	 * @return
	 */
	public static List<Ref> getLocalBranchs(String projecDir) {
		String gitPath = projecDir + "/.git";
		try (Git git = Git.open(new File(gitPath))) {
			return git.branchList().call();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Get (local)branch name.
	 *
	 * @param ref
	 * @return
	 */
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

	/**
	 * Check GIT local path exist?
	 *
	 * @param path
	 * @return
	 */
	public static boolean checkGitPath(String path) {
		File file = new File(path + "/.git");
		return file.exists();
	}

	/**
	 * Get local latest committed.
	 *
	 * @param projecDir
	 * @return
	 * @throws Exception
	 */
	public static String getLatestCommitted(String projecDir) throws Exception {
		try (Git git = Git.open(new File(projecDir))) {
			Iterable<RevCommit> iterb = git.log().setMaxCount(1).call();// 拿最新的comit-sha
			Iterator<RevCommit> it = iterb.iterator();
			if (it.hasNext()) {
				RevCommit commit = it.next();
				String commitID = commit.getName(); // Latest committed version?
				if (log.isInfoEnabled()) {
					log.info("Latest committed sha:{}, path:{}", commitID, projecDir);
				}
				return commitID;
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	/**
	 * Roll-back GIT stored(fetch and checkout).
	 *
	 * @param credentials
	 * @param projecDir
	 * @param sha
	 * @return
	 */
	public static Ref rollback(CredentialsProvider credentials, String projecDir, String sha) {
		String projectURL = projecDir + "/.git";
		try (Git git = Git.open(new File(projectURL))) {
			git.fetch().setCredentialsProvider(credentials).call();
			Ref ref = git.checkout().setName(sha).call();
			String msg = "Rollback branch completed, sha:" + sha + ", localPath:" + projecDir;
			if (log.isInfoEnabled()) {
				log.info(msg);
			}
			printfQuietly(msg);
			return ref;
		} catch (Exception e) {
			String errmsg = String.format("Failed to rollback, sha:%s, localPath:%s", sha, projecDir);
			printfQuietly(errmsg);
			log.error(errmsg, e);
			throw new IllegalStateException(e);
		}
	}

}