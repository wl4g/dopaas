/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.udc.service.impl;

import static java.util.Objects.nonNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.dopaas.common.bean.udc.GenTable;
import com.wl4g.dopaas.common.bean.udc.GenTableColumn;
import com.wl4g.dopaas.udc.data.GenTableColumnDao;
import com.wl4g.dopaas.udc.data.GenTableDao;
import com.wl4g.dopaas.udc.service.GenTableService;

/**
 * {@link GenTableServiceImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-03-15
 * @sine v1.0
 * @see
 */
@Service
public class GenTableServiceImpl implements GenTableService {

	private @Autowired GenTableDao genTableDao;
	private @Autowired GenTableColumnDao genTableColumnDao;

	@Override
	public PageHolder<GenTable> searchPage(PageHolder<GenTable> pm, String tableName, Long projectId) {
		pm.count().startPage();
		pm.setRecords(genTableDao.list(tableName, projectId));
		return pm;
	}

	@Override
	public GenTable getGenTable(Long tableId) {
		return genTableDao.selectByPrimaryKey(tableId);
	}

	@Override
	public List<GenTable> findGenTables(Long projectId) {
		return genTableDao.selectByProjectId(projectId);
	}

	@Override
	public Long getGenTableCount(Long projectId, String tableName) {
		return genTableDao.countByProjectIdAndTableName(projectId, tableName);
	}

	@Override
	public List<GenTableColumn> findGenTableColumns(Long tableId) {
		return genTableColumnDao.selectByTableId(tableId);
	}

	@Override
	public void save(GenTable genTable) {
		if (nonNull(genTable.getId())) {
			genTable.preUpdate();
			genTableDao.updateByPrimaryKeySelective(genTable);
		} else {
			genTable.preInsert();
			genTableDao.insertSelective(genTable);
		}
	}

	@Override
	public void deleteGenTable(Long tableId) {
		genTableDao.deleteByPrimaryKey(tableId);
	}

}
