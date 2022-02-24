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
package com.wl4g.dopaas.uci.service.impl;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.uci.OrchestrationHistory;
import com.wl4g.dopaas.common.bean.uci.PipelineHistory;
import com.wl4g.dopaas.uci.data.OrchestrationHistoryDao;
import com.wl4g.dopaas.uci.service.OrchestrationHistoryService;
import com.wl4g.dopaas.uci.service.PipelineHistoryService;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class OrchestrationHistoryServcieImpl implements OrchestrationHistoryService {

	private @Autowired OrchestrationHistoryDao orchestrationHistoryDao;
	private @Autowired PipelineHistoryService pipelineHistoryService;

	@Override
	public PageHolder<OrchestrationHistory> list(PageHolder<OrchestrationHistory> pm, String runId) {
		pm.useCount().bind();
		List<OrchestrationHistory> list = orchestrationHistoryDao.list(getRequestOrganizationCodes(), runId);
		for (OrchestrationHistory orch : list) {
			List<PipelineHistory> pipeHis = pipelineHistoryService.listWithoutPage(null, null, null, null, null, null, null, 2,
					orch.getId());
			orch.setPipeHistories(pipeHis);
		}
		pm.setRecords(list);
		return pm;
	}

}