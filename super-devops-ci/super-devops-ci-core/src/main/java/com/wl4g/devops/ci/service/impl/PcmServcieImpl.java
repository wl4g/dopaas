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
import com.wl4g.devops.ci.pcm.CompositePcmOperatorAdapter;
import com.wl4g.devops.ci.service.PcmService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Pcm;
import com.wl4g.devops.common.bean.ci.Task;
import com.wl4g.devops.common.web.model.SelectionModel;
import com.wl4g.devops.dao.ci.PcmDao;
import com.wl4g.devops.dao.ci.TaskDao;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class PcmServcieImpl implements PcmService {

	@Autowired
	private PcmDao pcmDao;

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private CompositePcmOperatorAdapter pcmOperatorAdapter;

	@Override
	public PageModel list(PageModel pm, String name, String providerKind, Integer authType) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(pcmDao.list(name, providerKind, authType));
		return pm;
	}

	@Override
	public void save(Pcm pcm) {
		if (pcm.getId() == null) {
			pcm.preInsert();
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
	public void del(Integer id) {
		Pcm pcm = new Pcm();
		pcm.setId(id);
		pcm.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		pcmDao.updateByPrimaryKeySelective(pcm);
	}

	@Override
	public Pcm detail(Integer id) {
		return pcmDao.selectByPrimaryKey(id);
	}

	@Override
	public List<Pcm> all() {
		return pcmDao.list(null, null, null);
	}

	@Override
	public List<SelectionModel> getUsers(Integer taskId) {
		Pcm pcm = getPcmKind(taskId);
		return pcmOperatorAdapter.forAdapt(pcm.getProviderKind()).getUsers(pcm);
	}

	@Override
	public List<SelectionModel> getProjects(Integer taskId) {
		Pcm pcm = getPcmKind(taskId);
		return pcmOperatorAdapter.forAdapt(pcm.getProviderKind()).getProjects(pcm);
	}

	@Override
	public List<SelectionModel> getIssues(Integer taskId, String userId, String projectId, String search) {
		Pcm pcm = getPcmKind(taskId);
		return pcmOperatorAdapter.forAdapt(pcm.getProviderKind()).getIssues(pcm,userId,projectId,search);
	}

	private Pcm getPcmKind(Integer taskId){
		Assert.notNull(taskId,"taskId is null");
		Task task = taskDao.selectByPrimaryKey(taskId);
		Assert.notNull(task,"task is null");
		Assert.notNull(task.getPcmId(),"pcmId is null");
		Assert.notNull(task.getPcmId(),"pcmId is null");
		Pcm pcm = pcmDao.selectByPrimaryKey(task.getPcmId());
		Assert.notNull(pcm,"pcm is null");
		Assert.hasText(pcm.getProviderKind(),"provide kind is null");
		return pcm;
	}

}