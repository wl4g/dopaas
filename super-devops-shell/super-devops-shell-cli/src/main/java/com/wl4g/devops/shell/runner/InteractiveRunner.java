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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.shell.bean.ExceptionMessage;
import com.wl4g.devops.shell.bean.MetaMessage;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.config.Configuration;
import static com.wl4g.devops.shell.bean.RunState.*;
import static com.wl4g.devops.shell.config.DefaultBeanRegistry.getSingle;
import static java.lang.System.out;

import org.jline.reader.UserInterruptException;

/**
 * Interactive shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class InteractiveRunner extends AbstractRunner {

	final private AtomicBoolean completed = new AtomicBoolean(true);

	public InteractiveRunner(Configuration config) {
		super(config);
	}

	@Override
	public void run(String[] args) {
		while (true) {
			Thread worker = null;
			try {
				String line = lineReader.readLine(getPrompt());
				if (isNotBlank(line) && completed.get()) {
					// Submission processing
					worker = new Thread(() -> {
						try {
							submit(line);
						} catch (IOException e) {
							throw new IllegalStateException(e);
						}
					});
					worker.start();

					// Wait completed.
					waitForCompleted(line);
				}
			} catch (UserInterruptException e) {
				shutdown();
			} catch (Throwable e) {
				printErr(EMPTY, e);
			} finally {
				if (worker != null) {
					worker.interrupt();
					worker = null;
				}
			}
		}

	}

	@Override
	protected void postProcessResult(Object result) {
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
			if (ret.getState() == NONCE || ret.getState() == FINISHED) {
				wakeup();
			}

			// Print server result message.
			out.println(ret.getContent());
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
		completed.set(false);
	}

	/**
	 * Wakeup for wait lineReader. </br>
	 * {@link AbstractRunner#waitForComplished()}
	 */
	private void wakeup() {
		completed.set(true);
	}

	/**
	 * Get the current status prompt.
	 * 
	 * @return
	 */
	private String getPrompt() {
		return completed.get() ? getAttributed().toAnsi(lineReader.getTerminal()) : EMPTY;
	}

}
