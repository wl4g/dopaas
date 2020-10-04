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
package com.wl4g.devops.ci.service;

import com.wl4g.components.core.bean.ci.PipelineHistory;
import com.wl4g.components.core.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-27 17:24:00
 */
public interface PipelineHistoryService {

	PipelineHistory createPipelineHistory(NewParameter newParameter);

	PipelineHistory createPipelineHistory(HookParameter hookParameter);

	PipelineHistory createPipelineHistory(RollbackParameter rollbackParameter);

	void updatePipeHisInstanceStatus(Long pipeInstanceId, int status);

	void updateStatus(Long pipeId, int status);

	void updateStatusAndResultAndSha(Long pipeId, int status, String sha);

	void stopByPipeHisId(Long taskHisId);

	void updateCostTime(Long taskId, long costTime);

	PageModel list(PageModel pm, String pipeName, String clusterName, String environment, String startDate, String endDate,
			String providerKind);

	List<PipelineHistoryInstance> getPipeHisInstanceByPipeId(Long pipeHisId);

	PipelineHistory detail(Long pipeHisId);

	PipelineHistory getById(Long pipeHisId);

}