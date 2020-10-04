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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.ci.Pcm;
import com.wl4g.components.core.bean.ci.PipeHistoryPcm;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.core.web.model.SelectionModel;
import com.wl4g.devops.ci.pcm.PcmOperator;
import com.wl4g.devops.ci.pcm.PcmOperator.PcmKind;
import com.wl4g.devops.ci.service.PcmService;
import com.wl4g.devops.dao.ci.PcmDao;
import com.wl4g.devops.page.PageModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;

import java.util.List;
import java.util.Objects;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class PcmServcieImpl implements PcmService {

	@Autowired
	private PcmDao pcmDao;

	@Autowired
	private GenericOperatorAdapter<PcmKind, PcmOperator> pcmOperator;

	@Override
	public PageModel list(PageModel pm, String name, String providerKind, Integer authType) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(pcmDao.list(getRequestOrganizationCodes(), name, providerKind, authType));
		return pm;
	}

	@Override
	public void save(Pcm pcm) {
		if (pcm.getId() == null) {
			pcm.preInsert(getRequestOrganizationCode());
			insert(pcm);
		} else {
			pcm.preUpdate();
			update(pcm);
		}
	}

	private void insert(Pcm pcm) {
		pcmDao.insertSelective(pcm);
	}

	private void update(Pcm pcm) {
		pcmDao.updateByPrimaryKeySelective(pcm);
	}

	@Override
	public void del(Long id) {
		Pcm pcm = new Pcm();
		pcm.setId(id);
		pcm.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		pcmDao.updateByPrimaryKeySelective(pcm);
	}

	@Override
	public Pcm detail(Long id) {
		return pcmDao.selectByPrimaryKey(id);
	}

	@Override
	public List<Pcm> all() {
		return pcmDao.list(getRequestOrganizationCodes(), null, null, null);
	}

	@Override
	public List<SelectionModel> getUsers(Long pcmId) {
		Pcm pcm = getPcm(pcmId);
		if (Objects.isNull(pcm)) {
			return null;
		}
		return pcmOperator.forOperator(pcm.getProviderKind()).getUsers(pcm);
	}

	@Override
	public List<SelectionModel> getProjects(Long pcmId) {
		Pcm pcm = getPcm(pcmId);
		if (Objects.isNull(pcm)) {
			return null;
		}
		return pcmOperator.forOperator(pcm.getProviderKind()).getProjects(pcm);
	}

	@Override
	public List<SelectionModel> getIssues(Long pcmId, String userId, String projectId, String search) {
		Pcm pcm = getPcm(pcmId);
		if (Objects.isNull(pcm)) {
			return null;
		}
		return pcmOperator.forOperator(pcm.getProviderKind()).getIssues(pcm, userId, projectId, search);
	}

	@Override
	public List<SelectionModel> getProjectsByPcmId(Long pcmId) {
		Pcm pcm = getPcm(pcmId);
		return pcmOperator.forOperator(pcm.getProviderKind()).getProjects(pcm);
	}

	@Override
	public List<SelectionModel> getTrackers(Long pcmId) {
		Pcm pcm = getPcm(pcmId);
		return pcmOperator.forOperator(pcm.getProviderKind()).getTracker(pcm);
	}

	@Override
	public List<SelectionModel> getStatuses(Long pcmId) {
		Pcm pcm = getPcm(pcmId);
		return pcmOperator.forOperator(pcm.getProviderKind()).getStatuses(pcm);
	}

	private Pcm getPcm(Long pcmId) {
		Pcm pcm = pcmDao.selectByPrimaryKey(pcmId);
		if (Objects.isNull(pcm)) {
			Page<Pcm> list = pcmDao.list(getRequestOrganizationCodes(), null, null, null);
			if (!CollectionUtils.isEmpty(list)) {
				pcm = list.get(0);
			}
		}
		Assert2.notNullOf(pcm, "pcm");
		return pcm;
	}

	@Override
	public List<SelectionModel> getPriorities(Long pcmId) {
		Pcm pcm = pcmDao.selectByPrimaryKey(pcmId);
		return pcmOperator.forOperator(pcm.getProviderKind()).getPriorities(pcm);
	}

	@Override
	public void createIssues(Long pcmId, PipeHistoryPcm pipeHistoryPcm) {
		Pcm pcm = pcmDao.selectByPrimaryKey(pcmId);
		pcmOperator.forOperator(pcm.getProviderKind()).createIssues(pcm, pipeHistoryPcm);

	}

}