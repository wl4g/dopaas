/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.uci.core;

import com.wl4g.component.common.io.FileIOUtils.ReadTailFrame;
import com.wl4g.paas.uci.utils.HookCommandHolder.HookCommand;
import com.wl4g.paas.common.bean.uci.param.RollbackParameter;
import com.wl4g.paas.common.bean.uci.param.RunParameter;

/**
 * CICD pipeline entry management.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-08-01 14:45:00
 */
public interface PipelineManager {

	/**
	 * New create pipeline task job.
	 * 
	 * @param runParam
	 */
	void runPipeline(RunParameter runParam) throws Exception;

	/**
	 * Roll-back pipeline task job.
	 * 
	 * @param rollback
	 */
	void rollbackPipeline(RollbackParameter rollback);

	/**
	 * Hook pipeline task job.
	 * 
	 * @param param
	 */
	void hookPipeline(HookCommand hook) throws Exception;

	/**
	 * Reader pipeline task building logs.
	 * 
	 * @param taskHisId
	 * @param startPos
	 * @param size
	 * @return
	 */
	ReadTailFrame logfile(Long taskHisId, Long startPos, Integer size);

	/**
	 * Reader pipeline task detail deploying logs.
	 *
	 * @param taskHisId
	 * @param taskHisDetailId
	 * @param startPos
	 * @param size
	 * @return
	 */
	ReadTailFrame logDetailFile(Long taskHisId, Long instanceId, Long startPos, Integer size);

}