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
package com.wl4g.devops.support.cli.command;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

/**
 * SSH remote command's wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月5日
 * @since
 */
public class RemoteDestroableCommand extends DestroableCommand {
	private static final long serialVersionUID = -5843814202945157321L;

	/** Remote host user name to execute the remote command. */
	final private String user;

	/** Remote host user name to execute the remote host address. */
	final private String host;

	/** SSH2 (public key) of the remote host. */
	final private char[] pemPrivateKey;

	final private String password;

	public RemoteDestroableCommand(String command, long timeoutMs, String user, String host, char[] pemPrivateKey) {
		this(null, command, false, timeoutMs, user, host, pemPrivateKey,null);
	}

	public RemoteDestroableCommand(String command, boolean destroable, long timeoutMs, String user, String host,
			char[] pemPrivateKey) {
		this(null, command, destroable, timeoutMs, user, host, pemPrivateKey,null);
	}

	public RemoteDestroableCommand(String processId, String command, long timeoutMs, String user, String host,
			char[] pemPrivateKey) {
		this(processId, command, true, timeoutMs, user, host, pemPrivateKey,null);
	}

	public RemoteDestroableCommand(String command, long timeoutMs, String user, String host, String  passrod) {
		this(null, command, false, timeoutMs, user, host, null,passrod);
	}

	public RemoteDestroableCommand(String processId, String command, boolean destroable, long timeoutMs, String user, String host,
			char[] pemPrivateKey,String password) {
		super(processId, command, destroable, timeoutMs);
		hasText(user, "Command remote user can't empty.");
		hasText(host, "Command remote host can't empty.");
		isTrue(Objects.nonNull(pemPrivateKey) || StringUtils.isNotBlank(password),"Command remote ssh pubkey or passrod can't empty.");
		this.user = user;
		this.host = host;
		this.pemPrivateKey = pemPrivateKey;
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public String getHost() {
		return host;
	}

	public char[] getPemPrivateKey() {
		return pemPrivateKey;
	}

	public String getPassword() {
		return password;
	}
}