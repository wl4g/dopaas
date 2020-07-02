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
package com.wl4g.devops.components.tools.common.cli.ssh2;

import com.wl4g.devops.components.tools.common.collection.Collections2;
import com.wl4g.devops.components.tools.common.function.CallbackFunction;
import com.wl4g.devops.components.tools.common.function.ProcessFunction;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.scp.DefaultScpClientCreator;
import org.apache.sshd.client.scp.ScpClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.security.SecurityUtils;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Iterator;
import java.util.Objects;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasText;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Sshd based SSH2 tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class SshdHolder extends SSH2Holders<ChannelExec, ScpClient> {

	// --- Transfer files. ---

	/**
	 * Transfer get file from remote host.(user sftp)
	 *
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param localFile
	 * @param remoteFilePath
	 * @throws Exception
	 */
	@Override
	public void scpGetFile(String host, String user, char[] pemPrivateKey, String password, File localFile, String remoteFilePath)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteFilePath, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteFilePath);

		try {
			// Transfer get file.
			doScpTransfer(host, user, pemPrivateKey, password, scp -> {
				scp.download(remoteFilePath, localFile.getAbsolutePath());
			});

			log.debug("SCP get transfered: '{}' from '{}@{}:{}'", localFile.getAbsolutePath(), user, host, remoteFilePath);
		} catch (IOException e) {
			throw e;
		}

	}

	/**
	 * Transfer put file to remote host directory.
	 *
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param localFile
	 * @param remoteDir
	 * @throws Exception
	 */
	@Override
	public void scpPutFile(String host, String user, char[] pemPrivateKey, String password, File localFile, String remoteDir)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteDir);

		try {
			// Transfer send file.
			doScpTransfer(host, user, pemPrivateKey, password, scp -> {
				scp.upload(localFile.getAbsolutePath(), remoteDir);
			});

			log.debug("SCP put transfered: '{}' to '{}@{}:{}'", localFile.getAbsolutePath(), user, host, remoteDir);
		} catch (IOException e) {
			throw e;
		}

	}

	/**
	 * Perform file transfer with remote host, including scp.put/upload or
	 * scp.get/download.
	 *
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param processor
	 * @throws IOException
	 */
	@Override
	protected void doScpTransfer(String host, String user, char[] pemPrivateKey, String password,
			CallbackFunction<ScpClient> processor) throws Exception {
		hasText(host, "Transfer host can't empty.");
		hasText(user, "Transfer user can't empty.");
		notNull(processor, "Transfer processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		SshClient client = null;
		ClientSession session = null;
		ScpClient scpClient = null;
		try {
			client = SshClient.setUpDefaultClient();
			client.start();
			session = authWithPrivateKey(client, host, null, user, pemPrivateKey, password);
			scpClient = DefaultScpClientCreator.INSTANCE.createScpClient(session);
			processor.process(scpClient);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (nonNull(session)) {
					session.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
			try {
				if (nonNull(client)) {
					client.stop();
					client.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	// --- Execution commands. ---

	public SshExecResponse execWaitForResponse(String host, String user, char[] pemPrivateKey, String password, String command,
			long timeoutMs) throws Exception {
		return execWaitForComplete(host, user, pemPrivateKey, password, command, session -> {
			String msg = null, errmsg = null;
			if (nonNull(session.getOut())) {
				// msg = readFullyToString(session.getOut());
				msg = session.getOut().toString();
			}
			if (nonNull(session.getErr())) {
				errmsg = session.getErr().toString();
			}
			return new SshExecResponse(session.getExitSignal(), session.getExitStatus(), msg, errmsg);
		}, timeoutMs);
	}

	@Override
	public <T> T execWaitForComplete(String host, String user, char[] pemPrivateKey, String password, String command,
			ProcessFunction<ChannelExec, T> processor, long timeoutMs) throws Exception {
		return doExecCommand(host, user, pemPrivateKey, password, command, channelExec -> {
			// Wait for completed by condition.
			channelExec.waitFor(singleton(ClientChannelEvent.CLOSED), timeoutMs);
			return processor.process(channelExec);
		});
	}

	@Override
	public <T> T doExecCommand(String host, String user, char[] pemPrivateKey, String password, String command,
			ProcessFunction<ChannelExec, T> processor) throws Exception {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		notNull(processor, "SSH2 command processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		ClientSession session = null;
		ChannelExec chSession = null;
		SshClient client = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		try {
			client = SshClient.setUpDefaultClient();
			client.start();
			session = authWithPrivateKey(client, host, null, user, pemPrivateKey, password);
			// channelExec = session.createExecChannel(DEFAULT_LINUX_ENV_CMD +
			// command);
			chSession = session.createExecChannel(command);
			chSession.setErr(err);
			chSession.setOut(out);
			chSession.open();
			return processor.process(chSession);
		} catch (Exception e) {
			throw e;
		} finally {
			if (nonNull(out)) {
				try {
					out.close();
				} catch (Exception e) {
					log.error("", e);
				}
			}
			try {
				if (nonNull(chSession)) {
					chSession.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
			try {
				if (nonNull(session)) {
					session.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
			try {
				if (nonNull(client)) {
					client.stop();
					client.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	private InputStream getStrToStream(String sInputString) {
		if (sInputString != null && !sInputString.trim().equals("")) {
			return new ByteArrayInputStream(sInputString.getBytes());
		}
		return null;
	}

	private ClientSession authWithPrivateKey(SshClient client, String host, Integer port, String user, char[] pemPrivateKey,
			String password) throws IOException, GeneralSecurityException {
		ClientSession session = client.connect(user, host, Objects.isNull(port) ? 22 : port).verify(10000).getSession();
		if (!Collections2.isEmptyArray(pemPrivateKey)) {
			Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(session, null,
					getStrToStream(new String(pemPrivateKey)), null);
			Iterator<KeyPair> iterator = keyPairs.iterator();
			if (iterator.hasNext()) {
				KeyPair next = iterator.next();
				session.addPublicKeyIdentity(next);// for password-less
			}
		} else {
			session.addPasswordIdentity(password); // for password-based
		}
		// authentication
		AuthFuture verify = session.auth().verify(10000);
		if (!verify.isSuccess()) {
			throw new GeneralSecurityException("auth fail");
		}

		return session;
	}

	/**
	 * auth with password (unused now)
	 *
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	// private ClientSession authWithPassword(SshClient client, String host,
	// Integer port, String user, String password)
	// throws IOException, GeneralSecurityException {
	// ClientSession session = client.connect(user, host, Objects.isNull(port) ?
	// 22 : port).verify(10000).getSession();
	// session.addPasswordIdentity(password); // for password-based
	// // authentication
	// AuthFuture verify = session.auth().verify(10000);
	// if (!verify.isSuccess()) {
	// throw new GeneralSecurityException("auth fail");
	// }
	// return session;
	// }

	// --- Tool function's. ---
	@Override
	public Ssh2KeyPair generateKeypair(AlgorithmType type, String comment) throws Exception {
		throw new UnsupportedOperationException();
	}

}