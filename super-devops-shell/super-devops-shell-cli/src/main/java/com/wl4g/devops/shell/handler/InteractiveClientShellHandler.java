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
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.shell.config.Configuration;
import com.wl4g.devops.shell.message.StderrMessage;
import com.wl4g.devops.shell.message.InterruptMessage;
import com.wl4g.devops.shell.message.Message;
import com.wl4g.devops.shell.message.MetaMessage;
import com.wl4g.devops.shell.message.StdoutMessage;
import com.wl4g.devops.shell.message.ConfirmInterruptMessage;
import com.wl4g.devops.shell.message.AskInterruptMessage;
import com.wl4g.devops.shell.message.ProgressMessage;

import static com.wl4g.devops.shell.utils.ShellUtils.*;
import static com.wl4g.devops.tool.common.cli.ProcessUtils.*;
import static com.wl4g.devops.shell.config.DefaultShellHandlerRegistrar.getSingle;
import static com.wl4g.devops.shell.handler.ShellMessageChannel.BOF;
import static com.wl4g.devops.shell.handler.ShellMessageChannel.EOF;
import static com.wl4g.devops.shell.message.ChannelState.*;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Objects.nonNull;

import org.jline.reader.UserInterruptException;

/**
 * Interactive shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class InteractiveClientShellHandler extends AbstractClientShellHandler {

	/** Running status. */
	final private AtomicBoolean running = new AtomicBoolean(false);

	/** Mark the current processing completion status. */
	private volatile boolean completed = true;

	/** Current processing tasks. */
	private Thread stdinTask;

	/** Current command line stdin string. */
	private String stdin;

	/** Record command send time-stamp, waiting for timeout processing. */
	private long lastPayloadCmdWriteTime = 0L;

	public InteractiveClientShellHandler(Configuration config) {
		super(config);
	}

	@Override
	public void run(String[] args) {
		if (!running.compareAndSet(false, true)) {
			err.println(format("Already running of '%s'", this));
			return;
		}

		while (true) {
			try {
				stdin = lineReader.readLine(getPrompt());

				// Stdin 'E' simulates interrupt events, for secondary
				// development, it is convenient to debug in IDE environment.
				if (DEBUG && "E".equals(trimToEmpty(stdin))) {
					throw new UserInterruptException(stdin);
				}

				if (!isBlank(stdin) && isLastComplated()) {
					// Write command.
					stdinTask = new Thread(() -> {
						lastPayloadCmdWriteTime = currentTimeMillis();
						writeStdin(stdin);
					});
					stdinTask.start();

					waitForComplete(stdin);
				}
			} catch (UserInterruptException e) { // e.g. Ctrl+C interrupt event.
				// No running tasks at present, shutdown directly.
				if (isLastComplated()) {
					shutdown();
				} else {
					// There are still unfinished tasks. shutdown gracefully.
					writeStdin(new InterruptMessage(true));
				}
			} catch (Throwable e) {
				printErr(EMPTY, e);
			} finally {
				if (nonNull(stdinTask)) {
					stdinTask.interrupt();
					stdinTask = null;
				}
			}
		}

	}

	@Override
	protected void writeStdin(Object stdin) {
		try {
			super.writeStdin(stdin);
		} catch (IOException e) {
			printErr(EMPTY, e);
		}
	}

	@SuppressWarnings("resource")
	@Override
	protected void postHandleOutput(Object msg) {
		boolean isWakeup = false;

		if (msg instanceof Message) { // Remote command stdout?
			// Meta
			if (msg instanceof MetaMessage) {
				MetaMessage meta = (MetaMessage) msg;
				getSingle().merge(meta.getRegistedMethods());
				isWakeup = true;
			}
			// Exception
			else if (msg instanceof StderrMessage) {
				StderrMessage stderr = (StderrMessage) msg;
				printErr(EMPTY, stderr.getThrowable());
				isWakeup = true;
			}
			// Progress
			else if (msg instanceof ProgressMessage) {
				ProgressMessage pro = (ProgressMessage) msg;
				printProgress(pro.getTitle(), pro.getProgress(), pro.getWhole(), '=');
			}
			// Ask interrupt
			else if (msg instanceof AskInterruptMessage) {
				AskInterruptMessage ask = (AskInterruptMessage) msg;
				// Ask for interrupt?
				lineReader.printAbove(ask.getSubject());
				String confirm = new Scanner(in).next();
				if (DEBUG) {
					out.println("stdin interrupt confirm: " + confirm);
				}
				writeStdin(new ConfirmInterruptMessage(isTrue(trimToEmpty(confirm), false)));
			}
			// Output result
			else if (msg instanceof StdoutMessage) {
				StdoutMessage stdout = (StdoutMessage) msg;
				if (stdout.getState() == NEW || stdout.getState() == COMPLETED) {
					// Wakeup lineReader required when output is complete.
					isWakeup = true;
				}
				// Print from server result message.
				if (!equalsAny(stdout.getContent(), BOF, EOF)) {
					out.println(stdout.getContent());
				}
			}
		} else { // Local command stdout?
			isWakeup = true;
		}

		// Direct print of local command stdout.
		if (msg instanceof CharSequence) {
			out.println(msg);
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
	private void waitForComplete(String stdin) {
		if (DEBUG) {
			out.println(format("waitForCompleted: %s, completed: %s", this, completed));
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
			out.println(format("Wakeup: %s, completed: %s", this, completed));
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
			out.println(format("getPrompt: %s, completed: %s", this, completed));
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
		return completed || (currentTimeMillis() - lastPayloadCmdWriteTime) >= TIMEOUT;
	}

}