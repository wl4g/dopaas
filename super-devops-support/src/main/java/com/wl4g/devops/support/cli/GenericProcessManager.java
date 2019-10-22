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

import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static com.wl4g.devops.common.utils.io.FileIOUtils.writeFile;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.exception.support.IllegalProcessStateException;
import com.wl4g.devops.common.exception.support.TimeoutDestroyProcessException;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.cli.destroy.DestroySignal;
import com.wl4g.devops.support.cli.repository.ProcessRepository;
import com.wl4g.devops.support.cli.repository.ProcessRepository.ProcessInfo;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.RunnerProperties;

/**
 * Abstract generic command-line process management implements.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public abstract class GenericProcessManager extends GenericTaskRunner<RunnerProperties> implements ProcessManager {

	final public static long DEFAULT_DESTROY_ROUND_MS = 300L;
	final public static long DEFAULT_MIN_WATCH_MS = 2_00L;
	final public static long DEFAULT_MAX_WATCH_MS = 2_000L;

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Command-line process repository */
	@Autowired
	protected ProcessRepository repository;

	/** JEDIS service. */
	@Autowired
	protected JedisService jedisService;

	/** JEDIS locks manager. */
	@Autowired
	protected JedisLockManager lockManager;

	public GenericProcessManager() {
		super(new RunnerProperties(true));
	}

	@Override
	public void execFile(String processId, String multiCommands, File file, File stdout, long timeoutMs)
			throws IllegalProcessStateException, InterruptedException, IOException {
		writeFile(file, multiCommands, false);
		exec(processId, file.getAbsolutePath(), stdout, timeoutMs);
	}

	@Override
	public void exec(String processId, String cmd, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException {
		exec(processId, cmd, null, null, timeoutMs);
	}

	@Override
	public void exec(String processId, String cmd, File stdout, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException {
		exec(processId, cmd, null, stdout, timeoutMs);
	}

	@Override
	public void exec(String processId, String cmd, File pwdDir, File stdout, long timeoutMs)
			throws IllegalProcessStateException, IOException {
		notNull(processId, "Execution commands must not be empty");
		hasText(cmd, "Execution commands must not be empty");
		isTrue(timeoutMs > 0, "Command-line timeoutMs must greater than 0");

		// Use input (stdout/stderr)
		if (!isNull(stdout)) {
			cmd = cmd + " 2>&1 | tee -a " + stdout.getAbsolutePath();
		} else { // Append to stdout file.
			cmd = cmd + " > /dev/null 2>&1";
		}

		// Execution.
		final String[] commands = { "/bin/bash", "-c", cmd };
		if (log.isInfoEnabled()) {
			log.info("Execution commands: [{}]", asList(commands));
		}

		Process ps = null;
		if (nonNull(pwdDir)) {
			state(pwdDir.exists(), String.format("No such directory for processId(%s), pwdDir:[%s]", processId, pwdDir));
			ps = Runtime.getRuntime().exec(commands, null, pwdDir);
		} else {
			ps = Runtime.getRuntime().exec(commands);
		}

		// Register process.
		repository.register(processId, new ProcessInfo(processId, pwdDir, asList(commands), stdout, ps));

		// Check exited?
		try {
			// Wait for completed.
			ps.waitFor(timeoutMs, TimeUnit.MILLISECONDS);

			int exitCode = ps.exitValue();
			if (exitCode != 0) { // e.g. destroy() was called.
				throw new IllegalProcessStateException(
						String.format("Process(%s) interrupted, exit code for (%s)", processId, exitCode));
			}
		} catch (Throwable ex) {
			throw new IllegalProcessStateException(String.format("Failed to process(%s), commands:[%s], cause: %s", processId,
					asList(commands), getRootCausesString(ex)));
		} finally {
			repository.cleanup(processId); // Cleanup.
		}

	}

	/**
	 * Destroy command-line process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param signal
	 *            Destruction process signal.
	 * @throws TimeoutDestroyProcessException
	 */
	protected void destroy0(DestroySignal signal) throws TimeoutDestroyProcessException {
		notNull(signal, "Destroy signal must not be null.");
		isTrue(signal.getTimeoutMs() >= DEFAULT_DESTROY_ROUND_MS,
				String.format("Destroy timeoutMs must be less than or equal to %s", DEFAULT_DESTROY_ROUND_MS));

		Process process = repository.get(signal.getProcessId()).getProcess();
		if (nonNull(process)) {
			try {
				process.getOutputStream().close();
			} catch (IOException e) {
			}
			try {
				process.getInputStream().close();
			} catch (IOException e) {
			}
			try {
				process.getErrorStream().close();
			} catch (IOException e) {
			}

			// Destroy force.
			for (long i = 0, c = (signal.getTimeoutMs() / DEFAULT_DESTROY_ROUND_MS); (process.isAlive() || i < c); i++) {
				try {
					process.destroyForcibly();
					Thread.sleep(DEFAULT_DESTROY_ROUND_MS);
				} catch (Exception e) {
					log.error("Failed to destory process.", e);
					break;
				}
			}

			// Check destroyed?
			if (process.isAlive()) {
				throw new TimeoutDestroyProcessException(
						String.format("Still not destroyed '%s', destruction handling timeout", signal.getProcessId()));
			}

			// Cleanup
			repository.cleanup(signal.getProcessId());
		}
	}

}
