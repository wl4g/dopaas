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
import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import com.wl4g.devops.dts.codegen.dao.GenDatabaseDao;
import com.wl4g.devops.dts.codegen.service.GenDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class GenDatabaseServiceImpl implements GenDatabaseService {

	@Autowired
	private GenDatabaseDao genDatabaseDao;

	@Override
	public PageModel page(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(genDatabaseDao.list(name));
		return pm;
	}

	@Override
	public List<GenDatabase> getForSelect() {
		return genDatabaseDao.list(null);
	}

	public void save(GenDatabase genDatabase) {
		if (isNull(genDatabase.getId())) {
			genDatabase.preInsert(getRequestOrganizationCode());
			insert(genDatabase);
		} else {
			genDatabase.preUpdate();
			update(genDatabase);
		}
	}

	private void insert(GenDatabase genDatabase) {
		genDatabaseDao.insertSelective(genDatabase);
	}

	private void update(GenDatabase genDatabase) {
		genDatabaseDao.updateByPrimaryKeySelective(genDatabase);
	}

	public GenDatabase detail(Integer id) {
		Assert.notNull(id, "id is null");
		return genDatabaseDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		GenDatabase genDatabase = new GenDatabase();
		genDatabase.setId(id);
		genDatabase.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genDatabaseDao.updateByPrimaryKeySelective(genDatabase);
	}

}