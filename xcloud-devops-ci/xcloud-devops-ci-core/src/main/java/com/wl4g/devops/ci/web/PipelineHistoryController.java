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

import com.wl4g.components.common.io.FileIOUtils;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.ci.PipelineHistory;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.service.PipelineHistoryService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * Task History controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/pipeHis")
public class PipelineHistoryController extends BaseController {

	@Autowired
	private PipelineHistoryService pipelineHistoryService;

	@Autowired
	private PipelineManager pipe;

	@Autowired
	private FlowManager flowManager;

	/**
	 * Query search of page
	 * 
	 * @param groupName
	 * @param projectName
	 * @param branchName
	 * @param customPage
	 * @return
	 */
	@RequestMapping(value = "/list")
	@RequiresPermissions(value = { "ci:pipehis" }, logical = AND)
	public RespBase<?> list(PageModel<PipelineHistory> pm, String pipeName, String clusterName, String environment,
			String startDate, String endDate, String providerKind) {
		RespBase<Object> resp = RespBase.create();
		PageModel<?> list = pipelineHistoryService.list(pm, pipeName, clusterName, environment, startDate, endDate, providerKind);
		resp.setData(list);
		return resp;
	}

	/**
	 * Detail by id
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/detail")
	@RequiresPermissions(value = { "ci:pipehis" }, logical = AND)
	public RespBase<?> detail(Long pipeHisId) {
		RespBase<Object> resp = RespBase.create();
		PipelineHistory detail = pipelineHistoryService.detail(pipeHisId);
		resp.setData(detail);
		return resp;
	}

	/**
	 * Rollback
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value = "/rollback")
	@RequiresPermissions(value = { "ci:pipehis" }, logical = AND)
	public RespBase<?> rollback(Long pipeHisId) {
		RespBase<Object> resp = RespBase.create();
		PipelineModel pipelineModel = flowManager.buildPipeline(pipeHisId);
		pipe.rollbackPipeline(new RollbackParameter(pipeHisId, "rollback"), pipelineModel);
		return resp;
	}

	@RequestMapping(value = "/readLog")
	@RequiresPermissions(value = { "ci:pipehis" }, logical = AND)
	public RespBase<?> readLog(Long pipeHisId, Long startPos, Integer size) {
		RespBase<Object> resp = RespBase.create();
		FileIOUtils.ReadResult readResult = pipe.logfile(pipeHisId, startPos, size);
		resp.forMap().put("data", readResult);
		return resp;
	}

	@RequestMapping(value = "/readDetailLog")
	@RequiresPermissions(value = { "ci:pipehis" }, logical = AND)
	public RespBase<?> readDetailLog(Long pipeHisId, Long instanceId, Long startPos, Integer size) {
		RespBase<Object> resp = RespBase.create();
		FileIOUtils.ReadResult readResult = pipe.logDetailFile(pipeHisId, instanceId, startPos, size);
		resp.forMap().put("data", readResult);
		return resp;
	}

	@RequestMapping(value = "/stopTask")
	@RequiresPermissions(value = { "ci:pipehis" }, logical = AND)
	public RespBase<?> create(Long pipeHisId) {
		RespBase<Object> resp = RespBase.create();
		pipelineHistoryService.stopByPipeHisId(pipeHisId);
		return resp;
	}

}