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

import java.io.File;

/**
 * Generic(local) command's wrapper.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月5日
 * @since
 */
public class LocalDestroableCommand extends DestroableCommand {
	private static final long serialVersionUID = -5843814202945157321L;

	/** Command of context directory. */
	final private File pwdDir;

	public LocalDestroableCommand(String command, File stdout, File stderr, long timeoutMs) {
		this(null, command, null, stdout, stderr, timeoutMs);
	}

	public LocalDestroableCommand(String command, File pwdDir, File stdout, File stderr, long timeoutMs) {
		this(null, command, pwdDir, stdout, stderr, timeoutMs);
	}

	public LocalDestroableCommand(String processId, String command, long timeoutMs) {
		this(processId, command, null, null, null, timeoutMs);
	}

	public LocalDestroableCommand(String processId, String command, File pwdDir, File stdout, File stderr, long timeoutMs) {
		super(processId, command, stdout, stderr, timeoutMs);
		// notNull(pwdDir, "Command pwdDir can't null.");
		this.pwdDir = pwdDir;
	}

	public File getPwdDir() {
		return pwdDir;
	}

}
