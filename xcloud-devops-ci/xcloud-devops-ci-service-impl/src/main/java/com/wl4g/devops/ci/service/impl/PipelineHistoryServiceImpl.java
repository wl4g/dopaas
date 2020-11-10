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
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.components.support.cli.DestroableProcessManager;
import com.wl4g.components.support.cli.destroy.DestroySignal;
import com.wl4g.devops.ci.dao.PipelineDao;
import com.wl4g.devops.ci.dao.PipelineHistoryDao;
import com.wl4g.devops.ci.dao.PipelineHistoryInstanceDao;
import com.wl4g.devops.ci.dao.PipelineInstanceDao;
import com.wl4g.devops.ci.service.PipelineHistoryService;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.common.bean.ci.PipelineInstance;
import com.wl4g.devops.common.bean.ci.param.HookParameter;
import com.wl4g.devops.common.bean.ci.param.RollbackParameter;
import com.wl4g.devops.common.bean.ci.param.RunParameter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.wl4g.components.core.constants.CiDevOpsConstants.TASK_STATUS_CREATE;
import static com.wl4g.components.core.constants.CiDevOpsConstants.TASK_STATUS_STOPING;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;

/**
 * @author vjay
 * @date 2020-04-27 17:25:00
 */
@Service
public class PipelineHistoryServiceImpl implements PipelineHistoryService {

	@Autowired
	private PipelineDao pipelineDao;
	@Autowired
	private PipelineHistoryDao pipelineHistoryDao;
	@Autowired
	private PipelineHistoryInstanceDao pipelineHistoryInstanceDao;
	@Autowired
	protected DestroableProcessManager pm;
	@Autowired
	private PipelineInstanceDao pipelineInstanceDao;

	// --- Create Pipe Runner. ---

	@Override
	public PipelineHistory createRunnerPipeline(RunParameter param) {
		Assert2.notNullOf(param, "newParameter");
		Long pipeId = param.getPipeId();
		String traceId = param.getTrackId();
		String traceType = param.getTrackType();
		String remark = param.getRemark();
		String annex = param.getAnnex();

		Assert2.notNullOf(pipeId, "pipeId");
		Pipeline pipeline = pipelineDao.selectByPrimaryKey(pipeId);
		Assert2.notNullOf(pipeline, "pipeline");

		PipelineHistory pipelineHistory = new PipelineHistory();
		pipelineHistory.preInsert(pipeline.getOrganizationCode());
		pipelineHistory.setPipeId(pipeId);
		pipelineHistory.setProviderKind(pipeline.getProviderKind());
		pipelineHistory.setAnnex(annex);
		pipelineHistory.setStatus(TASK_STATUS_CREATE);
		pipelineHistory.setTrackId(traceId);
		pipelineHistory.setTrackType(traceType);
		pipelineHistory.setRemark(remark);

		pipelineHistory.setOrchestrationType(param.getOrchestrationType());
		pipelineHistory.setOrchestrationId(param.getOrchestrationId());

		pipelineHistoryDao.insertSelective(pipelineHistory);
		createPipeHistoryInstance(pipeline.getId(), pipelineHistory.getId());
		return pipelineHistory;

	}

	@Override
	public PipelineHistory createHookPipeline(HookParameter param) {
		Long pipeId = param.getPipeId();
		String remark = param.getRemark();
		RunParameter newParameter = new RunParameter(pipeId, remark, null, null, null);

		Assert2.notNullOf(pipeId, "pipeId");
		Pipeline pipeline = pipelineDao.selectByPrimaryKey(pipeId);
		Assert2.notNullOf(pipeline, "pipeline");

		PipelineHistory pipelineHistory = new PipelineHistory();
		pipelineHistory.preInsert(getRequestOrganizationCode());
		pipelineHistory.setPipeId(pipeId);
		pipelineHistory.setProviderKind(pipeline.getProviderKind());
		pipelineHistory.setAnnex(null);
		pipelineHistory.setStatus(TASK_STATUS_CREATE);
		pipelineHistory.setTrackId(null);
		pipelineHistory.setTrackType(null);
		pipelineHistory.setRemark(remark);

		pipelineHistoryDao.insertSelective(pipelineHistory);
		createPipeHistoryInstance(pipeline.getId(), pipelineHistory.getId());

		return createRunnerPipeline(newParameter);
	}

	@Override
	public PipelineHistory createRollbackPipeline(RollbackParameter param) {
		Assert2.notNullOf(param, "rollbackParameter");
		Long pipeId = param.getPipeId();
		Assert2.notNullOf(pipeId, "pipeId");
		String remark = param.getRemark();

		PipelineHistory oldPipelineHistory = pipelineHistoryDao.selectByPrimaryKey(pipeId);
		Assert2.notNullOf(oldPipelineHistory, "pipelineHistory");

		Long oldPipeId = oldPipelineHistory.getPipeId();
		Pipeline pipeline = pipelineDao.selectByPrimaryKey(oldPipeId);
		Assert2.notNullOf(pipeline, "pipeline");

		PipelineHistory pipelineHistory = new PipelineHistory();
		BeanUtils.copyProperties(oldPipelineHistory, pipelineHistory);
		pipelineHistory.preInsert(getRequestOrganizationCode());
		pipelineHistory.setPipeId(oldPipeId);
		pipelineHistory.setProviderKind(pipeline.getProviderKind());
		pipelineHistory.setStatus(TASK_STATUS_CREATE);
		pipelineHistory.setRemark(remark);
		pipelineHistory.setRefId(pipeId);

		pipelineHistoryDao.insertSelective(pipelineHistory);
		createPipeHistoryInstance(pipeline.getId(), pipelineHistory.getId());
		return pipelineHistory;

	}

	// --- CRUD. ---

	private void createPipeHistoryInstance(Long pipeId, Long pipeHisId) {
		List<PipelineInstance> pipelineInstances = pipelineInstanceDao.selectByPipeId(pipeId);
		if (!CollectionUtils.isEmpty(pipelineInstances)) {
			for (PipelineInstance pipelineInstance : pipelineInstances) {
				PipelineHistoryInstance pipelineHistoryInstance = new PipelineHistoryInstance();
				pipelineHistoryInstance.preInsert();
				pipelineHistoryInstance.setStatus(TASK_STATUS_CREATE);
				pipelineHistoryInstance.setInstanceId(pipelineInstance.getInstanceId());
				pipelineHistoryInstance.setPipeHistoryId(pipeHisId);
				pipelineHistoryInstanceDao.insertSelective(pipelineHistoryInstance);
			}
		}
	}

	@Override
	public void updateStatus(Long pipeId, int status) {
		PipelineHistory pipelineHistory = new PipelineHistory();
		pipelineHistory.preUpdate();
		pipelineHistory.setId(pipeId);
		pipelineHistory.setStatus(status);
		pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);
	}

	@Override
	public void updateStatusAndResultAndSha(Long pipeId, int status, String sha) {
		PipelineHistory pipelineHistory = new PipelineHistory();
		pipelineHistory.preUpdate();
		pipelineHistory.setId(pipeId);
		pipelineHistory.setStatus(status);
		pipelineHistory.setShaLocal(sha);
		pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);
	}

	@Override
	public void stopByPipeHisId(Long pipeHisId) {
		PipelineHistory pipelineHistory = new PipelineHistory();
		pipelineHistory.preUpdate();
		pipelineHistory.setId(pipeHisId);
		pipelineHistory.setStatus(TASK_STATUS_STOPING);
		pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);

		// TODO timeoutMs?
		pm.destroyForComplete(new DestroySignal(String.valueOf(pipeHisId), 5000L));
	}

	@Override
	public void updateCostTime(Long taskId, long costTime) {
		PipelineHistory pipelineHistory = new PipelineHistory();
		pipelineHistory.preUpdate();
		pipelineHistory.setId(taskId);
		pipelineHistory.setCostTime(costTime);
		pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);
	}

	@Override
	public PageModel<PipelineHistory> list(PageModel<PipelineHistory> pm, String pipeName, String clusterName, String environment,
			String startDate, String endDate, String providerKind) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(pipelineHistoryDao.list(getRequestOrganizationCodes(), pipeName, clusterName, environment, startDate,
				endDate, providerKind, null, null));
		return pm;
	}

	@Override
	public List<PipelineHistoryInstance> getPipeHisInstanceByPipeId(Long pipeId) {
		return pipelineHistoryInstanceDao.selectByPipeHistoryId(pipeId);
	}

	@Override
	public PipelineHistory detail(Long pipeId) {
		PipelineHistory pipelineHistory = pipelineHistoryDao.selectByPrimaryKey(pipeId);
		List<PipelineHistoryInstance> pipelineHistoryInstances = pipelineHistoryInstanceDao.selectByPipeHistoryId(pipeId);
		pipelineHistory.setPipelineHistoryInstances(pipelineHistoryInstances);
		return pipelineHistory;
	}

	@Override
	public PipelineHistory getById(Long pipeHisId) {
		return pipelineHistoryDao.selectByPrimaryKey(pipeHisId);
	}

	@Override
	public void updatePipeHisInstanceStatus(Long pipeInstanceId, int status) {
		PipelineHistoryInstance pipelineHistoryInstance = new PipelineHistoryInstance();
		pipelineHistoryInstance.preUpdate();
		pipelineHistoryInstance.setId(pipeInstanceId);
		pipelineHistoryInstance.setStatus(status);
		pipelineHistoryInstanceDao.updateByPrimaryKeySelective(pipelineHistoryInstance);
	}

}