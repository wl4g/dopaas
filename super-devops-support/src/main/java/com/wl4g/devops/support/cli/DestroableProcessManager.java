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
package com.wl4g.devops.support.cli;

import java.io.File;
import java.io.IOException;

import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.support.IllegalProcessStateException;

/**
 * Supports destroable(Interruptible) execution command line process manager.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Beta
public interface DestroableProcessManager {

	/**
	 * Blocking execution multiple-commands, Save to temporary file before
	 * execution.
	 * 
	 * @param multiCommands
	 * @param execFile
	 * @param stdout
	 * @param timeoutMs
	 * @throws IllegalProcessStateException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	default void execFile(String multiCommands, File execFile, File stdout, long timeoutMs)
			throws IllegalProcessStateException, InterruptedException, IOException {
		execFile(null, multiCommands, execFile, stdout, timeoutMs);
	}

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param cmd
	 *            commands
	 * @param stdout
	 *            standard output stream file.
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	default void exec(String cmd, File stdout, long timeoutMs) throws IllegalProcessStateException, IOException {
		exec(null, cmd, null, stdout, timeoutMs);
	}

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param cmd
	 *            commands
	 * @param pwdDir
	 *            process command-line context directory
	 * @param stdout
	 *            standard output stream file.
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	default void exec(String cmd, File pwdDir, File stdout, long timeoutMs) throws IllegalProcessStateException, IOException {
		exec(null, cmd, pwdDir, stdout, timeoutMs);
	}

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param processId
	 *            command-line process ID, If it is not empty, the process
	 *            reference will be saved, which can be used to interrupt the
	 *            execution of commands in a timely manner.
	 * @param cmd
	 *            commands
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws InterruptedException
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	default void exec(String processId, String cmd, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException {
		exec(processId, cmd, null, null, timeoutMs);
	}

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param processId
	 *            command-line process ID, If it is not empty, the process
	 *            reference will be saved, which can be used to interrupt the
	 *            execution of commands in a timely manner.
	 * @param cmd
	 *            commands
	 * @param stdout
	 *            standard output stream file.
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws InterruptedException
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	default void exec(String processId, String cmd, File stdout, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException {
		exec(processId, cmd, null, stdout, timeoutMs);
	}

	/**
	 * Blocking execution multiple-commands, Save to temporary file before
	 * execution.
	 * 
	 * @param processId
	 *            command-line process ID, If it is not empty, the process
	 *            reference will be saved, which can be used to interrupt the
	 *            execution of commands in a timely manner.
	 * @param multiCommands
	 * @param execFile
	 * @param stdout
	 * @param timeoutMs
	 * @throws IllegalProcessStateException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	void execFile(String processId, String multiCommands, File execFile, File stdout, long timeoutMs)
			throws IllegalProcessStateException, InterruptedException, IOException;

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param processId
	 *            command-line process ID, If it is not empty, the process
	 *            reference will be saved, which can be used to interrupt the
	 *            execution of commands in a timely manner.
	 * @param cmd
	 *            commands
	 * @param pwdDir
	 *            process command-line context directory
	 * @param stdout
	 *            standard output stream file.
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	void exec(String processId, String cmd, File pwdDir, File stdout, long timeoutMs)
			throws IllegalProcessStateException, IOException;

	/**
	 * Destroy command-line process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param processId
	 *            Created command-line process ID, must not be empty.
	 * @param timeoutMs
	 *            Destroy process timeout Ms.
	 */
	void destroy(String processId, long timeoutMs);

}