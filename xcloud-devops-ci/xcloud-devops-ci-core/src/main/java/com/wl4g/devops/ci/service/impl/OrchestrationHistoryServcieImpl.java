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
package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.ci.OrchestrationHistory;
import com.wl4g.components.core.bean.ci.TaskHistory;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.service.OrchestrationHistoryService;
import com.wl4g.devops.dao.ci.OrchestrationHistoryDao;
import com.wl4g.devops.dao.ci.OrchestrationPipelineDao;
import com.wl4g.devops.dao.ci.PipelineHistoryDao;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class OrchestrationHistoryServcieImpl implements OrchestrationHistoryService {

	@Autowired
	private OrchestrationHistoryDao orchestrationHistoryDao;

	@Autowired
	private PipelineHistoryDao pipelineHistoryDao;

	@Autowired
	private OrchestrationPipelineDao orchestrationPipelineDao;

	@Autowired
	private FlowManager flowManager;

	@Autowired
	private JedisService jedisService;

	@Override
	public PageModel list(PageModel pm, String runId) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		List<OrchestrationHistory> list = orchestrationHistoryDao.list(getRequestOrganizationCodes(), runId);
		for(OrchestrationHistory orchestrationHistory : list){
			List<TaskHistory> taskHistories = pipelineHistoryDao.list(getRequestOrganizationCodes(),
					null, null, null, null, null, null,
					2, orchestrationHistory.getId());
			orchestrationHistory.setTaskHistories(taskHistories);
		}
		pm.setRecords(list);
		return pm;
	}



}