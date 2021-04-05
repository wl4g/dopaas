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

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.uci.PipelineHistory;
import com.wl4g.dopaas.common.bean.uci.PipelineHistoryInstance;
import com.wl4g.dopaas.common.bean.uci.param.HookParameter;
import com.wl4g.dopaas.common.bean.uci.param.RollbackParameter;
import com.wl4g.dopaas.common.bean.uci.param.RunParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * {@link PipelineHistoryService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-04-27 17:24:00
 * @sine v1.0.0
 * @see
 */
@FeignConsumer(name = "${provider.serviceId.uci-facade:uci-facade}")
@RequestMapping("/pipelineHistory-service")
public interface PipelineHistoryService {

	@RequestMapping(value = "/createRunnerPipeline", method = POST)
	PipelineHistory createRunnerPipeline(@RequestBody RunParameter run);

	@RequestMapping(value = "/createHookPipeline", method = POST)
	PipelineHistory createHookPipeline(@RequestBody HookParameter hook);

	@RequestMapping(value = "/createRollbackPipeline", method = POST)
	PipelineHistory createRollbackPipeline(@RequestBody RollbackParameter rollback);

	@RequestMapping(value = "/updatePipeHisInstanceStatus", method = POST)
	void updatePipeHisInstanceStatus(@RequestParam(name = "pipeInstanceId", required = false) Long pipeInstanceId,
			@RequestParam(name = "status", required = false) int status);

	@RequestMapping(value = "/updateStatus", method = POST)
	void updateStatus(@RequestParam(name = "pipeId", required = false) Long pipeId,
			@RequestParam(name = "status", required = false) int status);

	@RequestMapping(value = "/updateStatusAndResultAndSha", method = POST)
	void updateStatusAndResultAndSha(@RequestParam(name = "pipeId", required = false) Long pipeId,
			@RequestParam(name = "status", required = false) int status,
			@RequestParam(name = "sha", required = false) String sha);

	@RequestMapping(value = "/stopByPipeHisId", method = POST)
	void stopByPipeHisId(@RequestParam(name = "taskHisId", required = false) Long taskHisId);

	@RequestMapping(value = "/updateCostTime", method = POST)
	void updateCostTime(@RequestParam(name = "taskId", required = false) Long taskId,
			@RequestParam(name = "costTime", required = false) long costTime);

	@RequestMapping(value = "/list", method = POST)
	PageHolder<PipelineHistory> list(@RequestBody PageHolder<PipelineHistory> pm,
			@RequestParam(name = "pipeName", required = false) String pipeName,
			@RequestParam(name = "clusterName", required = false) String clusterName,
			@RequestParam(name = "environment", required = false) String environment,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "providerKind", required = false) String providerKind);

	@RequestMapping(value = "/list", method = POST)
	List<PipelineHistory> list(
			@RequestBody PageHolder<PipelineHistory> pm,
			@RequestParam(name = "pipeName", required = false) String pipeName,
			@RequestParam(name = "clusterName", required = false) String clusterName,
			@RequestParam(name = "environment", required = false) String environment,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "providerKind", required = false) String providerKind,
			@RequestParam(name = "orchestrationType", required = false) Integer orchestrationType,
			@RequestParam(name = "providerKind", required = false) Long orchestrationId);


	@RequestMapping(value = "/getPipeHisInstanceByPipeId", method = POST)
	List<PipelineHistoryInstance> getPipeHisInstanceByPipeId(@RequestParam(name = "pipeHisId", required = false) Long pipeHisId);

	@RequestMapping(value = "/detail", method = POST)
	PipelineHistory detail(@RequestParam(name = "pipeHisId", required = false) Long pipeHisId);

	@RequestMapping(value = "/getById", method = POST)
	PipelineHistory getById(@RequestParam(name = "pipeHisId", required = false) Long pipeHisId);

}