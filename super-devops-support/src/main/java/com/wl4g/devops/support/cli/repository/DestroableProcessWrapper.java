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
package com.wl4g.devops.support.cli.repository;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.tool.common.cli.ProcessUtils.DelegateProcess;

import ch.ethz.ssh2.Session;

import static ch.ethz.ssh2.ChannelCondition.CLOSED;
import static ch.ethz.ssh2.ChannelCondition.EOF;
import static ch.ethz.ssh2.ChannelCondition.TIMEOUT;
import static ch.ethz.ssh2.channel.Channel.*;

/**
 * Destroable command process wrapper adapter.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-20
 * @since
 */
public abstract class DestroableProcessWrapper {

	/**
	 * Destroable process as ID.
	 */
	final private String processId;

	/**
	 * Destroable command.
	 */
	final private DestroableCommand command;

	/**
	 * Set whether the current process is allowed to interrupt, for example:
	 * when processing a lengthy task, when some key steps are executed, it can
	 * be set as not to interrupt, which is very useful to ensure the security
	 * of the task.
	 */
	private boolean destroable = true;

	public DestroableProcessWrapper(String processId, DestroableCommand command) {
		// hasText(processId, "Destroable processId can't empty");
		notNull(command, "Destroable command can't null.");
		this.processId = processId;
		this.command = command;
	}

	public String getProcessId() {
		return processId;
	}

	public DestroableCommand getCommand() {
		return command;
	}

	public boolean isDestroable() {
		return destroable;
	}

	public void setDestroable(boolean destroable) {
		this.destroable = destroable;
	}

	/**
	 * Get Output stream connected to normal input of subprocess
	 * 
	 * @return
	 */
	public abstract OutputStream getStdin();

	/**
	 * Get The input stream connected to the normal output of the subprocess
	 * 
	 * @return
	 */
	public abstract InputStream getStdout();

	/**
	 * Get Input stream for error output connected to subprocesses
	 * 
	 * @return
	 */
	public abstract InputStream getStderr();

	/**
	 * Check if the command processing process is active (make sure it is not
	 * closed)
	 * 
	 * @return
	 */
	public abstract boolean isAlive();

	/**
	 * Force destroy command process.
	 */
	public abstract void destoryForcibly();

	/**
	 * Wait for the command processing process to execute for a certain time
	 * 
	 * @param timeout
	 * @param unit
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void waitFor(long timeout, TimeUnit unit) throws IOException, InterruptedException;

	/**
	 * Get the status code returned by the command processing process (Note:
	 * different current system implementations may be different, such as
	 * forgetting to return the status code, so it may be null)
	 * 
	 * @return
	 */
	public abstract Integer exitValue();

	/**
	 * Local destroable process implements.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-12-06
	 * @since
	 */
	public final static class LocalDestroableProcess extends DestroableProcessWrapper {

		/**
		 * Execution process, {@link DelegateProcess}
		 */
		final private Process process;

		public LocalDestroableProcess(String processId, DestroableCommand command, Process process) {
			super(processId, command);
			notNull(process, "Command process can't null.");
			this.process = process;
		}

		@Override
		public OutputStream getStdin() {
			return process.getOutputStream();
		}

		@Override
		public InputStream getStdout() {
			return process.getInputStream();
		}

		@Override
		public InputStream getStderr() {
			return process.getErrorStream();
		}

		@Override
		public boolean isAlive() {
			return process.isAlive();
		}

		@Override
		public void destoryForcibly() {
			process.destroyForcibly();
		}

		@Override
		public void waitFor(long timeout, TimeUnit unit) throws IOException, InterruptedException {
			process.waitFor(timeout, TimeUnit.MILLISECONDS);
		}

		@Override
		public Integer exitValue() {
			return process.exitValue();
		}

	}

	/**
	 * Remote destroable process implements.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-12-06
	 * @since
	 */
	public final static class RemoteDestroableProcess extends DestroableProcessWrapper {

		/**
		 * Execution remote process of session {@link Session}
		 */
		final private Session session;

		public RemoteDestroableProcess(String processId, DestroableCommand command, Session session) {
			super(processId, command);
			notNull(session, "Command remote process session can't null.");
			this.session = session;
		}

		@Override
		public OutputStream getStdin() {
			return session.getStdin();
		}

		@Override
		public InputStream getStdout() {
			return session.getStdout();
		}

		@Override
		public InputStream getStderr() {
			return session.getStderr();
		}

		@Override
		public boolean isAlive() {
			return session.getState() == STATE_OPEN;
		}

		@Override
		public void destoryForcibly() {
			session.close();
		}

		@Override
		public void waitFor(long timeout, TimeUnit unit) throws IOException, InterruptedException {
			// Wait for completed by condition.
			session.waitForCondition((CLOSED | EOF | TIMEOUT), timeout);
		}

		@Override
		public Integer exitValue() {
			return session.getExitStatus();
		}

	}

}