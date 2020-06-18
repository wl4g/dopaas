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
package com.wl4g.devops.support.cli.process;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasText;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.wl4g.devops.support.cli.command.DestroableCommand;

/**
 * Destroable command process wrapper adapter.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-20
 * @since
 */
public abstract class DestroableProcess {

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
	 * 
	 * @see {@link DestroableCommand}
	 */
	private boolean destroable;

	public DestroableProcess(String processId, DestroableCommand command) {
		if (command.isDestroable()) {
			hasText(processId, "Destroable processId can't empty");
		}
		notNull(command, "Destroable command can't null.");
		this.processId = processId;
		this.command = command;
		this.destroable = command.isDestroable();
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

	public DestroableProcess setDestroable(boolean destroable) {
		this.destroable = destroable;
		return this;
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

}