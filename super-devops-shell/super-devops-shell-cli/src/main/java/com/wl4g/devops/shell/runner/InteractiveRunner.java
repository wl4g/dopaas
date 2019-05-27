/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell.runner;

import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.shell.bean.ExceptionMessage;
import com.wl4g.devops.shell.bean.InterruptMessage;
import com.wl4g.devops.shell.bean.MetaMessage;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.config.Configuration;
import static com.wl4g.devops.shell.bean.RunState.*;
import static com.wl4g.devops.shell.config.DefaultBeanRegistry.getSingle;
import static com.wl4g.devops.shell.handler.ChannelMessageHandler.BEGIN_EOF;
import static com.wl4g.devops.shell.handler.ChannelMessageHandler.EOF;
import static java.lang.System.*;

import org.jline.reader.UserInterruptException;

/**
 * Interactive shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class InteractiveRunner extends AbstractRunner {

	/** Mark the current processing completion status. */
	private volatile boolean completed = true;

	/** Current processing tasks. */
	private Thread task;

	/** Current processing command line strings. */
	private String line;

	/** Record command send timestamp, waiting for timeout processing. */
	private long sentTime = 0L;

	public InteractiveRunner(Configuration config) {
		super(config);
	}

	@Override
	public void run(String[] args) {
		while (true) {
			try {
				line = lineReader.readLine(getPrompt());

				// Debugging mode input 'E' simulates interrupt events
				if (DEBUG && "E".equals(trimToEmpty(line))) {
					throw new UserInterruptException(line);
				}

				if (isNotBlank(line) && isComplated()) {
					// Submission processing
					task = new Thread(() -> {
						sentTime = currentTimeMillis();
						submit(line);
					});
					task.start();

					// Wait completed.
					waitForCompleted(line);
				}
			} catch (UserInterruptException e) {
				// When there is no unfinished task, the console is interrupted.
				if (isComplated()) {
					shutdown();
				} else {
					// When there is an unfinished task, the interrupt task
					// signal is sent.
					submit(new InterruptMessage(true));
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
	protected void postProcessResult(Object result) {
		sentTime = currentTimeMillis();

		// Merge remote target methods commands
		if (result instanceof MetaMessage) {
			MetaMessage meta = (MetaMessage) result;
			getSingle().merge(meta.getRegistedMethods());
		} else if (result instanceof ExceptionMessage) {
			ExceptionMessage ex = (ExceptionMessage) result;
			printErr(EMPTY, ex.getThrowable());
		}

		if (result instanceof ResultMessage) {
			ResultMessage ret = (ResultMessage) result;
			// Update printf state
			// setState(ret.getState());

			// Wake-up the waiting thread when the response is
			// completed.
			if (ret.getState() == NONCE || ret.getState() == COMPLATED) {
				wakeup();
			}

			// Print server result message.
			if (!equalsAny(ret.getContent(), BEGIN_EOF, EOF)) {
				out.println(ret.getContent());
			}
		} else {
			wakeup(); // Wake-up lineReader
		}

		// Print local string message
		if (result instanceof CharSequence) {
			out.println(result);
		}

	}

	/**
	 * Wait for completed. </br>
	 * {@link AbstractRunner#wakeup()}
	 * 
	 * @param line
	 * @throws InterruptedException
	 */
	private void waitForCompleted(String line) {
		if (DEBUG) {
			out.println(String.format("waitForCompleted: %s, completed: %s", this, completed));
		}
		completed = false;
	}

	/**
	 * Wakeup for wait lineReader. </br>
	 * {@link AbstractRunner#waitForComplished()}
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
	private boolean isComplated() {
		return completed || (currentTimeMillis() - sentTime) >= TIMEOUT;
	}

}
