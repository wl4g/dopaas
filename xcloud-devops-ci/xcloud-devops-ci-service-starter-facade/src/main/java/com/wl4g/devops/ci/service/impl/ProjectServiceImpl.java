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
package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.core.web.model.PageModel;
import com.wl4g.devops.ci.data.DependencyDao;
import com.wl4g.devops.ci.data.ProjectDao;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.vcs.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.VcsOperator;
import com.wl4g.devops.vcs.operator.VcsOperator.VcsProviderKind;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.components.core.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.components.core.bean.BaseBean.ENABLED;
import static com.wl4g.devops.common.constant.CiConstants.TASK_LOCK_STATUS_UNLOCK;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCodes;

/**
 * @author vjay
 * @date 2019-05-17 10:24:00
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private GenericOperatorAdapter<VcsProviderKind, VcsOperator> vcsOperator;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private DependencyDao dependencyDao;

	@Override
	public void save(Project project) {
		if (null != project.getId() && project.getId() > 0) {
			project.preUpdate();
			update(project);
		} else {
			project.preInsert(getRequestOrganizationCode());
			project.setDelFlag(DEL_FLAG_NORMAL);
			project.setEnable(ENABLED);
			project.setLockStatus(TASK_LOCK_STATUS_UNLOCK);
			insert(project);
		}
	}

	@Transactional
	public int insert(Project project) {
		Project hasProject = projectDao.getByAppClusterId(project.getAppClusterId());
		// check repeated
		Assert.state(hasProject == null, "Config Repeated");
		int result = projectDao.insertSelective(project);
		if (project.getDependencies() != null) {
			for (Dependency dependency : project.getDependencies()) {
				if (dependency.getDependentId() != null) {
					dependency.setProjectId(project.getId());
					dependency.preInsert();
					dependencyDao.insertSelective(dependency);
				}
			}
		}
		return result;
	}

	@Transactional
	public int update(Project project) {
		Project hasProject = projectDao.getByAppClusterId(project.getAppClusterId());
		// check repeated
		Assert.state(hasProject == null || hasProject.getId().longValue() == project.getId().longValue(), "Config Repeated");
		project.preUpdate();
		int result = projectDao.updateByPrimaryKeySelective(project);
		dependencyDao.deleteByProjectId(project.getId());
		if (project.getDependencies() != null) {
			for (Dependency dependency : project.getDependencies()) {
				if (dependency.getDependentId() != null) {
					dependency.setProjectId(project.getId());
					dependency.preInsert();
					dependencyDao.insertSelective(dependency);
				}
			}
		}
		return result;
	}

	@Override
	public int deleteById(Long id) {
		Project project = new Project();
		project.preUpdate();
		project.setId(id);
		project.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public int removeById(Long id) {
		return projectDao.deleteByPrimaryKey(id);
	}

	@Override
	public PageModel<Project> list(PageModel<Project> pm, String groupName, String projectName) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		List<Project> list = projectDao.list(getRequestOrganizationCodes(), groupName, projectName, null);
		for (Project project : list) {
			project.setVcs(null);
		}
		pm.setRecords(list);
		return pm;
	}

	@Override
	public List<Project> getBySelect(Integer isBoot) {
		return projectDao.list(getRequestOrganizationCodes(), null, null, isBoot);
	}

	@Override
	public Project selectByPrimaryKey(Long id) {
		Project project = projectDao.selectByPrimaryKey(id);
		List<Dependency> dependencies = dependencyDao.getParentsByProjectId(project.getId());
		project.setDependencies(dependencies);
		return project;
	}

	@Override
	public Project getByAppClusterId(Long appClusteId) {
		return projectDao.getByAppClusterId(appClusteId);
	}

	@Override
	public int updateLockStatus(Long id, Integer lockStatus) {
		Project project = new Project();
		project.setId(id);
		project.setLockStatus(lockStatus);
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public List<String> getBranchs(Long appClusterId, Integer tagOrBranch) throws Exception {
		Assert.notNull(appClusterId, "id can not be null");
		Project project = projectDao.getByAppClusterId(appClusterId);
		buildVcsProject(project);
		Assert.notNull(project, "not found project ,please check you project config");
		return getBranchByProject(project, tagOrBranch);
	}

	@Override
	public List<String> getBranchsByProjectId(Long projectId, Integer tagOrBranch) throws Exception {
		Assert.notNull(projectId, "id can not be null");
		Project project = projectDao.selectByPrimaryKey(projectId);
		buildVcsProject(project);
		Assert.notNull(project, "not found project ,please check you project config");
		return getBranchByProject(project, tagOrBranch);
	}

	private List<String> getBranchByProject(Project project, Integer tagOrBranch) throws Exception {

		List<String> result = new ArrayList<>();
		if (tagOrBranch != null && tagOrBranch == 2) { // tag
			List<VcsTagModel> remoteTags = vcsOperator.forOperator(project.getVcs().getProviderKind())
					.getRemoteTags(project.getVcs(), project.getVcsProject());
			for (VcsTagModel vcsTagModel : remoteTags) {
				result.add(vcsTagModel.getName());
			}
		}
		// Branch
		else {
			List<VcsBranchModel> remoteBranchs = vcsOperator.forOperator(project.getVcs().getProviderKind())
					.getRemoteBranchs(project.getVcs(), project.getVcsProject());
			for (VcsBranchModel vcsBranchModel : remoteBranchs) {
				result.add(vcsBranchModel.getName());
			}
		}
		return result;
	}

	private void buildVcsProject(Project project) {
		String gitInfo = project.getGitInfo();
		if (StringUtils.isNotBlank(gitInfo)) {
			CompositeBasicVcsProjectModel compositeBasicVcsProjectModel = JacksonUtils.parseJSON(gitInfo,
					CompositeBasicVcsProjectModel.class);
			project.setVcsProject(compositeBasicVcsProjectModel);
		}
	}

}