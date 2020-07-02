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

import ch.ethz.ssh2.*;
import com.wl4g.devops.components.tools.common.collection.Collections2;
import com.wl4g.devops.components.tools.common.function.CallbackFunction;
import com.wl4g.devops.components.tools.common.function.ProcessFunction;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static ch.ethz.ssh2.ChannelCondition.CLOSED;
import static com.wl4g.devops.components.tools.common.io.ByteStreamUtils.readFullyToString;
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Ethz based SSH2 tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public class EthzHolder extends SSH2Holders<Session, SCPClient> {

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
	public void scpGetFile(String host, String user, char[] pemPrivateKey, String password, File localFile, String remoteFilePath)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteFilePath, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteFilePath);

		try {
			// Transfer get file.
			doScpTransfer(host, user, pemPrivateKey, password, scp -> {
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
	public void scpPutFile(String host, String user, char[] pemPrivateKey, String password, File localFile, String remoteDir)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteDir);

		try {
			// Transfer send file.
			doScpTransfer(host, user, pemPrivateKey, password, scp -> {
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
	@Override
	protected void doScpTransfer(String host, String user, char[] pemPrivateKey, String password,
			CallbackFunction<SCPClient> processor) throws Exception {
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
			processor.process(new SCPClient(conn = createSsh2Connection(host, user, pemPrivateKey, password)));
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

	@Override
	public SshExecResponse execWaitForResponse(String host, String user, char[] pemPrivateKey, String password, String command,
			long timeoutMs) throws Exception {
		return execWaitForComplete(host, user, pemPrivateKey, password, command, session -> {
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

	@Override
	public <T> T execWaitForComplete(String host, String user, char[] pemPrivateKey, String password, String command,
			ProcessFunction<Session, T> processor, long timeoutMs) throws Exception {
		return doExecCommand(host, user, pemPrivateKey, password, command, session -> {
			// Wait for completed by condition.
			session.waitForCondition((CLOSED), timeoutMs);
			return processor.process(session);
		});
	}

	@Override
	public final <T> T doExecCommand(String host, String user, char[] pemPrivateKey, String password, String command,
			ProcessFunction<Session, T> processor) throws Exception {
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
			session = (conn = createSsh2Connection(host, user, pemPrivateKey, password)).openSession();
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
	private final Connection createSsh2Connection(String host, String user, char[] pemPrivateKey, String password)
			throws IOException {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		// notNull(pemPrivateKey, "SSH2 command pemPrivateKey must not be
		// null.");
		isTrueOf(!Collections2.isEmptyArray(pemPrivateKey) || StringUtils.isNotBlank(password), "pemPrivateKey");

		Connection conn = new Connection(host);
		conn.connect();

		if (!Collections2.isEmptyArray(pemPrivateKey)) {
			// Authentication with pub-key.
			isTrue(conn.authenticateWithPublicKey(user, pemPrivateKey, null), String
					.format("Failed to SSH2 authenticate with %s@%s privateKey(%s)", user, host, new String(pemPrivateKey)));
		} else {
			isTrue(conn.authenticateWithPassword(user, password),
					String.format("Failed to SSH2 authenticate with %s@%s password(%s)", user, host, password));
		}

		log.debug("SSH2 connected to {}@{}", user, host);
		return conn;
	}

	// --- Tool function's. ---

	@Override
	public Ssh2KeyPair generateKeypair(AlgorithmType type, String comment) throws Exception {
		throw new UnsupportedOperationException();
	}

}