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

import com.wl4g.devops.support.cli.GenericProcessManager;

import java.io.File;

import static java.util.Objects.nonNull;

/**
 * Generic(local) command's wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月5日
 * @since
 */
public class LocalDestroableCommand extends DestroableCommand {
	private static final long serialVersionUID = -5843814202945157321L;

	/** Command of context directory. */
	final private File pwdDir;

	/** Command of stdout local file. */
	private File stdout;

	/** Command of stderr local file. */
	private File stderr;

	/**
	 * Execute append mode of output stdout/stderr to local file to local file.
	 * (default: true)
	 */
	private boolean append = true;

	public LocalDestroableCommand(String command, File pwdDir, long timeoutMs) {
		this(null, command, pwdDir, false, timeoutMs);
	}

	public LocalDestroableCommand(String processId, String command, File pwdDir, long timeoutMs) {
		this(processId, command, pwdDir, true, timeoutMs);
	}

	public LocalDestroableCommand(String processId, String command, File pwdDir, boolean destroable, long timeoutMs) {
		super(processId, command, destroable, timeoutMs);
		// notNull(pwdDir, "Command pwdDir can't null.");
		this.pwdDir = pwdDir;
	}

	public File getPwdDir() {
		return pwdDir;
	}

	public File getStdout() {
		return stdout;
	}

	public boolean hasStdout() {
		return nonNull(stdout);
	}

	public boolean isAppend() {
		return append;
	}

	/**
	 * Setup execute command output stdout/stderr append mode.
	 * 
	 * @param append
	 * @return
	 */
	public LocalDestroableCommand setAppend(boolean append) {
		this.append = append;
		return this;
	}

	/**
	 * Execute local command, standard stream output to local file.</br>
	 * Note: if the standard output is set to flow to a file,
	 * {@link Process#getInputStream()} cannot get the data.
	 * 
	 * @param stdout
	 * @return
	 * @see {@link GenericProcessManager#inputStreamRead0()}
	 */
	public LocalDestroableCommand setStdout(File stdout) {
		// notNull(stdout, "Command stdout can't null.");
		if (nonNull(stdout)) {
			this.stdout = stdout;
		}
		return this;
	}

	public File getStderr() {
		return stderr;
	}

	public boolean hasStderr() {
		return nonNull(stderr);
	}

	/**
	 * Execute local command, error stream output to local file.</br>
	 * Note: if the standard output is set to flow to a file,
	 * {@link Process#getErrorStream()} cannot get the data.
	 * 
	 * @param stderr
	 * @return
	 * @see {@link GenericProcessManager#inputStreamRead0()}
	 */
	public LocalDestroableCommand setStderr(File stderr) {
		// notNull(stderr, "Command stderr can't null.");
		if (nonNull(stderr)) {
			this.stderr = stderr;
		}
		return this;
	}

}