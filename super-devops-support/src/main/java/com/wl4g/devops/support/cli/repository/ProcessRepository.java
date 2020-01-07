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
package com.wl4g.devops.support.cli.repository;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.support.NoSuchProcessException;
import com.wl4g.devops.support.cli.process.DestroableProcess;

/**
 * Command-line process repository interface.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Beta
public interface ProcessRepository {

	/**
	 * Registration to command-line process.
	 * 
	 * @param processId
	 * @param dpw
	 */
	void register(String processId, DestroableProcess dpw);

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
	 * Get command-line process information.
	 * 
	 * @param processId
	 * @return
	 */
	DestroableProcess get(String processId) throws NoSuchProcessException;

	/**
	 * Check local has command-line process.
	 * 
	 * @param processId
	 * @return
	 */
	boolean hasProcess(String processId);

	/**
	 * Remove cleanup command-line process information.
	 * 
	 * @param processId
	 * @return
	 */
	DestroableProcess cleanup(String processId);

	/**
	 * Obtain processes list all.
	 * 
	 * @return
	 */
	Collection<DestroableProcess> getProcessRegistry();

}