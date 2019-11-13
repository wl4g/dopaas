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

import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 10:24:00
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private DependencyDao dependencyDao;

	@Override
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
					dependencyDao.insertSelective(dependency);
				}
			}
		}
		return result;
	}

	@Override
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
	public List<Project> list(String groupName, String projectName) {
		return projectDao.list(groupName, projectName);
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

}