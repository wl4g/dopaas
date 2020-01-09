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

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;

import com.wl4g.devops.tool.common.function.CallbackFunction;
import com.wl4g.devops.tool.common.function.ProcessFunction;

import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

/**
 * {@link Ssh2Clients}, generic SSH2 client wrapper tool. </br>
 * Including the implementation of ethz/ssj/ssd.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public abstract class Ssh2Clients {
	final protected Logger log = getLogger(getClass());

	// TODO
	public final static Ssh2Clients getInstance() {
		return null;
	}

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
	public abstract void scpGetFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteFilePath)
			throws Exception;

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
	public abstract void scpPutFile(String host, String user, char[] pemPrivateKey, File localFile, String remoteDir)
			throws Exception;

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
	protected abstract void doScpTransfer0(String host, String user, char[] pemPrivateKey,
			CallbackFunction<SCPFileTransfer> processor) throws Exception;

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
	public abstract <T> T execWaitForCompleteWithSsh2(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<Session.Command, T> processor, long timeoutMs) throws Exception;

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
	protected abstract <T> T doExecSsh2Command0(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<Session.Command, T> processor, long timeoutMs) throws Exception;

	/**
	 * Get local current user ssh authentication private key of default.
	 * 
	 * @param host
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected final char[] getDefaultLocalUserPrivateKey() throws Exception {
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
	 * {@link SshExecResponse}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年1月9日 v1.0.0
	 * @see
	 */
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
