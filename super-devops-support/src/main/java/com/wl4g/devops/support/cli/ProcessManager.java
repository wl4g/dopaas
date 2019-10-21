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
package com.wl4g.devops.support.cli;

import java.io.File;
import java.io.IOException;

import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.support.IllegalProcessStateException;
import com.wl4g.devops.common.exception.support.TimeoutDestroyProcessException;

/**
 * Commands line process management.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Beta
public interface ProcessManager {

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param processId
	 *            command-lien process ID.
	 * @param cmd
	 *            commands
	 * @param timeoutMs
	 *            timeout Ms.
	 * @throws InterruptedException
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	void exec(String processId, String cmd, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException;

	/**
	 * Blocking execution command-line to process.
	 * 
	 * @param processId
	 *            command-lien process ID.
	 * @param cmd
	 *            commands
	 * @param pwdDir
	 *            process command-line context directory
	 * @param stdout
	 *            standard output stream file.
	 * @param timeoutMs
	 *            timeout Ms.
	 * @param processId
	 * @throws InterruptedException
	 * @throws IllegalProcessStateException
	 * @throws IOException
	 */
	void exec(String processId, String cmd, File pwdDir, File stdout, long timeoutMs)
			throws InterruptedException, IllegalProcessStateException, IOException;

	/**
	 * Destroy command-line process.</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param processId
	 *            Create command-lien process ID.
	 * @param timeoutMs
	 *            Destroy process timeout Ms.
	 * @throws TimeoutDestroyProcessException
	 */
	void destroy(String processId, long timeoutMs) throws TimeoutDestroyProcessException;

}
