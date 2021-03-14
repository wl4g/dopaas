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
package com.wl4g.paas.uci.web;

import com.wl4g.component.common.io.FileIOUtils;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.paas.uci.service.OrchestrationManagerAdapter;
import com.wl4g.paas.uci.service.PipelineManagerAdapter;
import com.wl4g.paas.uci.service.PipelineHistoryService;
import com.wl4g.paas.common.bean.uci.PipelineHistory;
import com.wl4g.paas.common.bean.uci.model.PipelineModel;
import com.wl4g.paas.common.bean.uci.param.RollbackParameter;

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
	private PipelineManagerAdapter pipelineManagerService;

	@Autowired
	private OrchestrationManagerAdapter flowManagerService;

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
	@RequiresPermissions(value = { "uci:pipehis" }, logical = AND)
	public RespBase<?> list(PageHolder<PipelineHistory> pm, String pipeName, String clusterName, String environment,
			String startDate, String endDate, String providerKind) {
		RespBase<Object> resp = RespBase.create();
		PageHolder<?> list = pipelineHistoryService.list(pm, pipeName, clusterName, environment, startDate, endDate,
				providerKind);
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
	@RequiresPermissions(value = { "uci:pipehis" }, logical = AND)
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
	@RequiresPermissions(value = { "uci:pipehis" }, logical = AND)
	public RespBase<?> rollback(Long pipeHisId) {
		RespBase<Object> resp = RespBase.create();
		PipelineModel pipeModel = flowManagerService.buildPipeline(pipeHisId);
		pipelineManagerService.rollbackPipeline(new RollbackParameter(pipeHisId, "rollback", pipeModel));
		return resp;
	}

	@RequestMapping(value = "/readLog")
	@RequiresPermissions(value = { "uci:pipehis" }, logical = AND)
	public RespBase<?> readLog(Long pipeHisId, Long startPos, Integer size) {
		RespBase<Object> resp = RespBase.create();
		FileIOUtils.ReadTailFrame readResult = pipelineManagerService.logfile(pipeHisId, startPos, size);
		resp.forMap().put("data", readResult);
		return resp;
	}

	@RequestMapping(value = "/readDetailLog")
	@RequiresPermissions(value = { "uci:pipehis" }, logical = AND)
	public RespBase<?> readDetailLog(Long pipeHisId, Long instanceId, Long startPos, Integer size) {
		RespBase<Object> resp = RespBase.create();
		FileIOUtils.ReadTailFrame readResult = pipelineManagerService.logDetailFile(pipeHisId, instanceId, startPos, size);
		resp.forMap().put("data", readResult);
		return resp;
	}

	@RequestMapping(value = "/stopTask")
	@RequiresPermissions(value = { "uci:pipehis" }, logical = AND)
	public RespBase<?> create(Long pipeHisId) {
		RespBase<Object> resp = RespBase.create();
		pipelineHistoryService.stopByPipeHisId(pipeHisId);
		return resp;
	}

}