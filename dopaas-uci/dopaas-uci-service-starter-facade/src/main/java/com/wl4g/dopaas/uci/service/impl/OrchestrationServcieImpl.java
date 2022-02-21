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

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.support.cache.jedis.JedisService;
import com.wl4g.component.support.cache.jedis.ScanCursor;
import com.wl4g.dopaas.uci.data.OrchestrationDao;
import com.wl4g.dopaas.uci.data.OrchestrationPipelineDao;
import com.wl4g.dopaas.uci.service.OrchestrationManagerAdapter;
import com.wl4g.dopaas.uci.service.OrchestrationService;
import com.wl4g.dopaas.common.bean.uci.Orchestration;
import com.wl4g.dopaas.common.bean.uci.OrchestrationPipeline;
import com.wl4g.dopaas.common.bean.uci.model.RunModel;
import com.wl4g.dopaas.common.constant.UciConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class OrchestrationServcieImpl implements OrchestrationService {

	private @Autowired OrchestrationDao orchestrationDao;

	private @Autowired OrchestrationPipelineDao orchestrationPipelineDao;

	private @Autowired OrchestrationManagerAdapter flowManagerService;

	private @Autowired JedisService jedisService;

	@Override
	public PageHolder<Orchestration> list(PageHolder<Orchestration> pm, String name) {
		pm.useCount().bind();
		pm.setRecords(orchestrationDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public void save(Orchestration orchestration) {
		if (orchestration.getId() == null) {
			orchestration.preInsert(getRequestOrganizationCode());
			insert(orchestration);
		} else {
			orchestration.preUpdate();
			update(orchestration);
		}
	}

	private void insert(Orchestration orchestration) {
		insertOrUpdateOrchestrationPipelines(orchestration.getOrchestrationPipelines(), orchestration.getId());
		orchestrationDao.insertSelective(orchestration);
	}

	private void update(Orchestration orchestration) {
		insertOrUpdateOrchestrationPipelines(orchestration.getOrchestrationPipelines(), orchestration.getId());
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	private void insertOrUpdateOrchestrationPipelines(List<OrchestrationPipeline> orchestrationPipelines, Long orchestrationId) {

		List<OrchestrationPipeline> oldOrchestrationPipelines = orchestrationPipelineDao.selectByOrchestrationId(orchestrationId);
		cleanOldOrchestrationPipelines(oldOrchestrationPipelines, orchestrationPipelines);
		cleanRepeat(orchestrationPipelines);

		for (OrchestrationPipeline orchestrationPipeline : orchestrationPipelines) {
			if (Objects.isNull(orchestrationPipeline.getPipelineId())) {
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

	private void cleanOldOrchestrationPipelines(List<OrchestrationPipeline> oldOrchestrationPipelines,
			List<OrchestrationPipeline> newOrchestrationPipelines) {
		for (OrchestrationPipeline oldOrchestrationPipeline : oldOrchestrationPipelines) {
			boolean had = false;
			for (OrchestrationPipeline newOrchestrationPipeline : newOrchestrationPipelines) {
				if (newOrchestrationPipeline.getId() == null) {
					continue;
				}
				if (oldOrchestrationPipeline.getId().longValue() == newOrchestrationPipeline.getId().longValue()) {
					had = true;
					break;
				}
			}
			if (!had) {
				orchestrationPipelineDao.deleteByPrimaryKey(oldOrchestrationPipeline.getId());
			}
		}
	}

	private void cleanRepeat(List<OrchestrationPipeline> orchestrationPipelines) {
		List<OrchestrationPipeline> needRemove = new ArrayList<>();
		for (int i = 0; i < orchestrationPipelines.size(); i++) {
			for (int j = i + 1; j < orchestrationPipelines.size(); j++) {
				if (orchestrationPipelines.get(i).getPipelineId().longValue() == orchestrationPipelines.get(j).getPipelineId()
						.longValue()) {
					needRemove.add(orchestrationPipelines.get(i));
				}
			}
		}
		orchestrationPipelines.removeAll(needRemove);
	}

	@Override
	public void del(Long id) {
		Orchestration orchestration = new Orchestration();
		orchestration.setId(id);
		orchestration.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	@Override
	public Orchestration detail(Long id) {
		return orchestrationDao.selectByPrimaryKey(id);
	}

	@Override
	public void run(Long id, String remark, String taskTraceId, String taskTraceType, String annex) {
		Assert2.notNullOf(id, "id");
		Assert2.isTrue(!isMaxRuner(), "Runner is biggest , cant not create any more");
		Orchestration orchestration = orchestrationDao.selectByPrimaryKey(id);
		Assert2.notNullOf(orchestration, "orchestration");
		orchestration.setStatus(1);
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
		flowManagerService.runOrchestration(orchestration, remark, taskTraceId, taskTraceType, annex);
	}

	private boolean isMaxRuner() {
		ScanCursor<RunModel> scan = jedisService.scan(UciConstants.REDIS_CI_RUN_PRE, UciConstants.REDIS_CI_RUN_SCAN_BATCH + 1,
				RunModel.class);
		int count = 0;
		while (scan.hasNext()) {
			scan.next();
			count++;
			if (count >= UciConstants.REDIS_CI_RUN_SCAN_BATCH) {
				return true;
			}
		}
		return false;
	}

}