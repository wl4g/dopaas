/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.common.io.FileIOUtils.ReadTailFrame;
import com.wl4g.dopaas.uci.core.PipelineManager;
import com.wl4g.dopaas.uci.service.PipelineManagerAdapter;
import com.wl4g.dopaas.uci.utils.HookCommandHolder.HookCommand;
import com.wl4g.dopaas.common.bean.uci.param.RollbackParameter;
import com.wl4g.dopaas.common.bean.uci.param.RunParameter;

/**
 * {@link PipelineManagerAdapterImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-01-22
 * @sine v1.0
 * @see
 */
@Service
public class PipelineManagerAdapterImpl implements PipelineManagerAdapter {

	@Autowired
	private PipelineManager pipelineManager;

	@Override
	public void runPipeline(RunParameter runParam) throws Exception {
		pipelineManager.runPipeline(runParam);
	}

	@Override
	public void rollbackPipeline(RollbackParameter rollback) {
		pipelineManager.rollbackPipeline(rollback);
	}

	@Override
	public void hookPipeline(HookCommand hook) throws Exception {
		pipelineManager.hookPipeline(hook);
	}

	@Override
	public ReadTailFrame logfile(Long taskHisId, Long startPos, Integer size) {
		return pipelineManager.logfile(taskHisId, startPos, size);
	}

	@Override
	public ReadTailFrame logDetailFile(Long taskHisId, Long instanceId, Long startPos, Integer size) {
		return pipelineManager.logDetailFile(taskHisId, instanceId, startPos, size);
	}

}
