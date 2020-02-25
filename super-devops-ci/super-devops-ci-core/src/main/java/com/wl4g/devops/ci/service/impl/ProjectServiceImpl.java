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
import com.wl4g.devops.ci.vcs.CompositeVcsOperateAdapter;
import com.wl4g.devops.ci.vcs.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.ci.vcs.model.VcsProjectModel;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.dao.ci.VcsDao;
import com.wl4g.devops.page.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS_UNLOCK;
import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notNull;

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
	private CompositeVcsOperateAdapter vcsAdapter;

	@Autowired
	private VcsDao vcsDao;

	@Override
	public void save(Project project) {
		if (null != project.getId() && project.getId() > 0) {
			project.preUpdate();
			update(project);
		} else {
			project.preInsert();
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
		project.preInsert();
		int result = projectDao.insertSelective(project);
		if (project.getDependencies() != null) {
			for (Dependency dependency : project.getDependencies()) {
				if (dependency.getDependentId() != null && StringUtils.isNotBlank(dependency.getBranch())) {
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
				if (dependency.getDependentId() != null && StringUtils.isNotBlank(dependency.getBranch())) {
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
		List<Project> list = projectDao.list(groupName, projectName);
		for (Project project : list) {
			project.setVcs(null);
		}
		pm.setRecords(list);
		return pm;
	}

	@Override
	public List<Project> all() {
		return projectDao.list(null, null);
	}

	@Override
	public Project selectByPrimaryKey(Integer id) {
		Project project = projectDao.selectByPrimaryKey(id);
		List<Dependency> dependencies = dependencyDao.getParentsByProjectId(project.getId());
		project.setDependencies(dependencies);
		return project;
	}

	@Override
	public int updateLockStatus(Integer id, Integer lockStatus) {
		Project project = new Project();
		project.setId(id);
		project.setLockStatus(lockStatus);
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public List<String> getBranchs(Integer appClusterId, Integer tarOrBranch) {
		Assert.notNull(appClusterId, "id can not be null");

		Project project = projectDao.getByAppClusterId(appClusterId);
		Assert.notNull(project, "not found project ,please check you project config");
		String url = project.getHttpUrl();

		// Find remote projectIds.
		String projectName = extProjectName(url);
		Integer vcsProjectId = vcsAdapter.forAdapt(project.getVcs()).getRemoteProjectId(project.getVcs(), projectName);
		Assert.notNull(vcsProjectId, String.format("No found projectId of name: %s", projectName));

		if (tarOrBranch != null && tarOrBranch == 2) { // tag
			List<String> branchNames = vcsAdapter.forAdapt(project.getVcs()).getRemoteTags(project.getVcs(), vcsProjectId);
			return branchNames;
		}
		// Branch
		else {
			List<String> branchNames = vcsAdapter.forAdapt(project.getVcs()).getRemoteBranchNames(project.getVcs(), vcsProjectId);
			return branchNames;
		}
	}

	@Override
	public List<CompositeBasicVcsProjectModel> vcsProjects(Integer vcsId, String projectName) {
		notNull(vcsId, "vcsId can not be null");
		// Get VCS information.
		Vcs vcs = vcsDao.selectByPrimaryKey(vcsId);

		// Search remote projects.
		List<VcsProjectModel> projects = vcsAdapter.forAdapt(vcs).searchRemoteProjects(vcs, projectName);
		return safeList(projects).stream().map(p -> p.toCompositeVcsProject()).collect(toList());
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