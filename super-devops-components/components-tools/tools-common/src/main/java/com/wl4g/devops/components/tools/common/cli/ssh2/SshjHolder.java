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
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;
import net.schmizz.sshj.xfer.scp.ScpCommandLine;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wl4g.devops.components.tools.common.io.ByteStreamUtils.readFullyToString;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasText;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * SSHJ based SSH2 tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class SshjHolder extends SSH2Holders<Session.Command, SCPFileTransfer> {

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
				scp.download(remoteFilePath, new FileSystemFile(localFile));
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
				// scp.upload(new FileSystemFile(localFile), remoteDir);
				scp.newSCPUploadClient().copy(new FileSystemFile(localFile), remoteDir, ScpCommandLine.EscapeMode.NoEscape);
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
			CallbackFunction<SCPFileTransfer> processor) throws Exception {
		hasText(host, "Transfer host can't empty.");
		hasText(user, "Transfer user can't empty.");
		notNull(processor, "Transfer processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		SSHClient ssh = null;
		SCPFileTransfer scpFileTransfer = null;
		try {
			ssh = new SSHClient();
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.connect(host);
			KeyProvider keyProvider = ssh.loadKeys(new String(pemPrivateKey), null, null);
			ssh.authPublickey(user, keyProvider);

			scpFileTransfer = ssh.newSCPFileTransfer();

			// Transfer file(put/get).
			processor.process(scpFileTransfer);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (nonNull(ssh)) {
					ssh.disconnect();
					ssh.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	// --- Execution commands. ---

	public SshExecResponse execWaitForResponse(String host, String user, char[] pemPrivateKey, String password, String command,
			long timeoutMs) throws Exception {
		return execWaitForComplete(host, user, pemPrivateKey, password, command, cmd -> {
			String message = null, errmsg = null;
			if (nonNull(cmd.getInputStream())) {
				message = readFullyToString(cmd.getInputStream());
			}
			if (nonNull(cmd.getErrorStream())) {
				errmsg = readFullyToString(cmd.getErrorStream());
			}
			return new SshExecResponse(Objects.nonNull(cmd.getExitSignal()) ? cmd.getExitSignal().toString() : null,
					cmd.getExitStatus(), message, errmsg);
		}, timeoutMs);
	}

	@Override
	public <T> T execWaitForComplete(String host, String user, char[] pemPrivateKey, String password, String command,
			ProcessFunction<Session.Command, T> processor, long timeoutMs) throws Exception {
		return doExecCommand(host, user, pemPrivateKey, password, command, cmd -> {
			// Wait for completed by condition.
			cmd.join(timeoutMs, TimeUnit.MILLISECONDS);
			return processor.process(cmd);
		});
	}

	@Override
	public <T> T doExecCommand(String host, String user, char[] pemPrivateKey, String password, String command,
			ProcessFunction<Session.Command, T> processor) throws Exception {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		notNull(processor, "SSH2 command processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		SSHClient ssh = null;
		Session session = null;
		Session.Command cmd = null;
		try {
			ssh = new SSHClient();
			ssh.addHostKeyVerifier(new PromiscuousVerifier());
			ssh.connect(host);

			if (!Collections2.isEmptyArray(pemPrivateKey)) {
				KeyProvider keyProvider = ssh.loadKeys(new String(pemPrivateKey), null, null);
				ssh.authPublickey(user, keyProvider);
			} else {
				ssh.authPassword(user, password);
			}
			session = ssh.startSession();
			// TODO
			command = "source /etc/profile\nsource /etc/bashrc\n" + command;
			cmd = session.exec(command);
			return processor.process(cmd);
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
				if (nonNull(ssh)) {
					ssh.disconnect();
					ssh.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	// --- Tool function's. ---

	@Override
	public Ssh2KeyPair generateKeypair(AlgorithmType type, String comment) throws Exception {
		throw new UnsupportedOperationException();
	}

}