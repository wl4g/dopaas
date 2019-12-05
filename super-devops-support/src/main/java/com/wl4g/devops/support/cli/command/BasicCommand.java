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
package com.wl4g.devops.support.cli.command;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

import java.io.Serializable;
import java.util.concurrent.Executor;

/**
 * Basic command's wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月5日
 * @since
 */
public class BasicCommand implements Serializable {
	private static final long serialVersionUID = -5843814202945157321L;

	/** Command of processId. */
	final private String processId;
	/** Command of script. */
	final private String command;
	/** Command of timeout.(Ms) */
	final private long timeoutMs;
	/** Input reading actuator returned by command. */
	final private Executor inputExecutor;

	public BasicCommand(String command, long timeoutMs) {
		this(null, command, timeoutMs);
	}

	public BasicCommand(String processId, String command, long timeoutMs) {
		this(processId, command, timeoutMs, null);
	}

	public BasicCommand(String processId, String command, long timeoutMs, Executor inputExecutor) {
		// hasText(processId, "Command processId can't empty.");
		hasText(command, "Command can't empty.");
		isTrue(timeoutMs > 0, "Command must timeoutMs>0.");
		// notNull(inputExecutor, "Command input executor can't null.");
		this.processId = processId;
		this.command = command;
		this.timeoutMs = timeoutMs;
		this.inputExecutor = inputExecutor;
	}

	public String getProcessId() {
		return processId;
	}

	public String getCommand() {
		return command;
	}

	public long getTimeoutMs() {
		return timeoutMs;
	}

	public Executor getInputExecutor() {
		return inputExecutor;
	}

}
