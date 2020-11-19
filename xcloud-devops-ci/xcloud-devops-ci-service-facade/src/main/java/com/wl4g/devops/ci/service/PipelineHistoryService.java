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

import com.wl4g.components.core.web.model.PageModel;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.common.bean.ci.param.HookParameter;
import com.wl4g.devops.common.bean.ci.param.RollbackParameter;
import com.wl4g.devops.common.bean.ci.param.RunParameter;

import java.util.List;

/**
 * {@link PipelineHistoryService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-04-27 17:24:00
 * @sine v1.0.0
 * @see
 */
public interface PipelineHistoryService {

	PipelineHistory createRunnerPipeline(RunParameter run);

	PipelineHistory createHookPipeline(HookParameter hook);

	PipelineHistory createRollbackPipeline(RollbackParameter rollback);

	void updatePipeHisInstanceStatus(Long pipeInstanceId, int status);

	void updateStatus(Long pipeId, int status);

	void updateStatusAndResultAndSha(Long pipeId, int status, String sha);

	void stopByPipeHisId(Long taskHisId);

	void updateCostTime(Long taskId, long costTime);

	PageModel<PipelineHistory> list(PageModel<PipelineHistory> pm, String pipeName, String clusterName, String environment,
			String startDate, String endDate, String providerKind);

	List<PipelineHistoryInstance> getPipeHisInstanceByPipeId(Long pipeHisId);

	PipelineHistory detail(Long pipeHisId);

	PipelineHistory getById(Long pipeHisId);

}