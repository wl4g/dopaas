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

import com.wl4g.devops.tool.common.collection.RegisteredUnmodifiableMap;
import com.wl4g.devops.tool.common.function.CallbackFunction;
import com.wl4g.devops.tool.common.function.ProcessFunction;
import org.slf4j.Logger;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static org.apache.commons.lang3.SystemUtils.USER_HOME;

/**
 * {@link Ssh2Holders}, generic SSH2 client wrapper tool. </br>
 * Including the implementation of ethz/ssj/ssd.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public abstract class Ssh2Holders<S, F> {
	final protected Logger log = getLogger(getClass());

	/**
	 * Get default {@link Ssh2Holders} instance by provider class.
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final static <T extends Ssh2Holders> T getDefault() {
		return (T) providerRegistry.get(SshjHolder.class);
	}

	/**
	 * Get {@link Ssh2Holders} instance by provider class.
	 * 
	 * @param <T>
	 * @param providerClass
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final static <T extends Ssh2Holders> T getInstance(Class<T> providerClass) {
		return (T) providerRegistry.get(providerClass);
	}

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
	protected abstract void doScpTransfer(String host, String user, char[] pemPrivateKey, CallbackFunction<F> processor)
			throws Exception;

	// --- Execution commands. ---

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
			ProcessFunction<S, T> processor, long timeoutMs) throws Exception;

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
	protected abstract <T> T doExecCommandWithSsh2(String host, String user, char[] pemPrivateKey, String command,
			ProcessFunction<S, T> processor, long timeoutMs) throws Exception;

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

	// --- Tool function's. ---

	/**
	 * Generate keypair of SSH2 based on RSA/DSA/ECDSA.
	 * 
	 * @param type
	 *            Algorithm type(RSA/DSA/ECDSA).
	 * @param comment
	 * @return
	 * @throws Exception
	 */
	public abstract Ssh2KeyPair generateKeypair(AlgorithmType type, String comment) throws Exception;

	/**
	 * {@link Ssh2Holders} provider registry.
	 */
	@SuppressWarnings("rawtypes")
	private final static Map<Class<? extends Ssh2Holders>, Ssh2Holders> providerRegistry = new RegisteredUnmodifiableMap<Class<? extends Ssh2Holders>, Ssh2Holders>(
			new HashMap<>()) {
		{
			putAll(new HashMap<Class<? extends Ssh2Holders>, Ssh2Holders>() {
				private static final long serialVersionUID = 6854310693801773032L;
				{
					put(EthzHolder.class, new EthzHolder());
					put(SshjHolder.class, new SshjHolder());
					put(SshdHolder.class, new SshdHolder());
					put(JschHolder.class, new JschHolder());
				}
			});
		}
	};

	/**
	 * Default IO buffer size.
	 */
	final public static int DEFAULT_TRANSFER_BUFFER = 1024 * 6;

	/**
	 * {@link SshExecResponse}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年1月9日 v1.0.0
	 * @see
	 */
	public final static class SshExecResponse {

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

	/**
	 * {@link Ssh2KeyPair}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年2月4日 v1.0.0
	 * @see
	 */
	public final static class Ssh2KeyPair {

		/** Generate ssh2 privateKey. */
		final private String privateKey;

		/** Generate ssh2 publicKey. */
		final private String publicKey;

		public Ssh2KeyPair(String privateKey, String publicKey) {
			notNullOf(privateKey, "privateKey");
			notNullOf(publicKey, "publicKey");
			this.privateKey = privateKey;
			this.publicKey = publicKey;
		}

		public String getPrivateKey() {
			return privateKey;
		}

		public String getPublicKey() {
			return publicKey;
		}

	}

	/**
	 * {@link AlgorithmType}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年2月4日 v1.0.0
	 * @see
	 */
	public static enum AlgorithmType {
		RSA, DSA, ECDSA
	}

}
