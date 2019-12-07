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

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;

import java.io.File;
import java.io.Serializable;

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

	/** Command of script. */
	final private String cmd;

	/** Command of stdout file. */
	final private File stdout;

	/** Command of stderr file. */
	final private File stderr;

	/** Command of timeout.(Ms) */
	final private long timeoutMs;

	public DestroableCommand(String processId, String cmd, long timeoutMs) {
		this(processId, cmd, null, null, timeoutMs);
	}

	public DestroableCommand(String processId, String cmd, File stdout, File stderr, long timeoutMs) {
		// hasText(processId, "Command processId can't empty.");
		hasText(cmd, "Command can't empty.");
		// notNull(stdout, "Command stdout can't null.");
		// notNull(stderr, "Command stderr can't null.");
		isTrue(timeoutMs > 0, "Command must timeoutMs>0.");
		this.processId = processId;
		this.cmd = cmd;
		this.stdout = stdout;
		this.stderr = stderr;
		this.timeoutMs = timeoutMs;
	}

	public String getProcessId() {
		return processId;
	}

	public String getCmd() {
		return cmd;
	}

	public File getStdout() {
		return stdout;
	}

	public File getStderr() {
		return stderr;
	}

	public long getTimeoutMs() {
		return timeoutMs;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
