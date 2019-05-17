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

import com.wl4g.devops.shell.config.Configuration;
import com.wl4g.devops.shell.exception.ProcessTimeoutException;

import org.jline.reader.UserInterruptException;

/**
 * Interactive shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class InteractiveRunner extends AbstractRunner {

	public InteractiveRunner(Configuration config) {
		super(config);
	}

	@Override
	public void run(String[] args) {
		synchronized (lock) {
			while (true) { // Listening console input.
				Thread worker = null;
				String line = null;
				try {
					// Read line
					line = lineReader.readLine(getAttributed().toAnsi(lineReader.getTerminal()));

					// Submission processing
					if (isNotBlank(line)) {
						final String _line = line;
						worker = new Thread(() -> {
							try {
								submit(_line);
							} catch (IOException e) {
								throw new IllegalStateException(e);
							}
						});
						worker.start();
						long begin = System.currentTimeMillis();

						// Wait for response.
						waitForResponse();

						// Check wait timeout
						if ((System.currentTimeMillis() - begin) >= TIMEOUT) {
							throw new ProcessTimeoutException(String.format("Processing command timeout: %s", line));
						}
					}

				} catch (UserInterruptException e) {
					shutdown(line);
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
	}

}
