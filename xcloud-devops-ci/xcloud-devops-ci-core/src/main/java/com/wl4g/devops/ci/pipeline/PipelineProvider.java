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
package com.wl4g.devops.ci.pipeline;

import com.wl4g.components.common.annotation.StableApi;
import com.wl4g.devops.ci.core.context.PipelineContext;

/**
 * Pipeline provider SPI.
 * 
 * @author vjay
 * @author Wangl.sir <983708408@qq.com>
 * @date 2019-05-05 17:17:00
 */
@StableApi
public interface PipelineProvider {

	/**
	 * Execution pipeline with provider process.
	 * 
	 * @throws Exception
	 */
	default void execute() throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Roll-back with provider process.
	 * 
	 * @throws Exception
	 */
	default void rollback() throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get pipeline information.
	 * 
	 * @return
	 */
	PipelineContext getContext();

	/**
	 * Invoke remote commands.
	 * 
	 * @param remoteHost
	 * @param user
	 * @param command
	 * @param sshkey
	 * @throws Exception
	 */
	void doRemoteCommand(String remoteHost, String user, String command, String sshkey) throws Exception;

	char[] getUsableCipherSshKey(String sshkey) throws Exception;

	String getAssetsFingerprint();

	String getSourceFingerprint();

}