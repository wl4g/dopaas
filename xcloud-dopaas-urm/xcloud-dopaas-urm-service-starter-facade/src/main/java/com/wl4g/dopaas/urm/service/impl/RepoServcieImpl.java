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
package com.wl4g.dopaas.urm.service.impl;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.urm.SourceRepo;
import com.wl4g.dopaas.common.bean.urm.model.CompositeBasicVcsProjectModel;
import com.wl4g.dopaas.urm.config.RepoProperties;
import com.wl4g.dopaas.urm.data.RepoDao;
import com.wl4g.dopaas.urm.operator.VcsOperator;
import com.wl4g.dopaas.urm.operator.VcsOperator.SearchMeta;
import com.wl4g.dopaas.urm.operator.VcsOperator.VcsProviderKind;
import com.wl4g.dopaas.urm.operator.model.VcsBranchModel;
import com.wl4g.dopaas.urm.operator.model.VcsGroupModel;
import com.wl4g.dopaas.urm.operator.model.VcsProjectModel;
import com.wl4g.dopaas.urm.operator.model.VcsTagModel;
import com.wl4g.dopaas.urm.service.RepoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.stream.Collectors.toList;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class RepoServcieImpl implements RepoService {

	@Autowired
	private RepoDao repoDao;

	@Autowired
	private GenericOperatorAdapter<VcsProviderKind, VcsOperator> vcsManager;

	@Autowired
	private RepoProperties vcsProperties;

	@Override
	public PageHolder<SourceRepo> list(PageHolder<SourceRepo> pm, String name, String providerKind, Integer authType) {
		pm.useCount().bindPage();
		pm.setRecords(repoDao.list(getRequestOrganizationCodes(), name, providerKind, authType));
		return pm;
	}

	@Override
	public void save(SourceRepo vcs) {
		if (vcs.getId() == null) {
			vcs.preInsert(getRequestOrganizationCode());
			insert(vcs);
		} else {
			vcs.preUpdate();
			update(vcs);
		}
	}

	private void insert(SourceRepo vcs) {
		repoDao.insertSelective(vcs);
	}

	private void update(SourceRepo vcs) {
		repoDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public void del(Long id) {
		SourceRepo vcs = new SourceRepo();
		vcs.setId(id);
		vcs.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		repoDao.updateByPrimaryKeySelective(vcs);
	}

	@Override
	public SourceRepo detail(Long id) {
		return repoDao.selectByPrimaryKey(id);
	}

	@Override
	public List<SourceRepo> all() {
		return repoDao.list(getRequestOrganizationCodes(), null, null, null);
	}

	@Override
	public List<VcsGroupModel> getGroups(Long id, String groupName) {
		SourceRepo vcs = repoDao.selectByPrimaryKey(id);
		Assert2.notNullOf(vcs, "vcs");
		return vcsManager.forOperator(vcs.getProviderKind()).searchRemoteGroups(vcs, groupName);
	}

	public List<CompositeBasicVcsProjectModel> getProjectsToCompositeBasic(Long vcsId, String projectName) throws Exception {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);

		// Search remote projects.
		List<VcsProjectModel> projects = vcsManager.forOperator(vcs.getProviderKind()).searchRemoteProjects(vcs, null,
				projectName, SearchMeta.MAX_LIMIT);
		return safeList(projects).stream().map(p -> p.toCompositeVcsProject()).collect(toList());
	}

	public List<VcsProjectModel> getProjects(PageHolder<?> pm, Long vcsId, Long groupId, String projectName) throws Exception {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");

		// Search remote projects.
		return vcsManager.forOperator(vcs.getProviderKind()).searchRemoteProjects(vcs, groupId, projectName,
				new SearchMeta(pm.getPageNum(), pm.getPageSize()));
	}

	public VcsProjectModel getProjectById(Long vcsId, Long projectId) {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");

		// Search remote projects.
		return vcsManager.forOperator(vcs.getProviderKind()).searchRemoteProjectsById(vcs, projectId);
	}

	@Override
	public List<VcsBranchModel> getBranchs(Long vcsId, Long projectId) throws Exception {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		return vcsManager.forOperator(vcs.getProviderKind()).getRemoteBranchs(vcs, new CompositeBasicVcsProjectModel(projectId));
	}

	@Override
	public List<VcsTagModel> getTags(Long vcsId, Long projectId) throws Exception {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		return vcsManager.forOperator(vcs.getProviderKind()).getRemoteTags(vcs, new CompositeBasicVcsProjectModel(projectId));
	}

	@Override
	public VcsBranchModel createBranch(Long vcsId, Long projectId, String branch, String ref) throws Exception {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		// TODO repeat branch or tag
		checkRepeatBranchOrTag(vcs, projectId, branch);

		// check format
		String pattern = vcsProperties.getBranchFormat();
		Assert2.isTrue(Pattern.matches(pattern, branch), "not match format,format=" + pattern);

		return vcsManager.forOperator(vcs.getProviderKind()).createRemoteBranch(vcs, projectId, branch, ref);
	}

	@Override
	public VcsTagModel createTag(Long vcsId, Long projectId, String tag, String ref, String message, String releaseDescription)
			throws Exception {
		notNullOf(vcsId, "vcsId");
		// Gets VCS information.
		SourceRepo vcs = repoDao.selectByPrimaryKey(vcsId);
		Assert2.notNullOf(vcs, "vcs");
		// check repeat branch or tag
		checkRepeatBranchOrTag(vcs, projectId, tag);

		// check format
		String pattern = vcsProperties.getTagFormat();
		Assert2.isTrue(Pattern.matches(pattern, tag), "not match format,format=" + pattern);

		return vcsManager.forOperator(vcs.getProviderKind()).createRemoteTag(vcs, projectId, tag, ref, message,
				releaseDescription);
	}

	private void checkRepeatBranchOrTag(SourceRepo vcs, Long projectId, String branchOrTag) throws Exception {
		Assert2.hasTextOf(branchOrTag, "branchOrTag");
		List<VcsBranchModel> remoteBranchs = vcsManager.forOperator(vcs.getProviderKind()).getRemoteBranchs(vcs,
				new CompositeBasicVcsProjectModel(projectId));
		List<VcsTagModel> remoteTags = vcsManager.forOperator(vcs.getProviderKind()).getRemoteTags(vcs,
				new CompositeBasicVcsProjectModel(projectId));
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