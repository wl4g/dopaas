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
package com.wl4g.dopaas.uci.service;

import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.uci.Orchestration;
import com.wl4g.dopaas.common.bean.uci.model.PipelineModel;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Flow pipelines manager.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-03-22
 * @sine v1.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.uci-facade:uci-facade}")
@RequestMapping("/flowPipelineManager-service")
public interface OrchestrationManagerAdapter {

	/**
	 * Start to run orchestration
	 *
	 * @param orchestration
	 */
	@RequestMapping(method = POST, path = "runOrchestration")
	public void runOrchestration(@RequestBody Orchestration orchestration,
			@RequestParam(name = "remark", required = false) String remark,
			@RequestParam(name = "taskTraceId", required = false) String taskTraceId,
			@RequestParam(name = "taskTraceType", required = false) String taskTraceType,
			@RequestParam(name = "annex", required = false) String annex);

	// TODO add distributed locks
	/**
	 * for single pipeline
	 *
	 * @param pipelineId
	 * @return
	 */
	@RequestMapping(method = POST, path = "buildPipeline")
	public PipelineModel buildPipeline(@RequestParam(name = "pipelineId", required = false) Long pipelineId);

}