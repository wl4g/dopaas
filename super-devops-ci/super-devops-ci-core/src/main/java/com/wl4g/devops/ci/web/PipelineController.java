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
package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.service.PipelineService;
import com.wl4g.devops.common.bean.ci.PipeStepBuilding;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.page.PageModel;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/pipe")
public class PipelineController extends BaseController {

	@Autowired
	private PipelineManager pipeliner;

	@Autowired
	private PipelineService pipelineService;

	@Autowired
	private FlowManager flowManager;

	/**
	 * Page List
	 * 
	 * @param customPage
	 * @param id
	 * @param taskName
	 * @param groupName
	 * @param branchName
	 * @param providerKind
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/list")
	public RespBase<?> list(PageModel pm, String pipeName, String providerKind, String environment) {
		RespBase<Object> resp = RespBase.create();
		PageModel list = pipelineService.list(pm, pipeName, providerKind, environment);
		resp.setData(list);
		return resp;
	}

	/**
	 * Save
	 * 
	 * @param task
	 * @param instance
	 * @return
	 */
	@RequestMapping(value = "/save")
	public RespBase<?> save(@RequestBody Pipeline pipeline) {
		Assert.notNull(pipeline, "task can not be null");
		checkPipeline(pipeline);
		RespBase<Object> resp = RespBase.create();
		pipelineService.save(pipeline);
		return resp;
	}

	/**
	 * Detail by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	public RespBase<?> detail(Integer id) {
		log.info("into TaskController.detail prarms::" + "id = {} ", id);
		Assert.notNull(id, "id can not be null");
		RespBase<Object> resp = RespBase.create();
		resp.setData(pipelineService.detail(id));
		return resp;
	}

	/**
	 * Delete by id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/del")
	public RespBase<?> del(Integer id) {
		Assert.notNull(id, "id can not be null");
		RespBase<Object> resp = RespBase.create();
		pipelineService.del(id);
		return resp;
	}

	/**
	 * Check the form
	 * 
	 * @param task
	 */
	private void checkPipeline(Pipeline pipeline) {
		hasTextOf(pipeline.getPipeName(), "pipeName");
		notNullOf(pipeline.getClusterId(), "clusterId");
		notNullOf(pipeline.getProviderKind(), "providerKind");
	}

	/**
	 * Get List By appClusterId
	 * 
	 * @param appClusterId
	 */
	@RequestMapping(value = "/getListByAppClusterId")
	public RespBase<?> getListByAppClusterId(Integer clusterId) {
		notNullOf(clusterId, "clusterId");
		RespBase<Object> resp = RespBase.create();
		resp.setData(pipelineService.getByClusterId(clusterId));
		return resp;
	}

	/**
	 * create Task History and run Task
	 * 
	 * @param taskId
	 */
	/*@RequestMapping(value = "/create")
	public RespBase<?> create(Integer taskId, String trackId, String trackType, String remark, String annex) {
		RespBase<Object> resp = RespBase.create();
		PipelineModel pipelineModel = flowManager.buildPipeline(taskId);
		pipeliner.runPipeline(new NewParameter(taskId, remark, trackId, trackType, annex),pipelineModel);
		return resp;
	}*/

	/**
	 * create Task History and run Task
	 *
	 * @param taskId
	 */
	@RequestMapping(value = "/create")
	public RespBase<?> create(NewParameter newParameter) {
		RespBase<Object> resp = RespBase.create();
		PipelineModel pipelineModel = flowManager.buildPipeline(newParameter.getPipeId());
		pipeliner.runPipeline(newParameter,pipelineModel);
		return resp;
	}



	@RequestMapping(value = "/getPipeStepBuilding")
	public RespBase<?> getPipeStepBuilding(Integer clusterId, Integer pipeId, Integer refType) {
		Assert2.notNullOf(clusterId, "clusterId");
		RespBase<Object> resp = RespBase.create();
		PipeStepBuilding pipeStepBuilding = pipelineService.getPipeStepBuilding(clusterId, pipeId, refType);
		resp.setData(pipeStepBuilding);
		return resp;
	}

	@RequestMapping(value = "/getForSelect")
	public RespBase<?> getForSelect() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(pipelineService.getForSelect());
		return resp;

	}

}