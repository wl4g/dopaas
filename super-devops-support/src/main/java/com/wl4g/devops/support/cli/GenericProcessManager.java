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

import com.wl4g.devops.common.exception.support.IllegalProcessStateException;
import com.wl4g.devops.common.exception.support.NoSuchProcessException;
import com.wl4g.devops.common.exception.support.TimeoutDestroyProcessException;
import com.wl4g.devops.components.tools.common.cli.ssh2.EthzHolder;
import com.wl4g.devops.components.tools.common.cli.ssh2.SSH2Holders;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.tools.common.task.RunnerProperties;
import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.support.cli.command.LocalDestroableCommand;
import com.wl4g.devops.support.cli.command.RemoteDestroableCommand;
import com.wl4g.devops.support.cli.destroy.DestroySignal;
import com.wl4g.devops.support.cli.process.DestroableProcess;
import com.wl4g.devops.support.cli.process.LocalDestroableProcess;
import com.wl4g.devops.support.cli.process.RemoteDestroableProcess;
import com.wl4g.devops.support.cli.repository.ProcessRepository;
import com.wl4g.devops.support.task.ApplicationTaskRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static com.wl4g.devops.components.tools.common.cli.ProcessUtils.*;
import static com.wl4g.devops.components.tools.common.io.ByteStreamUtils.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.devops.components.tools.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.lang.Thread.sleep;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Abstract generic command-line process management implements.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
public abstract class GenericProcessManager extends ApplicationTaskRunner<RunnerProperties> implements DestroableProcessManager {

	final protected SmartLogger log = getLogger(getClass());

	/** Command-line process repository */
	final protected ProcessRepository repository;

	public GenericProcessManager(ProcessRepository repository) {
		super(new RunnerProperties(false, 1));
		notNull(repository, "Process repository can't null");
		this.repository = repository;
	}

	@Override
	public String execWaitForComplete(DestroableCommand cmd)
			throws IllegalProcessStateException, InterruptedException, Exception {
		notNull(cmd, "Execution command can't null.");

		DestroableProcess dp = null;
		if (cmd instanceof LocalDestroableCommand) {
			dp = doExecLocal((LocalDestroableCommand) cmd);
		} else if (cmd instanceof RemoteDestroableCommand) {
			dp = doExecRemote((RemoteDestroableCommand) cmd);
		} else {
			throw new UnsupportedOperationException(String.format("Unsupported DestroableCommand[%s]", cmd));
		}
		notNull(dp, "Process not created? An unexpected error!");

		// Register process if necessary.
		if (!isBlank(cmd.getProcessId())) {
			repository.register(cmd.getProcessId(), dp);
		}

		// Check exited?
		try {
			// Wait for completed.
			dp.waitFor(cmd.getTimeoutMs(), TimeUnit.MILLISECONDS);

			// Waiting be completed.
			Integer exitValue = null;
			try {
				exitValue = dp.exitValue(); // [Note]: exitCode may be null
			} catch (IllegalThreadStateException e) {
				throw new IllegalProcessStateException(exitValue,
						format("Exec process timeout for: %sMs, %s", cmd.getTimeoutMs(), e.getMessage()));
			}
			if (isNull(exitValue)) {
				// [Fallback]: If the output is not redirected to the local
				// file, the execution fails if there is an stderr message
				if (!isLocalStderr(cmd)) {
					String errmsg = readFullyToString(dp.getStderr());
					if (!isBlank(errmsg)) {
						throw new IllegalProcessStateException(errmsg);
					}
				}
			} else if (exitValue != 0) {
				String errmsg = EMPTY;
				try {
					// stderr redirected? (e.g: mvn install >/mvn.out 2>&1)
					// and will not get the message.
					if (isLocalStderr(cmd))
						errmsg = format("Could't exec command, more error info refer to: '%s'",
								((LocalDestroableCommand) cmd).getStderr());
					else
						errmsg = readFullyToString(dp.getStderr());
				} catch (Exception e) {
					errmsg = getRootCausesString(e);
				}
				throw new IllegalProcessStateException(exitValue, errmsg);
			}

			return readFullyToString(dp.getStdout());
		} catch (IllegalProcessStateException ex) {
			throw new IllegalProcessStateException(ex.getExitValue(),
					format("Failed to process(%s), commands: [%s], cause by: %s", cmd.getProcessId(), dp.getCommand().getCmd(),
							getStackTraceAsString(ex)));
		} finally {
			// Destroy process.
			destroy0(dp, DEFAULT_DESTROY_TIMEOUTMS);

			// Cleanup if necessary.
			if (!isBlank(cmd.getProcessId())) {
				repository.cleanup(cmd.getProcessId());
			}
		}
	}

	@Override
	public void exec(DestroableCommand cmd, Executor executor, ProcessCallback callback) throws Exception, InterruptedException {
		notNull(cmd, "Execution command can't null.");
		notNull(executor, "Process excutor can't null.");
		notNull(callback, "Process callback can't null.");

		DestroableProcess dp = null;
		if (cmd instanceof DestroableCommand) {
			dp = doExecLocal((LocalDestroableCommand) cmd);
		} else if (cmd instanceof RemoteDestroableCommand) {
			dp = doExecRemote((RemoteDestroableCommand) cmd);
		} else {
			throw new UnsupportedOperationException(String.format("Unsupported DestroableCommand[%s]", cmd));
		}
		notNull(dp, "Process not created? An unexpected error!");

		// Register process if necessary.
		if (!isBlank(cmd.getProcessId())) {
			repository.register(cmd.getProcessId(), dp);
		}

		// Stderr/Stdout stream process.
		CountDownLatch latch = new CountDownLatch(2);
		try {
			inputStreamRead0(dp.getStderr(), executor, latch, callback, dp, true);
			inputStreamRead0(dp.getStdout(), executor, latch, callback, dp, false);
			latch.await(cmd.getTimeoutMs(), TimeUnit.MILLISECONDS); // Await-done
		} finally {
			// Destroy process.
			destroy0(dp, DEFAULT_DESTROY_TIMEOUTMS);
			// Cleanup if necessary.
			if (!isBlank(cmd.getProcessId())) {
				repository.cleanup(cmd.getProcessId());
			}
		}
	}

	/**
	 * Execution local command line, callback standard or exception output
	 * 
	 * @param cmd
	 * @throws InterruptedException
	 * @throws Exception
	 */
	protected DestroableProcess doExecLocal(LocalDestroableCommand cmd) throws InterruptedException, Exception {
		log.info("Exec local command: {}", cmd.getCmd());

		DelegateProcess ps = execMulti(cmd.getCmd(), cmd.getPwdDir(), cmd.getStdout(), cmd.getStderr(), cmd.isAppend(), false);
		return new LocalDestroableProcess(cmd.getProcessId(), cmd, ps);
	}

	/**
	 * Execution remote command line, callback standard or exception output
	 * 
	 * @param cmd
	 * @throws InterruptedException
	 * @throws Exception
	 */
	protected DestroableProcess doExecRemote(RemoteDestroableCommand cmd) throws InterruptedException, Exception {
		log.info("Exec remote command: {}", cmd.getCmd());

		return SSH2Holders.getInstance(EthzHolder.class).execWaitForComplete(cmd.getHost(), cmd.getUser(),
				cmd.getPemPrivateKey(), cmd.getPassword(), cmd.getCmd(), s -> new RemoteDestroableProcess(cmd.getProcessId(), cmd, s),
				cmd.getTimeoutMs());
	}

	@Override
	public void setDestroable(String processId, boolean destroable) throws NoSuchProcessException {
		repository.setDestroable(processId, destroable);
	}

	/**
	 * Destroy command-line process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param signal
	 *            Destruction process signal.
	 * @throws TimeoutDestroyProcessException
	 * @see {@link ch.ethz.ssh2.Session#close()}
	 * @see {@link ch.ethz.ssh2.channel.ChannelManager#closeChannel(Channel,String,boolean)}
	 */
	protected void doDestroy(DestroySignal signal) throws TimeoutDestroyProcessException {
		notNull(signal, "Destroy signal must not be null.");
		isTrue(signal.getTimeoutMs() >= DEFAULT_DESTROY_INTERVALMS,
				String.format("Destroy timeoutMs must be less than or equal to %s", DEFAULT_DESTROY_INTERVALMS));

		// Process wrapper.
		DestroableProcess dpw = repository.get(signal.getProcessId());
		// Check destroable.
		state(dpw.isDestroable(),
				String.format("Failed to destroy command process: (%s), because the current destroable state: %s",
						signal.getProcessId(), dpw.isDestroable()));

		// Destroy process.
		if (nonNull(dpw)) {
			destroy0(dpw, signal.getTimeoutMs());
			repository.cleanup(signal.getProcessId()); // Cleanup
		} else {
			log.warn("Failed to destroy because processId: {} does not exist or has been destroyed!", signal.getProcessId());
		}

	}

	/**
	 * Destroy process streams(IN/ERR {@link InputStream} and
	 * {@link OutputStream}).
	 * 
	 * @param timeoutMs
	 */
	private void destroy0(DestroableProcess dpw, long timeoutMs) {
		notNull(dpw, "Destroable process can't null.");
		try {
			dpw.getStdin().close();
		} catch (IOException e) {
			log.error("Failed to stdin stream close", e);
		}
		try {
			dpw.getStdout().close();
		} catch (IOException e) {
			log.error("Failed to stdout stream close", e);
		}
		try {
			dpw.getStderr().close();
		} catch (IOException e) {
			log.error("Failed to stderr stream close", e);
		}

		// Destroy force.
		for (long i = 0, c = (timeoutMs / DEFAULT_DESTROY_INTERVALMS); (dpw.isAlive() && i < c); i++) {
			try {
				dpw.destoryForcibly();
				if (dpw.isAlive()) { // Failed destroy?
					sleep(DEFAULT_DESTROY_INTERVALMS);
				}
			} catch (Exception e) {
				log.error("Failed to destory process.", e);
				break;
			}
		}

		// Assertion destroyed?
		if (dpw.isAlive()) {
			throw new TimeoutDestroyProcessException(
					String.format("Still not destroyed '%s', handling timeout", dpw.getCommand().getProcessId()));
		}
	}

	/**
	 * Submit read {@link InputStream} process task.
	 * 
	 * @param in
	 * @param executor
	 * @param latch
	 * @param callback
	 * @param dpw
	 * @param iserr
	 * @throws IOException
	 */
	private void inputStreamRead0(InputStream in, Executor executor, CountDownLatch latch, ProcessCallback callback,
			DestroableProcess dpw, boolean iserr) {
		notNull(dpw, "DestroableProcess can't null.");
		notNull(in, "Process inputStream can't null");
		notNull(callback, "Process callback can't null");

		executor.execute(() -> {
			try {
				int len = 0;
				byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
				while (dpw.isAlive() && ((len = in.read(buf)) != -1)) {
					byte[] data = new byte[len];
					arraycopy(buf, 0, data, 0, len);
					if (iserr) {
						callback.onStderr(data);
					} else {
						callback.onStdout(data);
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				latch.countDown();
			}
		});
	}

	/**
	 * Check {@link LocalDestroableCommand} and has stderr file.
	 * 
	 * @param command
	 * @return
	 */
	private boolean isLocalStderr(DestroableCommand command) {
		return (command instanceof LocalDestroableCommand) && ((LocalDestroableCommand) command).hasStderr();
	}

	final public static long DEFAULT_DESTROY_INTERVALMS = 200L;
	final public static long DEFAULT_DESTROY_TIMEOUTMS = 30 * 1000L;
	final public static int DEFAULT_BUFFER_SIZE = 1024 * 4;

}