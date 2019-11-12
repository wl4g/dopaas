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

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Generic version control service operator program based on GIT protocol
 * family.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-08
 * @since
 */
public abstract class GenericBasedGitVcsOperator extends AbstractVcsOperator {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T clone(Object credentials, String remoteUrl, String projecDir, String branchName) throws IOException {
		super.clone(credentials, remoteUrl, projecDir, branchName);

		File path = new File(projecDir);
		if (!path.exists()) {
			path.mkdirs();
		}
		try {
			CloneCommand cmd = Git.cloneRepository().setURI(remoteUrl).setDirectory(path)
					.setCredentialsProvider((CredentialsProvider) credentials);
			if (!isBlank(branchName)) {
				cmd.setBranch(branchName);
			}
			Git git = cmd.call();

			if (log.isInfoEnabled()) {
				log.info("Cloning from '" + remoteUrl + "' to " + git.getRepository());
			}
			return (T) git;
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Faild to clone from '%s'", remoteUrl), e);
		}
	}

	@Override
	public void checkoutAndPull(Object credentials, String projecDir, String branchName) {
		super.checkoutAndPull(credentials, projecDir, branchName);

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
			if (exist) { // Exist to checkout
				git.checkout().setName(branchName).call();
			} else { // Not exist to checkout & create local branch
				git.checkout().setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName)
						.setForceRefUpdate(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
			}
			// Pull & get latest source code.
			git.pull().setCredentialsProvider((CredentialsProvider) credentials).call();

			if (log.isInfoEnabled()) {
				log.info("Checkout & pull successful for branchName:{}, projecDir:{}", branchName, projecDir);
			}
		} catch (Exception e) {
			String errmsg = String.format("Failed to checkout & pull for branchName: %s, projecDir: %s", branchName, projecDir);
			log.error(errmsg, e);
			throw new IllegalStateException(errmsg, e);
		}
	}

	@Override
	public List<String> delLocalBranch(String projecDir, String branchName, boolean force) {
		super.delLocalBranch(projecDir, branchName, force);

		String gitPath = projecDir + "/.git";
		try (Git git = Git.open(new File(gitPath))) {
			return git.branchDelete().setForce(force).setBranchNames(branchName).call();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean ensureRepo(String projecDir) {
		super.ensureRepo(projecDir);

		File file = new File(projecDir + "/.git");
		return file.exists();
	}

	@Override
	public String getLatestCommitted(String projecDir) throws Exception {
		super.getLatestCommitted(projecDir);

		try (Git git = Git.open(new File(projecDir))) {
			Iterable<RevCommit> iterb = git.log().setMaxCount(1).call(); // Latest-commit
			Iterator<RevCommit> it = iterb.iterator();
			if (it.hasNext()) {
				// Get latest version committed.
				String commitSign = it.next().getName();
				if (log.isInfoEnabled()) {
					log.info("Latest committed sign:{}, path:{}", commitSign, projecDir);
				}
				return commitSign;
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T rollback(Object credentials, String projecDir, String sign) {
		super.rollback(credentials, projecDir, sign);

		String metaPath = projecDir + "/.git";
		try (Git git = Git.open(new File(metaPath))) {
			git.fetch().setCredentialsProvider((CredentialsProvider) credentials).call();
			Ref ref = git.checkout().setName(sign).call();

			String msg = "Rollback branch completed, sign:" + sign + ", localPath:" + projecDir;
			if (log.isInfoEnabled()) {
				log.info(msg);
			}
			return (T) ref;
		} catch (Exception e) {
			String errmsg = String.format("Failed to rollback, sign:%s, localPath:%s", sign, projecDir);
			log.error(errmsg, e);
			throw new IllegalStateException(e);
		}

	}

	/**
	 * Get (local) branch name.
	 *
	 * @param ref
	 * @return
	 */
	protected static String getBranchName(Ref ref) {
		String name = ref.getName();
		if ("HEAD".equals(trimToEmpty(name))) {
			ObjectId objectId = ref.getObjectId();
			name = objectId.getName();
		} else {
			int index = name.lastIndexOf("/");
			name = name.substring(index + 1);
		}
		return name;
	}

	// =======================get by ssh

	public static TransportConfigCallback getTransportConfigCallback(byte[] identity) throws Exception {
		SshSessionFactory sshFactory = new JschConfigSessionFactory() {

			@Override
			protected void configure(OpenSshConfig.Host hc, Session session) {
				session.setConfig("StrictHostKeyChecking", "no");
				// session.setPort(2022);
			}

			@Override
			protected JSch createDefaultJSch(FS fs) throws JSchException {
				JSch defaultJSch = super.createDefaultJSch(fs);
				defaultJSch.removeAllIdentity();
				// defaultJSch.addIdentity( "/Users/vjay/.ssh/id_rsa");
				// identity = getContent("/Users/vjay/.ssh/id_rsa");
				defaultJSch.getIdentityRepository().add(identity);
				return defaultJSch;
			}
		};

		TransportConfigCallback transportCallback = new TransportConfigCallback() {
			@Override
			public void configure(Transport transport) {
				SshTransport sshTransport = (SshTransport) transport;
				sshTransport.setSshSessionFactory(sshFactory);
			}
		};
		return transportCallback;
	}

	public static byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			return null;
		}
		FileInputStream fi = new FileInputStream(file);
		byte[] buffer = new byte[(int) fileSize];
		int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset != buffer.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		fi.close();
		return buffer;
	}

}
