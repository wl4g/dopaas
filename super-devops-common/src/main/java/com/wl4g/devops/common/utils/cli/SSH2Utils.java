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
package com.wl4g.devops.common.utils.cli;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SCPOutputStream;
import ch.ethz.ssh2.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static ch.ethz.ssh2.ChannelCondition.*;
import static com.wl4g.devops.common.utils.io.ByteStreams2.unsafeReadFullyToString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;
import static org.springframework.util.Assert.*;

/**
 * SSH connection utility tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class SSH2Utils {
	final protected static Logger log = LoggerFactory.getLogger(SSH2Utils.class);

	final public static int DEFAULT_TRANSFER_BUF = 1024 * 8;

	/**
	 * Transfer file to remote host directory.
	 * 
	 * @param host
	 * @param user
	 * @param localFile
	 * @param remoteDir
	 * @throws IOException
	 */
	public static void transferFile(String host, String user, File localFile, String remoteDir) throws IOException {
		hasText(host, "Transfer host must not be empty.");
		hasText(user, "Transfer user must not be empty.");
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir must not be empty.");

		// Check private key.
		File defaultKey = new File(USER_HOME + "/.ssh/id_rsa");
		isTrue(defaultKey.exists(), String.format("Not found privateKey for %s", defaultKey));

		try (CharArrayWriter cw = new CharArrayWriter(); FileReader fr = new FileReader(defaultKey.getAbsolutePath())) {
			char[] buff = new char[256];
			int len = 0;
			while ((len = fr.read(buff)) != -1) {
				cw.write(buff, 0, len);
			}

			// Transfer file.
			transferFile(host, user, cw.toCharArray(), localFile, remoteDir);
		}
	}

	/**
	 * Transfer file to remote host directory.
	 * 
	 * @param host
	 * @param user
	 * @param pemPrivateKey
	 * @param localFile
	 * @param remoteDir
	 * @throws IOException
	 */
	public static void transferFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteDir)
			throws IOException {
		hasText(host, "Transfer host must not be empty.");
		hasText(user, "Transfer user must not be empty.");
		notNull(pemPrivateKey, "Transfer pemPrivateKey must not be null.");
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir must not be empty.");
		if (log.isDebugEnabled()) {
			log.debug("Ssh2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteDir);
		}

		Connection conn = null;
		SCPOutputStream sos = null;
		FileInputStream fis = null;
		try {
			conn = new Connection(host);
			if (log.isDebugEnabled()) {
				log.debug("SSH2 scp connect to {}@{}", user, host);
			}
			// Connect.
			conn.connect();

			// Authentication with pub-key.
			isTrue(conn.authenticateWithPublicKey(user, pemPrivateKey, null), String
					.format("Failed to SSH2 authenticate with %s@%s privateKey(%s)", user, host, new String(pemPrivateKey)));

			// Transfer send file.
			SCPClient scp = new SCPClient(conn);

			sos = scp.put(localFile.getName(), localFile.length(), remoteDir, "0744");

			fis = new FileInputStream(localFile);
			int i = 0;
			byte[] buf = new byte[DEFAULT_TRANSFER_BUF];
			while ((i = fis.read(buf)) != -1) {
				sos.write(buf, 0, i);
			}
			sos.flush();

			if (log.isDebugEnabled()) {
				log.debug("SSH2 scp successful for => {} to {}", localFile.getAbsolutePath(), remoteDir);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (nonNull(fis)) {
					fis.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
			try {
				if (nonNull(sos)) {
					sos.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
			try {
				if (nonNull(conn)) {
					conn.close();
				}
			} catch (Exception e) {
				log.error("", e);
			}
		}

	}

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
	public static CommandResult executeWithCommand(String host, String user, char[] pemPrivateKey, String command, long timeoutMs)
			throws IOException {
		return executeWithCommand(host, user, pemPrivateKey, command, (s) -> {
			String message = null, errmsg = null;
			if (nonNull(s.getStdout())) {
				message = unsafeReadFullyToString(s.getStdout());
			}
			if (nonNull(s.getStderr())) {
				errmsg = unsafeReadFullyToString(s.getStderr());
			}
			return new CommandResult(s.getExitSignal(), s.getExitStatus(), message, errmsg);
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
	public static <T> T executeWithCommand(String host, String user, char[] pemPrivateKey, String command,
			CommandProcessor processor, long timeoutMs) throws IOException {
		hasText(host, "SSH2 command host must not be empty.");
		hasText(user, "SSH2 command user must not be empty.");
		notNull(pemPrivateKey, "SSH2 command pemPrivateKey must not be null.");
		notNull(processor, "SSH2 command processor must not be null.");

		Connection conn = null;
		Session session = null;
		try {
			conn = new Connection(host);
			if (log.isDebugEnabled()) {
				log.debug("SSH2 command connect to {}@{}", user, host);
			}
			// Connect.
			conn.connect();

			// Authentication with pub-key.
			isTrue(conn.authenticateWithPublicKey(user, pemPrivateKey, null), String
					.format("Failed to SSH2 authenticate with %s@%s privateKey(%s)", user, host, new String(pemPrivateKey)));

			// Session & send command.
			session = conn.openSession();
			if (log.isInfoEnabled()) {
				log.info("SSH2 sending command to {}@{}, ({})", user, host, command);
			}
			session.execCommand(command, "UTF-8");

			// Wait for completed by condition.
			session.waitForCondition((CLOSED | EOF | TIMEOUT), timeoutMs);

			// Customize process.
			return (T) processor.process(session);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (nonNull(session)) {
					session.close();
				}
			} catch (Exception e2) {
				log.error("", e2);
			}
			try {
				if (nonNull(conn)) {
					conn.close();
				}
			} catch (Exception e2) {
				log.error("", e2);
			}
		}

	}

	/**
	 * SSH2 commands processor
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月23日
	 * @since
	 */
	public static interface CommandProcessor {
		Object process(Session session);
	}

	/**
	 * Commands result wrapper.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月23日
	 * @since
	 */
	public static class CommandResult {

		/** Remote commands exit signal. */
		final private String exitSignal;

		/** Remote commands exit code. */
		final private Integer exitCode;

		/** Standard message */
		final private String message;

		/** Error message */
		final private String errmsg;

		public CommandResult(String exitSignal, Integer exitCode, String message, String errmsg) {
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