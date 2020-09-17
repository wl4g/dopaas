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
package com.wl4g.devops.dts.codegen.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenProject.ConfigOptions;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.service.GenProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * GenProjectServiceImpl
 *
 * @author heweijie
 * @Date 2020-09-11
 */
@Service
public class GenProjectServiceImpl implements GenProjectService {

	@Autowired
	private GenProjectDao genProjectDao;

	@Override
	public PageModel page(PageModel pm, String projectName) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(genProjectDao.list(projectName));
		return pm;
	}

	public void save(GenProject project) {
		if (nonNull(project.getExtraOptions())) {
			project.setExtraOptionsJson(toJSONString(project.getExtraOptions()));
		}
		if (isNull(project.getId())) {
			project.preInsert();
			insert(project);
		} else {
			project.preUpdate();
			update(project);
		}
	}

	private void insert(GenProject genProject) {
		genProjectDao.insertSelective(genProject);
	}

	private void update(GenProject genProject) {
		genProjectDao.updateByPrimaryKeySelective(genProject);
	}

	public GenProject detail(Integer id) {
		notNullOf(id, "genProjectId");

		GenProject project = genProjectDao.selectByPrimaryKey(id);
		if (isNotBlank(project.getExtraOptionsJson())) {
			ConfigOptions configuredOptions = parseJSON(project.getExtraOptionsJson(), ConfigOptions.class);
			project.setExtraOptions(configuredOptions);
		}

		return project;
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		GenProject genProject = new GenProject();
		genProject.setId(id);
		genProject.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genProjectDao.updateByPrimaryKeySelective(genProject);
	}

}