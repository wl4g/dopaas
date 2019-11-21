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
import java.util.concurrent.Executor;

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
	 * Sync blocking execution multiple-commands, Save to temporary file before
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
	default void execFileSync(String multiCommands, File execFile, File stdout, long timeoutMs)
			throws IllegalProcessStateException, InterruptedException, IOException {
		execFileSync(null, multiCommands, execFile, stdout, timeoutMs);
	}

	/**
	 * Sync blocking execution command-line to process.
	 * 
	 * @param cmd
	 *            commands
	 * @param stdout
	 *            standard output stream file.
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	default void execSync(String cmd, File stdout, long timeoutMs)
			throws IllegalProcessStateException, IOException, InterruptedException {
		execSync(null, cmd, null, stdout, timeoutMs);
	}

	/**
	 * Sync blocking execution command-line to process.
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
	 * @throws InterruptedException
	 */
	default void exec(String cmd, File pwdDir, File stdout, long timeoutMs)
			throws IllegalProcessStateException, IOException, InterruptedException {
		execSync(null, cmd, pwdDir, stdout, timeoutMs);
	}

	/**
	 * Sync blocking execution command-line to process.
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
	default void execSync(String processId, String cmd, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException {
		execSync(processId, cmd, null, null, timeoutMs);
	}

	/**
	 * Sync blocking execution command-line to process.
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
	default void execSync(String processId, String cmd, File stdout, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException {
		execSync(processId, cmd, null, stdout, timeoutMs);
	}

	/**
	 * Sync blocking execution multiple-commands, Save to temporary file before
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
	void execFileSync(String processId, String multiCommands, File execFile, File stdout, long timeoutMs)
			throws IllegalProcessStateException, InterruptedException, IOException;

	/**
	 * Sync blocking execution command-line to process.
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
	 * @throws InterruptedException
	 */
	void execSync(String processId, String cmd, File pwdDir, File stdout, long timeoutMs)
			throws IllegalProcessStateException, IOException, InterruptedException;

	/**
	 * Async execution command.
	 * 
	 * @param processId
	 * @param cmd
	 * @param executor
	 * @param callback
	 * @param timeoutMs
	 * @throws IOException
	 * @throws InterruptedException
	 */
	default void execAsync(String processId, String cmd, Executor executor, ProcessCallback callback, long timeoutMs)
			throws IOException, InterruptedException {
		execAsync(processId, cmd, null, executor, callback, timeoutMs);
	}

	/**
	 * Async execution command.
	 * 
	 * @param processId
	 * @param cmd
	 * @param pwdDir
	 * @param executor
	 * @param callback
	 * @param timeoutMs
	 * @throws IOException
	 * @throws InterruptedException
	 */
	void execAsync(String processId, String cmd, File pwdDir, Executor executor, ProcessCallback callback, long timeoutMs)
			throws IOException, InterruptedException;

	/**
	 * Destroy command process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param processId
	 *            Created command-line process ID, must not be empty.
	 * @param timeoutMs
	 *            Destroy process timeout Ms.
	 */
	void destroy(String processId, long timeoutMs);

	/**
	 * Async execution command process callback.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月21日
	 * @since
	 */
	public static interface ProcessCallback {

		/**
		 * Stdout process.
		 * 
		 * @param data
		 */
		void onStdout(byte[] data);

		/**
		 * Stderr process.
		 * 
		 * @param data
		 */
		void onStderr(byte[] err);

	}

}