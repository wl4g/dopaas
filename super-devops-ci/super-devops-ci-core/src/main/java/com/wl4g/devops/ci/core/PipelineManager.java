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
package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.core.command.HookCommand;
import com.wl4g.devops.ci.core.command.NewCommand;
import com.wl4g.devops.ci.core.command.RollbackCommand;
import com.wl4g.devops.common.utils.io.FileIOUtils.ReadResult;

/**
 * CICD pipeline entry management.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-08-01 14:45:00
 */
public abstract interface PipelineManager {

	/**
	 * New create pipeline task job.
	 * 
	 * @param cmd
	 */
	void newPipeline(NewCommand cmd);

	/**
	 * Roll-back pipeline task job.
	 * 
	 * @param cmd
	 */
	void rollbackPipeline(RollbackCommand cmd);

	/**
	 * Hook pipeline task job.
	 * 
	 * @param cmd
	 */
	void hookPipeline(HookCommand cmd);

	/**
	 * Reader pipeline task job logs.
	 * 
	 * @param taskHisId
	 * @param index
	 * @param size
	 * @return
	 */
	ReadResult logfile(Integer taskHisId, Integer index, Integer size);

}