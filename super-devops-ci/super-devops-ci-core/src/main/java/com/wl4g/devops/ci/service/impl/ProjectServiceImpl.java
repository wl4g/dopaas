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
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.VcsDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.operator.VcsOperator;
import com.wl4g.devops.vcs.operator.model.VcsBranchModel;
import com.wl4g.devops.vcs.operator.model.VcsTagModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS_UNLOCK;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;

/**
 * @author vjay
 * @date 2019-05-17 10:24:00
 */
@Service
public class ProjectServiceImpl implements ProjectService {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private DependencyDao dependencyDao;

	@Autowired
	private GenericOperatorAdapter<VcsOperator.VcsProviderKind, VcsOperator> vcsOperator;

	@Autowired
	private VcsDao vcsDao;

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
		Assert.state(hasProject == null || hasProject.getId().intValue() == project.getId().intValue(), "Config Repeated");
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
	public int deleteById(Integer id) {
		Project project = new Project();
		project.preUpdate();
		project.setId(id);
		project.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public int removeById(Integer id) {
		return projectDao.deleteByPrimaryKey(id);
	}

	@Override
	public PageModel list(PageModel pm, String groupName, String projectName) {
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
		return projectDao.list(getRequestOrganizationCodes(),null, null, isBoot);
	}

	@Override
	public Project selectByPrimaryKey(Integer id) {
		Project project = projectDao.selectByPrimaryKey(id);
		List<Dependency> dependencies = dependencyDao.getParentsByProjectId(project.getId());
		project.setDependencies(dependencies);
		return project;
	}

	@Override
	public Project getByAppClusterId(Integer appClusteId) {
		return projectDao.getByAppClusterId(appClusteId);
	}

	@Override
	public int updateLockStatus(Integer id, Integer lockStatus) {
		Project project = new Project();
		project.setId(id);
		project.setLockStatus(lockStatus);
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public List<String> getBranchs(Integer appClusterId, Integer tagOrBranch) {
		Assert.notNull(appClusterId, "id can not be null");
		Project project = projectDao.getByAppClusterId(appClusterId);
		Assert.notNull(project, "not found project ,please check you project config");
		return getBranchByProject(project,tagOrBranch);
	}

	@Override
	public List<String> getBranchsByProjectId(Integer projectId, Integer tagOrBranch) {
		Assert.notNull(projectId, "id can not be null");
		Project project = projectDao.selectByPrimaryKey(projectId);
		Assert.notNull(project, "not found project ,please check you project config");
		return getBranchByProject(project,tagOrBranch);
	}


	private List<String> getBranchByProject(Project project, Integer tagOrBranch){
		String url = project.getHttpUrl();
		// Find remote projectIds.
		String projectName = extProjectName(url);
		vcsOperator.forOperator(project.getVcs().getProviderKind());
		Integer vcsProjectId = vcsOperator.forOperator(project.getVcs().getProviderKind()).getRemoteProjectId(project.getVcs(),
				projectName);
		notNullOf(vcsProjectId, "vcsProjectId");

		List<String> result = new ArrayList<>();
		if (tagOrBranch != null && tagOrBranch == 2) { // tag
			List<VcsTagModel> remoteTags = vcsOperator.forOperator(project.getVcs().getProviderKind()).getRemoteTags(project.getVcs(),
					vcsProjectId);
			for(VcsTagModel vcsTagModel : remoteTags){
				result.add(vcsTagModel.getName());
			}
		}
		// Branch
		else {
			List<VcsBranchModel> remoteBranchs = vcsOperator.forOperator(project.getVcs().getProviderKind())
					.getRemoteBranchs(project.getVcs(), vcsProjectId);
			for(VcsBranchModel vcsBranchModel : remoteBranchs){
				result.add(vcsBranchModel.getName());
			}
		}
		return result;
	}

	/**
	 * Tool for this class : get Git Project Name from Url
	 *
	 * @param url
	 * @return
	 */
	private static String extProjectName(String url) {
		int index = url.lastIndexOf("/");
		url = url.substring(index + 1);
		index = url.lastIndexOf(".");
		url = url.substring(0, index);
		return url;
	}

}