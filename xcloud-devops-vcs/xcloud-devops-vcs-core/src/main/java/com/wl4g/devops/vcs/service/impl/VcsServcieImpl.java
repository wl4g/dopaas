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
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.ci.Vcs;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.dao.ci.VcsDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.config.VcsProperties;
import com.wl4g.devops.vcs.operator.VcsOperator;
import com.wl4g.devops.vcs.operator.VcsOperator.VcsProviderKind;
import com.wl4g.devops.vcs.operator.model.*;
import com.wl4g.devops.vcs.service.VcsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
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

	@Autowired
	private VcsProperties vcsProperties;

	@Override
	public PageModel list(PageModel pm, String name, String providerKind, Integer authType) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(vcsDao.list(getRequestOrganizationCodes(), name, providerKind, authType));
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
	public void del(Long id) {
		Vcs vcs = new Vcs();
		vcs.setId(id);
		vcs.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		vcsDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public Vcs detail(Long id) {
		return vcsDao.selectByPrimaryKey(id);
	}

	@Override
	public List<Vcs> all() {
		return vcsDao.list(getRequestOrganizationCodes(), null, null, null);
	}

	@Override
	public List<VcsGroupModel> getGroups(Long id, String groupName) {
		Vcs vcs = vcsDao.selectByPrimaryKey(id);
		Assert2.notNullOf(vcs, "vcs");
		return vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteGroups(vcs, groupName);
	}

	public List<CompositeBasicVcsProjectModel> getProjectsToCompositeBasic(Long vcsId, String projectName) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);

		// Search remote projects.
		List<VcsProjectModel> projects = vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteProjects(vcs, null,
				projectName, null);
		return safeList(projects).stream().map(p -> p.toCompositeVcsProject()).collect(toList());
	}

	public List<VcsProjectModel> getProjects(PageModel pm, Long vcsId, Long groupId, String projectName) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");

		// Search remote projects.
		return vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteProjects(vcs, groupId, projectName, pm);
	}

	public VcsProjectModel getProjectById(Long vcsId, Long projectId) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");

		// Search remote projects.
		return vcsOperator.forOperator(vcs.getProviderKind()).searchRemoteProjectsById(vcs, projectId);
	}

	@Override
	public List<VcsBranchModel> getBranchs(Long vcsId, Long projectId) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		return vcsOperator.forOperator(vcs.getProviderKind()).getRemoteBranchs(vcs, projectId);
	}

	@Override
	public List<VcsTagModel> getTags(Long vcsId, Long projectId) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		return vcsOperator.forOperator(vcs.getProviderKind()).getRemoteTags(vcs, projectId);
	}

	@Override
	public VcsBranchModel createBranch(Long vcsId, Long projectId, String branch, String ref) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		// TODO repeat branch or tag
		checkRepeatBranchOrTag(vcs, projectId, branch);

		// check format
		String pattern = vcsProperties.getBranchFormat();
		Assert2.isTrue(Pattern.matches(pattern, branch), "not match format,format=" + pattern);

		return vcsOperator.forOperator(vcs.getProviderKind()).createRemoteBranch(vcs, projectId, branch, ref);
	}

	@Override
	public VcsTagModel createTag(Long vcsId, Long projectId, String tag, String ref, String message, String releaseDescription) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		// check repeat branch or tag
		checkRepeatBranchOrTag(vcs, projectId, tag);

		// check format
		String pattern = vcsProperties.getTagFormat();
		Assert2.isTrue(Pattern.matches(pattern, tag), "not match format,format=" + pattern);

		return vcsOperator.forOperator(vcs.getProviderKind()).createRemoteTag(vcs, projectId, tag, ref, message,
				releaseDescription);
	}

	private void checkRepeatBranchOrTag(Vcs vcs, Long projectId, String branchOrTag) {
		Assert2.hasTextOf(branchOrTag, "branchOrTag");
		List<VcsBranchModel> remoteBranchs = vcsOperator.forOperator(vcs.getProviderKind()).getRemoteBranchs(vcs, projectId);
		List<VcsTagModel> remoteTags = vcsOperator.forOperator(vcs.getProviderKind()).getRemoteTags(vcs, projectId);
		// check repeart
		for (VcsBranchModel vcsBranchModel : remoteBranchs) {
			Assert2.isTrue(!StringUtils.equals(vcsBranchModel.getName(), branchOrTag),
					"had the same name of branch:" + branchOrTag);
		}
		for (VcsTagModel vcsTagModel : remoteTags) {
			Assert2.isTrue(!StringUtils.equals(vcsTagModel.getName(), branchOrTag), "had the same name of tag:" + branchOrTag);
		}
	}

}