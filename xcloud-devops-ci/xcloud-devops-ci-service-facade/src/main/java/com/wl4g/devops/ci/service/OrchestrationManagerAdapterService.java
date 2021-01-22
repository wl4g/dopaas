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
package com.wl4g.devops.ci.service;

import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import com.wl4g.devops.common.bean.ci.Orchestration;
import com.wl4g.devops.common.bean.ci.model.PipelineModel;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

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
@SpringBootFeignClient(name = "${provider.serviceId:flowPipelineManager-service}")
@RequestMapping("/flowPipelineManager")
public interface OrchestrationManagerAdapterService {

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

	/**
	 * for single pipeline
	 *
	 * @param pipelineId
	 * @return
	 */
	// TODO 这里需要添加redis锁（）jedisService.setMap()
	@RequestMapping(method = POST, path = "buildPipeline")
	public PipelineModel buildPipeline(@RequestParam(name = "pipelineId", required = false) Long pipelineId);

}