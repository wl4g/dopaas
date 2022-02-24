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
package com.wl4g.dopaas.lcdp.codegen.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.infra.core.bean.BaseBean;
import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.lcdp.GenProject;
import com.wl4g.dopaas.common.bean.lcdp.extra.ExtraOptionDefinition.GenExtraOption;
import com.wl4g.dopaas.lcdp.codegen.data.GenProjectDao;
import com.wl4g.dopaas.lcdp.codegen.engine.GenProviderSetDefinition;
import com.wl4g.dopaas.lcdp.codegen.service.GenProjectService;

/**
 * GenProjectServiceImpl
 *
 * @author heweijie
 * @Date 2020-09-11
 */
@Service
public class GenProjectServiceImpl implements GenProjectService {

	private @Autowired GenProjectDao genProjectDao;

	@Override
	public PageHolder<GenProject> page(PageHolder<GenProject> pm, String projectName) {
		pm.useCount().bind();
		pm.setRecords(genProjectDao.list(projectName));
		return pm;
	}

	public void save(GenProject project) {
		if (nonNull(project.getExtraOptions())) {
			// Validate
			GenProviderSetDefinition.validateOption(project.getProviderSet(), project.getExtraOptions());
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

	public GenProject detail(Long id) {
		notNullOf(id, "genProjectId");

		GenProject project = genProjectDao.selectByPrimaryKey(id);
		// Populate extraOptions
		if (!isBlank(project.getExtraOptionsJson())) {
			project.setExtraOptions(parseJSON(project.getExtraOptionsJson(), new TypeReference<List<GenExtraOption>>() {
			}));
		}

		return project;
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		GenProject genProject = new GenProject();
		genProject.setId(id);
		genProject.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genProjectDao.updateByPrimaryKeySelective(genProject);
	}

}