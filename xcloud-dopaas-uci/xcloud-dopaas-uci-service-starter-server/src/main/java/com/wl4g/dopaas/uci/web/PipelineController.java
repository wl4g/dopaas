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
package com.wl4g.dopaas.uci.web;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.uci.service.OrchestrationManagerAdapter;
import com.wl4g.dopaas.uci.service.PipelineManagerAdapter;
import com.wl4g.dopaas.uci.service.PipelineService;
import com.wl4g.dopaas.common.bean.uci.ClusterExtension;
import com.wl4g.dopaas.common.bean.uci.Pipeline;
import com.wl4g.dopaas.common.bean.uci.param.RunParameter;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static org.apache.shiro.authz.annotation.Logical.AND;

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
	private PipelineManagerAdapter pipelineManagerService;

	@Autowired
	private OrchestrationManagerAdapter flowManagerService;

	@Autowired
	private PipelineService pipelineService;

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
	@RequiresPermissions(value = { "uci:pipeline" }, logical = AND)
	public RespBase<?> list(PageHolder<Pipeline> pm, String pipeName, String providerKind, String environment) {
		RespBase<Object> resp = RespBase.create();
		PageHolder<?> list = pipelineService.list(pm, pipeName, providerKind, environment);
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
	@RequiresPermissions(value = { "uci", "uci:pipeline" }, logical = AND)
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
	@RequiresPermissions(value = { "uci:pipeline" }, logical = AND)
	public RespBase<?> detail(Long id) {
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
	@RequiresPermissions(value = { "uci:pipeline" }, logical = AND)
	public RespBase<?> del(Long id) {
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
	public RespBase<?> getListByAppClusterId(Long clusterId) {
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
	@RequestMapping(value = "/create")
	@RequiresPermissions(value = { "uci:pipeline" }, logical = AND)
	public RespBase<?> create(RunParameter runParam) throws Exception {
		RespBase<Object> resp = RespBase.create();
		runParam.setPipeModel(flowManagerService.buildPipeline(runParam.getPipeId()));
		pipelineManagerService.runPipeline(runParam);
		return resp;
	}

	@RequestMapping(value = "/getPipeStepBuilding")
	public RespBase<?> getPipeStepBuilding(Long clusterId, Long pipeId, Integer refType) throws Exception {
		Assert2.notNullOf(clusterId, "clusterId");
		RespBase<Object> resp = RespBase.create();
		return resp.withData(pipelineService.getPipeStageBuilding(clusterId, pipeId, refType));
	}

	@RequestMapping(value = "/getForSelect")
	public RespBase<?> getForSelect(String environment) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(pipelineService.getForSelect(environment));
		return resp;
	}

	@RequestMapping(value = "/clusterExtensionList")
	public RespBase<?> clusterExtensionList(PageHolder<ClusterExtension> pm, String clusterName) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(pipelineService.clusterExtensionList(pm, clusterName));
		return resp;
	}

	@RequestMapping(value = "/saveClusterExtension")
	public RespBase<?> saveClusterExtension(ClusterExtension clusterExtension) {
		RespBase<Object> resp = RespBase.create();
		pipelineService.saveClusterExtension(clusterExtension);
		return resp;
	}

}