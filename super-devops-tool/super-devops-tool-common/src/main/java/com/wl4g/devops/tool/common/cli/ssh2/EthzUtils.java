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
package com.wl4g.devops.tool.common.cli.ssh2;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPInputStream;
import ch.ethz.ssh2.SCPOutputStream;
import ch.ethz.ssh2.Session;

import com.wl4g.devops.tool.common.function.CallbackFunction;
import com.wl4g.devops.tool.common.function.ProcessFunction;

import java.io.*;

import static ch.ethz.ssh2.ChannelCondition.*;
import static com.wl4g.devops.tool.common.io.ByteStreams2.readFullyToString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static com.wl4g.devops.tool.common.lang.Assert2.*;

/**
 * Remote SSH command process tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class EthzUtils extends Ssh2Clients<Session, SCPClient> {

	// --- Transfer files. ---

	/**
	 * Transfer get file from remote host.
	 * 
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param localFile
	 * @param remoteFilePath
	 * @throws Exception
	 */
	@Override
	public void scpGetFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteFilePath)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteFilePath, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteFilePath);

		try {
			// Transfer get file.
			doScpTransfer(host, user, pemPrivateKey, scp -> {
				try (SCPInputStream sis = scp.get(remoteFilePath); FileOutputStream fos = new FileOutputStream(localFile);) {
					int i = 0;
					byte[] buf = new byte[DEFAULT_TRANSFER_BUFFER];
					while ((i = sis.read(buf)) != -1) {
						fos.write(buf, 0, i);
					}
					fos.flush();
				}
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
	public void scpPutFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteDir) throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteDir);

		try {
			// Transfer send file.
			doScpTransfer(host, user, pemPrivateKey, scp -> {
				try (SCPOutputStream sos = scp.put(localFile.getName(), localFile.length(), remoteDir, "0744");
						FileInputStream fis = new FileInputStream(localFile);) {
					int i = 0;
					byte[] buf = new byte[DEFAULT_TRANSFER_BUFFER];
					while ((i = fis.read(buf)) != -1) {
						sos.write(buf, 0, i);
					}
					sos.flush();
				}
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
	protected void doScpTransfer(String host, String user, char[] pemPrivateKey, CallbackFunction<SCPClient> processor)
			throws Exception {
		hasText(host, "Transfer host can't empty.");
		hasText(user, "Transfer user can't empty.");
		notNull(processor, "Transfer processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		Connection conn = null;
		try {
			// Transfer file(put/get).
			processor.process(new SCPClient(conn = createSsh2Connection(host, user, pemPrivateKey)));
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (nonNull(conn))
					conn.close();
			} catch (Exception e) {
				log.error("", e);
			}
		}

	}

	// --- Execution commands. ---

	/**
	 * Execution commands with SSH2.
	 * 
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param command
	 * @param timeoutMs
	 * @return
	 * @throws IOException
	 */
	public SshExecResponse execWithSsh2(String host, String user, char[] pemPrivateKey, String command, long timeoutMs)
			throws Exception {
		return execWaitForCompleteWithSsh2(host, user, pemPrivateKey, command, session -> {
			String message = null, errmsg = null;
			if (nonNull(session.getStdout())) {
				message = readFullyToString(session.getStdout());
			}
			if (nonNull(session.getStderr())) {
				errmsg = readFullyToString(session.getStderr());
			}
			return new SshExecResponse(session.getExitSignal(), session.getExitStatus(), message, errmsg);
		}, timeoutMs);
	}

	/**
	 * Execution commands wait for complete with SSH2
	 * 
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param command
	 * @param processor
	 * @param timeoutMs
	 * @return
	 * @throws IOException
	 */
	public <T> T execWaitForCompleteWithSsh2(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<Session, T> processor, long timeoutMs) throws Exception {
		return doExecCommandWithSsh2(host, user, pemPrivateKey, command, session -> {
			// Wait for completed by condition.
			session.waitForCondition((CLOSED), timeoutMs);
			return processor.process(session);
		}, timeoutMs);
	}

	/**
	 * Execution commands with SSH2
	 * 
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param command
	 * @param processor
	 * @param timeoutMs
	 * @return
	 * @throws IOException
	 */
	protected final <T> T doExecCommandWithSsh2(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<Session, T> processor, long timeoutMs) throws Exception {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		notNull(processor, "SSH2 command processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		Connection conn = null;
		Session session = null;
		try {
			// Session & send command.
			session = (conn = createSsh2Connection(host, user, pemPrivateKey)).openSession();
			if (log.isInfoEnabled()) {
				log.info("SSH2 sending command to {}@{}, ({})", user, host, command);
			}
			session.execCommand(command, "UTF-8");

			// Customize process.
			return processor.process(session);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (nonNull(session))
					session.close();
			} catch (Exception e2) {
				log.error("", e2);
			}
			try {
				if (nonNull(conn))
					conn.close();
			} catch (Exception e2) {
				log.error("", e2);
			}
		}
	}

	/**
	 * create ssh2 connection.
	 * 
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @return
	 * @throws IOException
	 */
	private final Connection createSsh2Connection(String host, String user, char[] pemPrivateKey) throws IOException {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		notNull(pemPrivateKey, "SSH2 command pemPrivateKey must not be null.");

		Connection conn = new Connection(host);
		conn.connect();
		// Authentication with pub-key.
		isTrue(conn.authenticateWithPublicKey(user, pemPrivateKey, null),
				String.format("Failed to SSH2 authenticate with %s@%s privateKey(%s)", user, host, new String(pemPrivateKey)));

		log.debug("SSH2 connected to {}@{}", user, host);
		return conn;
	}

}