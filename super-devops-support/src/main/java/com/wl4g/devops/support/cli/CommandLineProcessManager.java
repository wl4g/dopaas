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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.exception.ci.StoppedCommandStateException;
import com.wl4g.devops.support.cli.ProcessRepository.ProcessInfo;

/**
 * Commands line process management.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public class CommandLineProcessManager {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Command-line process repository */
	@Autowired
	protected ProcessRepository repository;

	/**
	 * Execution command-line to process.
	 * 
	 * @param processId
	 * @param cmd
	 * @param timeoutMs
	 * @throws IOException
	 * @throws Exception
	 */
	public void exec(Serializable processId, String cmd, long timeoutMs) throws InterruptedException, IOException {
		exec(processId, cmd, null, null, timeoutMs);
	}

	/**
	 * Execution command-line to process.
	 * 
	 * @param processId
	 * @param cmd
	 * @param pwdDir
	 * @param stdout
	 * @param timeoutMs
	 * @throws IOException
	 * @throws Exception
	 */
	public void exec(Serializable processId, String cmd, File pwdDir, File stdout, long timeoutMs)
			throws InterruptedException, IOException {
		notNull(processId, "Execution commands must not be empty");
		hasText(cmd, "Execution commands must not be empty");
		isTrue(timeoutMs > 0, "Command-line timeoutMs must greater than 0");

		// Use input stdout/stderr
		if (isNull(stdout)) {
			cmd = cmd + " > /dev/null 2>&1";
		} else { // Append to stdout file.
			cmd = cmd + " 2>&1 | tee -a " + stdout.getAbsolutePath();
		}
		if (log.isInfoEnabled()) {
			log.info("Execution command: [{}]", cmd);
		}

		// Execution.
		Process ps = null;
		Runtime rt = Runtime.getRuntime();
		List<String> commands = new ArrayList<String>(4) {
			private static final long serialVersionUID = 1L;
			{
				add("/bin/bash");
				add("-c");
			}
		};
		if (nonNull(pwdDir) && pwdDir.exists()) {
			ps = rt.exec(commands.toArray(new String[] {}), null, pwdDir);
		} else {
			ps = rt.exec(commands.toArray(new String[] {}));
		}

		// Registration process.
		repository.register(processId, new ProcessInfo(processId, pwdDir, commands, stdout, ps));

		// Wait for completed.
		ps.waitFor(timeoutMs, TimeUnit.MILLISECONDS);

		int exitCode = ps.exitValue();
		if (exitCode != 0) {
			if (exitCode == 143) {
				throw new StoppedCommandStateException("Manual Stop Task");
			}
			throw new IllegalStateException(String.format("Failed to execution command: [%s], stdout:[%s])", cmd, stdout));
		}
	}

	/**
	 * Destroy command-line process.
	 * 
	 * @param processId
	 */
	public void destroy(Serializable processId) {
		repository.getProcessInfo(processId).getProcess().destroyForcibly();
	}

}
