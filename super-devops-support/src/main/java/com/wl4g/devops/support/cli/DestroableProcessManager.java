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

import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.support.IllegalProcessStateException;
import com.wl4g.devops.common.exception.support.NoSuchProcessException;
import com.wl4g.devops.common.exception.support.TimeoutDestroyProcessException;
import com.wl4g.devops.support.cli.command.DestroableCommand;
import com.wl4g.devops.support.cli.destroy.DestroySignal;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Supports destroable(Interruptible) execution command line process manager.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Beta
public interface DestroableProcessManager {

	/**
	 * Execution command line and blocking wait results.
	 * 
	 * @param command
	 * @return stander output to String
	 * @throws IllegalProcessStateException
	 * @throws InterruptedException
	 * @throws Exception
	 */
	String execWaitForComplete(DestroableCommand command) throws IllegalProcessStateException, InterruptedException, Exception;

	/**
	 * Non-blocking command line, callback standard or exception output
	 * 
	 * @param command
	 * @param executor
	 * @param callback
	 * @throws IOException
	 * @throws InterruptedException
	 */
	void exec(DestroableCommand command, Executor executor, ProcessCallback callback) throws Exception, InterruptedException;

	/**
	 * Destroy command processing process guidance complete (success or
	 * failure).</br>
	 * <font color=red>There's no guarantee that it will be killed.</font>
	 * 
	 * @param signal
	 * @throws TimeoutDestroyProcessException
	 * @throws IllegalStateException
	 */
	void destroyForComplete(DestroySignal signal) throws TimeoutDestroyProcessException, IllegalStateException;

	/**
	 * Set whether the current process is allowed to interrupt, for example:
	 * when processing a lengthy task, when some key steps are executed, it can
	 * be set as not to interrupt, which is very useful to ensure the security
	 * of the task.
	 * 
	 * @param processId
	 * @param destroable
	 * @throws NoSuchProcessException
	 */
	void setDestroable(String processId, boolean destroable) throws NoSuchProcessException;

	/**
	 * Async execution command process callback.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月21日
	 * @since
	 */
	public static interface ProcessCallback {

		/**
		 * Stdout process.
		 * 
		 * @param data
		 */
		void onStdout(byte[] data);

		/**
		 * Stderr process.
		 * 
		 * @param data
		 */
		void onStderr(byte[] err);

	}

}