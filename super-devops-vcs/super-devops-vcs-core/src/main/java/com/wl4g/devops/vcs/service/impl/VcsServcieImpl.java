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
package com.wl4g.devops.vcs.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.dao.ci.VcsDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.vcs.operator.VcsOperator;
import com.wl4g.devops.vcs.operator.VcsOperator.VcsProviderKind;
import com.wl4g.devops.vcs.operator.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsGroupModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;
import com.wl4g.devops.vcs.service.VcsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.util.stream.Collectors.toList;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class VcsServcieImpl implements VcsService {

	@Autowired
	private VcsDao vcsDao;

	@Autowired
	private GenericOperatorAdapter<VcsProviderKind, VcsOperator> vcsOperator;

	@Override
	public PageModel list(PageModel pm, String name, String providerKind, Integer authType) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(vcsDao.list(name, providerKind, authType));
		return pm;
	}

	@Override
	public void save(Vcs vcs) {
		if (vcs.getId() == null) {
			vcs.preInsert();
			insert(vcs);
		} else {
			vcs.preUpdate();
			update(vcs);
		}
	}

	private void insert(Vcs vcs) {
		vcsDao.insertSelective(vcs);
	}

	private void update(Vcs vcs) {
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public void del(Integer id) {
		Vcs vcs = new Vcs();
		vcs.setId(id);
		vcs.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public Vcs detail(Integer id) {
		return vcsDao.selectByPrimaryKey(id);
	}

	@Override
	public List<Vcs> all() {
		return vcsDao.list(null, null, null);
	}

	@Override
	public List<VcsGroupModel> getGroups(Integer id, String groupName) {
		Vcs vcs = vcsDao.selectByPrimaryKey(id);
		Assert2.notNullOf(vcs,"vcs");
		return vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteGroups(vcs, groupName);
	}

	public List<CompositeBasicVcsProjectModel> getProjectsToCompositeBasic(Integer vcsId, String projectName) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);

		// Search remote projects.
		List<VcsProjectModel> projects = vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteProjects(vcs, null, projectName,null);
		return safeList(projects).stream().map(p -> p.toCompositeVcsProject()).collect(toList());
	}

	public List<VcsProjectModel> getProjects(PageModel pm, Integer vcsId, Integer groupId, String projectName) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs,"vcs");

		// Search remote projects.
		List<VcsProjectModel> projects = vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteProjects(vcs, groupId, projectName,pm);
		return projects;
	}


	public void test(){
		Vcs vcs = new Vcs();
		vcsOperator.forOperator("gitlab").getRemoteBranchNames(vcs, 1);
	}

}