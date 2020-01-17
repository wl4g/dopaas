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
package com.wl4g.devops.shell.handler;

import static org.apache.commons.lang3.StringUtils.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.shell.config.Configuration;
import com.wl4g.devops.shell.message.ExceptionMessage;
import com.wl4g.devops.shell.message.InterruptMessage;
import com.wl4g.devops.shell.message.Message;
import com.wl4g.devops.shell.message.MetaMessage;
import com.wl4g.devops.shell.message.OutputMessage;
import com.wl4g.devops.shell.message.ProgressMessage;

import static com.wl4g.devops.shell.config.DefaultCommandHandlerRegistrar.getSingle;
import static com.wl4g.devops.shell.handler.InternalChannelMessageHandler.BOF;
import static com.wl4g.devops.shell.handler.InternalChannelMessageHandler.EOF;
import static com.wl4g.devops.shell.message.ChannelState.*;
import static java.lang.System.*;

import org.jline.reader.UserInterruptException;

/**
 * Interactive shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class InteractiveClientShellHandler extends AbstractClientShellHandler {

	/** Running statue. */
	private AtomicBoolean running = new AtomicBoolean(false);

	/** Mark the current processing completion status. */
	private volatile boolean completed = true;

	/** Current processing tasks. */
	private Thread task;

	/** Current command line stdin string. */
	private String stdin;

	/** Record command send time-stamp, waiting for timeout processing. */
	private long sentTime = 0L;

	public InteractiveClientShellHandler(Configuration config) {
		super(config);
	}

	@Override
	public void run(String[] args) {
		if (!running.compareAndSet(false, true)) {
			err.println(String.format("Already running of '%s'", this));
			return;
		}

		while (true) {
			try {
				stdin = lineReader.readLine(getPrompt());

				// Debug-mode input 'E' simulates interrupt events
				if (DEBUG && "E".equals(trimToEmpty(stdin))) {
					throw new UserInterruptException(stdin);
				}

				if (!isBlank(stdin) && isLastComplated()) {
					// Submission processing
					task = new Thread(() -> {
						sentTime = currentTimeMillis();
						submitStdin(stdin);
					});
					task.start();

					// Wait completed.
					waitForCompleted(stdin);
				}

			} catch (UserInterruptException e) { // e.g. Ctrl+C interrupt event.
				// No running tasks at present, shutdown directly.
				if (isLastComplated()) {
					shutdown();
				} else {
					// There are still unfinished tasks. Shut down gracefully.
					submitStdin(new InterruptMessage(true));
				}
			} catch (Throwable e) {
				printErr(EMPTY, e);
			} finally {
				if (task != null) {
					task.interrupt();
					task = null;
				}
			}
		}

	}

	@Override
	protected void submitStdin(Object stdin) {
		try {
			super.submitStdin(stdin);
		} catch (IOException e) {
			printErr(EMPTY, e);
		}
	}

	@Override
	protected void postProcessStdout(Object stdout) {
		sentTime = currentTimeMillis();
		boolean isWakeup = false;

		if (stdout instanceof Message) { // Remote command stdout?
			if (stdout instanceof MetaMessage) {
				MetaMessage meta = (MetaMessage) stdout;
				getSingle().merge(meta.getRegistedMethods());
				isWakeup = true;
			} else if (stdout instanceof ExceptionMessage) {
				ExceptionMessage ex = (ExceptionMessage) stdout;
				printErr(EMPTY, ex.getThrowable());
				isWakeup = true;
			} else if (stdout instanceof ProgressMessage) {

			} else if (stdout instanceof OutputMessage) {
				OutputMessage ret = (OutputMessage) stdout;
				if (ret.getState() == NONCE || ret.getState() == COMPLATED) {
					// Wake up lineReader required when output is complete.
					isWakeup = true;
				}
				// Print server result message.
				if (!equalsAny(ret.getContent(), BOF, EOF)) {
					out.println(ret.getContent());
				}
			}
		} else { // Local command stdout?
			isWakeup = true;
		}

		// Print local string message
		if (stdout instanceof CharSequence) {
			out.println(stdout);
		}

		// Wakeup for lineReader watching.
		if (isWakeup) {
			wakeup();
		}

	}

	/**
	 * Wait for completed. </br>
	 * {@link AbstractClientShellHandler#wakeup()}
	 * 
	 * @param stdin
	 * @throws InterruptedException
	 */
	private void waitForCompleted(String stdin) {
		if (DEBUG) {
			out.println(String.format("waitForCompleted: %s, completed: %s", this, completed));
		}
		completed = false;
	}

	/**
	 * Wake-up for lineReader watching. </br>
	 * 
	 * {@link AbstractClientShellHandler#waitForComplished()}
	 */
	private void wakeup() {
		if (DEBUG) {
			out.println(String.format("Wakeup: %s, completed: %s", this, completed));
		}
		completed = true;
	}

	/**
	 * Get the current status prompt.
	 * 
	 * @return
	 */
	private String getPrompt() {
		if (DEBUG) {
			out.println(String.format("getPrompt: %s, completed: %s", this, completed));
		}
		return completed ? getAttributed().toAnsi(lineReader.getTerminal()) : EMPTY;
	}

	/**
	 * Gets the current execution return completion status (waiting for
	 * expiration also indicates completion)
	 * 
	 * @return
	 */
	private boolean isLastComplated() {
		return completed || (currentTimeMillis() - sentTime) >= TIMEOUT;
	}

}