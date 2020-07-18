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

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import com.wl4g.devops.components.tools.common.cli.ProcessUtils.DelegateProcess;
import com.wl4g.devops.support.cli.command.DestroableCommand;

/**
 * Local destroable process implements.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-06
 * @since
 */
public final class LocalDestroableProcess extends DestroableProcess {

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