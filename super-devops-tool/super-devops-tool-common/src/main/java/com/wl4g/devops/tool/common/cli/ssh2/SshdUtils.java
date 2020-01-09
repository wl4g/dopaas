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

import com.wl4g.devops.tool.common.function.CallbackFunction;
import com.wl4g.devops.tool.common.function.ProcessFunction;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.scp.DefaultScpClientCreator;
import org.apache.sshd.client.scp.ScpClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.slf4j.Logger;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

/**
 * Remote SSH command process tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class SshdUtils {
	final protected static Logger log = getLogger(SshdUtils.class);

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
	public static void scpGetFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteFilePath)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteFilePath, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteFilePath);

		try {
			// Transfer get file.
			doScpTransfer0(host, user, pemPrivateKey, scp -> {
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
	public static void scpPutFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteDir)
			throws Exception {
		notNull(localFile, "Transfer localFile must not be null.");
		hasText(remoteDir, "Transfer remoteDir can't empty.");
		log.debug("SSH2 transfer file from {} to {}@{}:{}", localFile.getAbsolutePath(), user, host, remoteDir);

		try {
			// Transfer send file.
			doScpTransfer0(host, user, pemPrivateKey, scp -> {
				scp.upload(localFile.getAbsolutePath(), remoteDir);
			});

			log.debug("SCP put transfered: '{}' to '{}@{}:{}'", localFile.getAbsolutePath(), user, host, remoteDir);
		} catch (IOException e) {
			throw e;
		}

	}

	/**
	 * Get local current user ssh authentication private key of default.
	 * 
	 * @param host
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private final static char[] getDefaultLocalUserPrivateKey() throws Exception {
		// Check private key.
		File privateKeyFile = new File(USER_HOME + "/.ssh/id_rsa");
		isTrue(privateKeyFile.exists(), String.format("Not found privateKey for %s", privateKeyFile));

		log.warn("Fallback use local user pemPrivateKey of: {}", privateKeyFile);
		try (CharArrayWriter cw = new CharArrayWriter(); FileReader fr = new FileReader(privateKeyFile.getAbsolutePath())) {
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
	private final static void doScpTransfer0(String host, String user, char[] pemPrivateKey,
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
			session = authWithPrivateKey(client, host, null, user, pemPrivateKey);
			scpClient = DefaultScpClientCreator.INSTANCE.createScpClient(session);
			processor.process(scpClient);
		} catch (Exception e) {
			e.printStackTrace();
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
		return execWaitForCompleteWithSsh2(host, user, pemPrivateKey, command, channelExec -> {
			String message = null, errmsg = null;
			if (nonNull(channelExec.getOut())) {
				// message = readFullyToString();
				message = channelExec.getOut().toString();
			}
			if (nonNull(channelExec.getErr())) {
				errmsg = channelExec.getErr().toString();
			}
			return new SshExecResponse(channelExec.getExitSignal(), channelExec.getExitStatus(), message, errmsg);
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
	public static <T> T execWaitForCompleteWithSsh2(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<ChannelExec, T> processor, long timeoutMs) throws Exception {
		return doExecSsh2Command0(host, user, pemPrivateKey, command, channelExec -> {
			// Wait for completed by condition.
			channelExec.waitFor(Collections.singleton(ClientChannelEvent.CLOSED), timeoutMs);
			return processor.process(channelExec);
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
	private final static <T> T doExecSsh2Command0(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<ChannelExec, T> processor, long timeoutMs) throws Exception {
		hasText(host, "SSH2 command host can't empty.");
		hasText(user, "SSH2 command user can't empty.");
		notNull(processor, "SSH2 command processor can't null.");

		// Fallback uses the local current user private key by default.
		if (isNull(pemPrivateKey)) {
			pemPrivateKey = getDefaultLocalUserPrivateKey();
		}
		notNull(pemPrivateKey, "Transfer pemPrivateKey can't null.");

		ClientSession session = null;
		ChannelExec channelExec = null;
		SshClient client = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		try {
			client = SshClient.setUpDefaultClient();
			client.start();
			session = authWithPrivateKey(client, host, null, user, pemPrivateKey);
			String proCommond = "source /etc/profile\nsource /etc/bashrc\n";
			channelExec = session.createExecChannel(proCommond + command);
			channelExec.setErr(err);
			channelExec.setOut(out);
			channelExec.open();
			return processor.process(channelExec);
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();
			try {
				if (nonNull(channelExec)) {
					channelExec.close();
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
	private static ClientSession authWithPassword(SshClient client, String host, Integer port, String user, String password)
			throws IOException, GeneralSecurityException {
		ClientSession session = client.connect(user, host, Objects.isNull(port) ? 22 : port).verify(10000).getSession();
		session.addPasswordIdentity(password); // for password-based
												// authentication
		AuthFuture verify = session.auth().verify(10000);
		if (!verify.isSuccess()) {
			throw new GeneralSecurityException("auth fail");
		}
		return session;
	}

	private static ClientSession authWithPrivateKey(SshClient client, String host, Integer port, String user,
			char[] pemPrivateKey) throws IOException, GeneralSecurityException {
		ClientSession session = client.connect(user, host, Objects.isNull(port) ? 22 : port).verify(10000).getSession();
		Iterable<KeyPair> keyPairs = SecurityUtils.loadKeyPairIdentities(session, null, getStrToStream(new String(pemPrivateKey)),
				null);
		Iterator<KeyPair> iterator = keyPairs.iterator();
		if (iterator.hasNext()) {
			KeyPair next = iterator.next();
			session.addPublicKeyIdentity(next);// for password-less
												// authentication
		}
		AuthFuture verify = session.auth().verify(10000);
		if (!verify.isSuccess()) {
			throw new GeneralSecurityException("auth fail");
		}
		return session;
	}

	private static InputStream getStrToStream(String sInputString) {
		if (sInputString != null && !sInputString.trim().equals("")) {
			return new ByteArrayInputStream(sInputString.getBytes());
		}
		return null;
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