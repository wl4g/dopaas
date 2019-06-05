/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.scm.BaseBean;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.ci.ProjectDao;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 10:24:00
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	private ProjectDao projectDao;

	@Override
	public int insert(Project project) {
		project.preInsert();
		return projectDao.insert(project);
	}

	@Override
	public int update(Project project) {
		project.preUpdate();
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public int deleteById(Integer id) {
		Project project = new Project();
		project.preUpdate();
		project.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		return projectDao.updateByPrimaryKeySelective(project);
	}

	@Override
	public int removeById(Integer id) {
		return projectDao.deleteByPrimaryKey(id);
	}

	@Override
	public List<Project> list(CustomPage customPage) {
		return projectDao.list(customPage);
	}

}