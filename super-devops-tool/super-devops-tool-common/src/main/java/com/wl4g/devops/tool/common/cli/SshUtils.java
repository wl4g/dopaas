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
package com.wl4g.devops.tool.common.cli;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPInputStream;
import ch.ethz.ssh2.SCPOutputStream;
import ch.ethz.ssh2.Session;
import org.slf4j.Logger;

import com.wl4g.devops.tool.common.function.CallbackFunction;
import com.wl4g.devops.tool.common.function.ProcessFunction;

import java.io.*;

import static ch.ethz.ssh2.ChannelCondition.*;
import static com.wl4g.devops.tool.common.io.ByteStreams2.readFullyToString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

/**
 * Remote SSH command process tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class SshUtils {
	final protected static Logger log = getLogger(SshUtils.class);
	final public static int DEFAULT_TRANSFER_BUFFER = 1024 * 6;

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
	public static void scpGetFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteFilePath)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteFilePath, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteFilePath);

		// Use local current user private key for fallback.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getLocalUserSshPrivateKey(host, user);
		}

		try {
			// Transfer get file.
			doSCPTransfer0(host, user, pemPrivateKey, scp -> {
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
	public static void scpPutFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteDir)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteDir);

		// Use local current user private key for fallback.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getLocalUserSshPrivateKey(host, user);
		}

		try {
			// Transfer send file.
			doSCPTransfer0(host, user, pemPrivateKey, scp -> {
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
	 * Get local current user ssh authentication private key.
	 * 
	 * @param host
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private final static char[] getLocalUserSshPrivateKey(String host, String user) throws Exception {
		// Check private key.
		File privateKey = new File(USER_HOME + "/.ssh/id_rsa");
		isTrue(privateKey.exists(), String.format("Not found privateKey for %s", privateKey));

		try (CharArrayWriter cw = new CharArrayWriter(); FileReader fr = new FileReader(privateKey.getAbsolutePath())) {
			char[] buff = new char[256];
			int len = 0;
			while ((len = fr.read(buff)) != -1) {
				cw.write(buff, 0, len);
			}
			return cw.toCharArray();
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
	private final static void doSCPTransfer0(String host, String user, char[] pemPrivateKey,
			CallbackFunction<SCPClient> processor) throws Exception {
		hasText(host, "Transfer host can't empty.");
		hasText(user, "Transfer user can't empty.");
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");
		notNull(processor, "Transfer processor can't null.");

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
	public static SshExecResponse execWithSsh2(String host, String user, char[] pemPrivateKey, String command, long timeoutMs)
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
	@SuppressWarnings("unchecked")
	public static <T> T execWaitForCompleteWithSsh2(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<Session, Object> processor, long timeoutMs) throws Exception {
		return doExecSsh2Command0(host, user, pemPrivateKey, command, session -> {
			// Wait for completed by condition.
			session.waitForCondition((CLOSED | EOF | TIMEOUT), timeoutMs);
			return (T) processor.process(session);
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
	@SuppressWarnings("unchecked")
	private final static <T> T doExecSsh2Command0(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<Session, Object> processor, long timeoutMs) throws Exception {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		notNull(pemPrivateKey, "SSH2 command pemPrivateKey must not be null.");
		notNull(processor, "SSH2 command processor must not be null.");

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
			return (T) processor.process(session);
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
	private final static Connection createSsh2Connection(String host, String user, char[] pemPrivateKey) throws IOException {
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

	public static class SshExecResponse {

		/** Remote commands exit signal. */
		final private String exitSignal;

		/** Remote commands exit code. */
		final private Integer exitCode;

		/** Standard message */
		final private String message;

		/** Error message */
		final private String errmsg;

		public SshExecResponse(String exitSignal, Integer exitCode, String message, String errmsg) {
			super();
			this.exitSignal = exitSignal;
			this.exitCode = exitCode;
			this.message = message;
			this.errmsg = errmsg;
		}

		public String getExitSignal() {
			return exitSignal;
		}

		public Integer getExitCode() {
			return exitCode;
		}

		public String getMessage() {
			return message;
		}

		public String getErrmsg() {
			return errmsg;
		}

	}

}