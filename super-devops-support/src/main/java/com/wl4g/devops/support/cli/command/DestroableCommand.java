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

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

import java.io.Serializable;

import com.wl4g.devops.support.cli.GenericProcessManager;
import com.wl4g.devops.support.cli.process.DestroableProcess;

/**
 * Basic destroable command's wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月5日
 * @since
 */
public class DestroableCommand implements Serializable {
	private static final long serialVersionUID = -5843814202945157321L;

	/** Command of processId. */
	final private String processId;

	/**
	 * Command of script.</br>
	 * Note: please use the "|" channel symbol and redirection carefully, for
	 * example:
	 * 
	 * <pre>
	 * mvn install | tee -a /mvn.out
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 * mvn install > /mvn.out 2>&1
	 * </pre>
	 * 
	 * , which will cause the exit code and stdout / stderr of command execution
	 * to behave abnormally, that is, whether the execution result is abnormal
	 * or not is uncertain. Please refer to:
	 * {@link GenericProcessManager#inputStreamRead0()}
	 */
	final private String cmd;

	/** Command of timeout.(Ms) */
	final private long timeoutMs;

	/**
	 * Set whether the current process is allowed to interrupt, for example:
	 * when processing a lengthy task, when some key steps are executed, it can
	 * be set as not to interrupt, which is very useful to ensure the security
	 * of the task.
	 * 
	 * @see {@link DestroableProcess}
	 */
	final private boolean destroable;

	public DestroableCommand(String cmd, long timeoutMs) {
		this(null, cmd, false, timeoutMs);
	}

	public DestroableCommand(String processId, String cmd, boolean destroable, long timeoutMs) {
		if (destroable) { // Must be set for a destructable processorId
			hasText(processId, "Command processId can't empty.");
		}
		hasText(cmd, "Command can't empty.");
		isTrue(timeoutMs > 0, "Command must timeoutMs>0.");
		this.processId = processId;
		this.cmd = cmd;
		this.destroable = destroable;
		this.timeoutMs = timeoutMs;
	}

	public String getProcessId() {
		return processId;
	}

	public String getCmd() {
		return cmd;
	}

	public boolean isDestroable() {
		return destroable;
	}

	public long getTimeoutMs() {
		return timeoutMs;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}