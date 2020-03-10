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
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.service.OrchestrationService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Orchestration;
import com.wl4g.devops.common.bean.ci.OrchestrationPipeline;
import com.wl4g.devops.dao.ci.OrchestrationDao;
import com.wl4g.devops.dao.ci.OrchestrationPipelineDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.tool.common.lang.Assert2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class OrchestrationServcieImpl implements OrchestrationService {

	@Autowired
	private OrchestrationDao orchestrationDao;

	@Autowired
	private OrchestrationPipelineDao orchestrationPipelineDao;

	@Autowired
	private FlowManager flowManager;

	@Override
	public PageModel list(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(orchestrationDao.list(name));
		return pm;
	}

	@Override
	public void save(Orchestration orchestration) {
		if (orchestration.getId() == null) {
			orchestration.preInsert();
			insert(orchestration);
		} else {
			orchestration.preUpdate();
			update(orchestration);
		}
	}

	private void insert(Orchestration orchestration) {
		insertOrUpdateOrchestrationPipelines(orchestration.getOrchestrationPipelines(),orchestration.getId());
		orchestrationDao.insertSelective(orchestration);
	}

	private void update(Orchestration orchestration) {
		insertOrUpdateOrchestrationPipelines(orchestration.getOrchestrationPipelines(),orchestration.getId());
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	private void insertOrUpdateOrchestrationPipelines(List<OrchestrationPipeline> orchestrationPipelines,
			Integer orchestrationId) {

		List<OrchestrationPipeline> oldOrchestrationPipelines = orchestrationPipelineDao.selectByOrchestrationId(orchestrationId);
		cleanOldOrchestrationPipelines(oldOrchestrationPipelines,orchestrationPipelines);

		for (OrchestrationPipeline orchestrationPipeline : orchestrationPipelines) {
			if(Objects.isNull(orchestrationPipeline.getPipelineId())){
				continue;
			}
			if (Objects.isNull(orchestrationPipeline.getId())) {
				orchestrationPipeline.preInsert();
				orchestrationPipeline.setOrchestrationId(orchestrationId);
				orchestrationPipelineDao.insertSelective(orchestrationPipeline);
			} else {
				orchestrationPipelineDao.updateByPrimaryKeySelective(orchestrationPipeline);
			}
		}
	}

	private void cleanOldOrchestrationPipelines(List<OrchestrationPipeline>  oldOrchestrationPipelines,List<OrchestrationPipeline>  newOrchestrationPipelines){
			for(OrchestrationPipeline oldOrchestrationPipeline : oldOrchestrationPipelines){
			boolean had = false;
				for(OrchestrationPipeline newOrchestrationPipeline : newOrchestrationPipelines){
					if(newOrchestrationPipeline.getId()==null){
						continue;
					}
					if(oldOrchestrationPipeline.getId().intValue() == newOrchestrationPipeline.getId().intValue()){
					had = true;
					break;
				}
			}
			if(!had){
				orchestrationPipelineDao.deleteByPrimaryKey(oldOrchestrationPipeline.getId());
			}
		}
	}

	@Override
	public void del(Integer id) {
		Orchestration orchestration = new Orchestration();
		orchestration.setId(id);
		orchestration.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	@Override
	public Orchestration detail(Integer id) {
		return orchestrationDao.selectByPrimaryKey(id);
	}

	@Override
	public void run(Integer id) {
		Assert2.notNullOf(id,"id");
		Orchestration orchestration = orchestrationDao.selectByPrimaryKey(id);
		Assert2.notNullOf(orchestration,"orchestration");
		flowManager.gateway(orchestration);
	}

}