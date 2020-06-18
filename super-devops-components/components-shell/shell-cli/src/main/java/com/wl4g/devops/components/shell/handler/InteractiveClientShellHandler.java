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
package com.wl4g.devops.components.shell.handler;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.components.shell.config.Configuration;
import com.wl4g.devops.components.shell.signal.AckInterruptSignal;
import com.wl4g.devops.components.shell.signal.AskInterruptSignal;
import com.wl4g.devops.components.shell.signal.BOFStdoutSignal;
import com.wl4g.devops.components.shell.signal.EOFStdoutSignal;
import com.wl4g.devops.components.shell.signal.MetaSignal;
import com.wl4g.devops.components.shell.signal.PreInterruptSignal;
import com.wl4g.devops.components.shell.signal.ProgressSignal;
import com.wl4g.devops.components.shell.signal.Signal;
import com.wl4g.devops.components.shell.signal.StderrSignal;
import com.wl4g.devops.components.shell.signal.StdoutSignal;

import static com.wl4g.devops.components.shell.cli.BuiltInCommand.*;
import static com.wl4g.devops.components.shell.config.DefaultShellHandlerRegistrar.getSingle;
import static com.wl4g.devops.components.shell.utils.ShellUtils.isTrue;
import static com.wl4g.devops.components.tools.common.cli.ProcessUtils.*;
import static java.lang.String.format;
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

	/** Running status. */
	final private AtomicBoolean running = new AtomicBoolean(false);

	/** Mark the current console pending state. */
	private volatile boolean pauseState = false;

	/** Current command line stdin string. */
	private String stdin;

	/** Payload command last sent timestamp, for timeout check. */
	private long lastCmdSentTime = 0L;

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
				printDebug("readLine...");
				stdin = lineReader.readLine(getPrompt());
				printDebug("readLine: " + stdin);

				synchronized (this) { // MARK2
					notifyAll(); // see:MARK3
				}

				// Simulates interrupt events, for secondary
				// development, it is convenient to debug in IDE environment.
				if (DEBUG && "E".equals(trimToEmpty(stdin))) {
					throw new UserInterruptException(stdin);
				}

				// Payload command?
				if (!isBlank(stdin) && !isPaused()) {
					paused(); // Paused wait complete
					lastCmdSentTime = currentTimeMillis();
					writeStdin(stdin); // Do send command
				}

				// e.g: when the last execution is not completed, the channel
				// has been interrupted, need wakeup the console prompt to
				// resume reading.
				if (isPaused() && !isActive()) {
					wakeup();
				}
			} catch (UserInterruptException e) { // e.g: Ctrl+C
				// Last command completed, interrupt allowed
				if (!isPaused()) {
					out.println(format("Command is cancelled. to exit please use: %s|%s|%s|%s", INTERNAL_EXIT, INTERNAL_EX,
							INTERNAL_QUIT, INTERNAL_QU));
				} else {
					// Last command is not completed, send interrupt signal
					// stop gracefully
					writeStdin(new PreInterruptSignal(true));
				}
			} catch (Throwable e) {
				printError(EMPTY, e);
			}
		}

	}

	@Override
	protected void postHandleOutput(Object output) throws Exception {
		if (output instanceof Signal) { // Remote command stdout?
			// Meta
			if (output instanceof MetaSignal) {
				MetaSignal meta = (MetaSignal) output;
				getSingle().merge(meta.getRegistedMethods());
			}
			// Progress
			else if (output instanceof ProgressSignal) {
				ProgressSignal pro = (ProgressSignal) output;
				printProgress(pro.getTitle(), pro.getProgress(), pro.getWhole(), '=');
			}
			// Ask interrupt
			else if (output instanceof AskInterruptSignal) {
				AskInterruptSignal ask = (AskInterruptSignal) output;
				printDebug("ask interrupt ...");

				// Print retry ask prompt
				do {
					lineReader.printAbove(ask.getSubject());
					synchronized (this) { // MARK3
						wait(TIMEOUT); // see:MARK2
					}
				} while (isBlank(stdin));

				AckInterruptSignal confirm = new AckInterruptSignal(isTrue(trimToEmpty(stdin), false));
				if (confirm.getConfirm()) {
					out.println("Command interrupting...");
				} else {
					out.println("Cancel interrupt!");
				}
				// Echo interrupt
				writeStdin(confirm);
			}
			// Stderr
			else if (output instanceof StderrSignal) {
				StderrSignal stderr = (StderrSignal) output;
				printError(EMPTY, stderr.getThrowable());
				wakeup();
			}
			// BOF stdout
			else if (output instanceof BOFStdoutSignal) {
				// Ignore
			}
			// EOF stdout
			else if (output instanceof EOFStdoutSignal) {
				wakeup();
			}
			// Stdout
			else if (output instanceof StdoutSignal) {
				out.println(((StdoutSignal) output).getContent());
			}
		} else { // Local command stdout?
			wakeup();
		}

		// Print of local command stdout.
		if (output instanceof CharSequence) {
			out.println(output);
		}

	}

	/**
	 * Pause wait for completed. </br>
	 * {@link AbstractClientShellHandler#wakeup()}
	 */
	private void paused() {
		printDebug(format("waitForCompleted: %s, completed: %s", this, pauseState));
		pauseState = true;
	}

	/**
	 * Wake-up for lineReader watching. </br>
	 * 
	 * {@link AbstractClientShellHandler#waitForComplished()}
	 */
	private void wakeup() {
		printDebug(format("Wakeup: %s, completed: %s", this, pauseState));
		pauseState = false;
	}

	/**
	 * Get the current status prompt.
	 * 
	 * @return
	 */
	private String getPrompt() {
		printDebug(format("getPrompt: %s, pauseState: %s", this, pauseState));
		return isPaused() ? EMPTY : getAttributed().toAnsi(lineReader.getTerminal());
	}

	/**
	 * Check whether it is currently paused? (if the last command is not
	 * completed, it is paused)
	 * 
	 * @return
	 */
	private boolean isPaused() {
		return pauseState && (currentTimeMillis() - lastCmdSentTime) < TIMEOUT;
	}

}