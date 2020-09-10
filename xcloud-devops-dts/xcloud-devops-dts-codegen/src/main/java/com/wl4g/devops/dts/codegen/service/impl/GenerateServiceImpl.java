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
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.core.GenerateManager;
import com.wl4g.devops.dts.codegen.core.param.GenericParameter;
import com.wl4g.devops.dts.codegen.dao.GenDatabaseDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata;
import com.wl4g.devops.dts.codegen.service.GenerateService;
import com.wl4g.devops.dts.codegen.utils.ParseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.utils.ParseUtils.lineToHump;

/**
 * {@link GenerateServiceImpl}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@Service
public class GenerateServiceImpl implements GenerateService {

	@Autowired
	protected NamingPrototypeBeanFactory beanFactory;

	@Autowired
	private GenerateManager genManager;

	@Autowired
	private GenDatabaseDao genDatabaseDao;

	@Autowired
	private GenTableDao genTableDao;

	@Autowired
	private GenTableColumnDao genColumnDao;

	@Override
	public List<String> loadTables(Integer databaseId) {
		notNullOf(databaseId, "databaseId");
		GenDatabase genDatabase = genDatabaseDao.selectByPrimaryKey(databaseId);
		notNullOf(genDatabase, "genDatabase");
		MetadataResolver paraer = getMetadataPaser(genDatabase);
		return paraer.loadTable(genDatabase);
	}

	@Override
	public GenTable loadMetadata(Integer databaseId, String tableName) {
		notNullOf(databaseId, "databaseId");
		GenDatabase gendb = genDatabaseDao.selectByPrimaryKey(databaseId);
		notNullOf(gendb, "genDatabase");

		MetadataResolver paser = getMetadataPaser(gendb);
		TableMetadata tableMetadata = paser.loadTable(gendb, tableName);
		notNullOf(tableMetadata, "tableMetadata");

		// TableMetadata to GenTable
		GenTable genTable = new GenTable();
		genTable.setClassName(ParseUtils.tableName2ClassName(tableMetadata.getTableName()));
		genTable.setTableName(tableMetadata.getTableName());
		genTable.setComments(tableMetadata.getComments());

		List<GenTableColumn> genColumns = new ArrayList<>();
		for (TableMetadata.ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
			GenTableColumn column = new GenTableColumn();
			column.setColumnName(columnMetadata.getColumnName());
			column.setColumnComment(columnMetadata.getComments());
			column.setColumnType(columnMetadata.getColumnType());

			// TODO
			// column.setAttrType(paser.convertToJavaType(columnMetadata.getDataType()));
			column.setAttrName(lineToHump(columnMetadata.getColumnName()));
			// Sets default
			column.setIsInsert("1");
			column.setIsUpdate("1");
			column.setIsList("1");
			column.setIsEdit("1");
			column.setNoNull("1");
			column.setQueryType("1");
			column.setShowType("1");
			if (StringUtils.equalsIgnoreCase(columnMetadata.getColumnKey(), "PRI")) {
				column.setIsPk("1");
				column.setIsList("0");
				column.setNoNull("0");
			}
			// TODO......
			genColumns.add(column);
		}
		genTable.setGenTableColumns(genColumns);

		return genTable;
	}

	@Override
	public PageModel page(PageModel pm, String tableName) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(genTableDao.list(tableName));
		return pm;
	}

	@Override
	public GenTable detail(Integer tableId) {
		notNullOf(tableId, "tableId");
		GenTable genTable = genTableDao.selectByPrimaryKey(tableId);
		notNullOf(genTable, "genTable");
		List<GenTableColumn> genTableColumns = genColumnDao.selectByTableId(tableId);
		genTable.setGenTableColumns(genTableColumns);
		return genTable;
	}

	@Override
	public void saveGenConfig(GenTable genTable) {
		if (Objects.nonNull(genTable.getId())) {
			genTable.preUpdate();
			update(genTable);
		} else {
			genTable.preInsert();
			insert(genTable);
		}
	}

	private void insert(GenTable genTable) {

		List<GenTableColumn> genTableColumns = genTable.getGenTableColumns();
		for (GenTableColumn column : genTableColumns) {
			column.preInsert();
			column.setTableId(genTable.getId());
		}
		genColumnDao.insertBatch(genTableColumns);
		genTableDao.insertSelective(genTable);
	}

	private void update(GenTable genTable) {
		genColumnDao.deleteByTableId(genTable.getId());
		genTableDao.updateByPrimaryKeySelective(genTable);
		List<GenTableColumn> genTableColumns = genTable.getGenTableColumns();
		for (GenTableColumn column : genTableColumns) {
			column.preInsert();
			column.setTableId(genTable.getId());
		}
		genColumnDao.insertBatch(genTable.getGenTableColumns());
	}

	@Override
	public void delete(Integer tableId) {
		GenTable genTable = new GenTable();
		genTable.preUpdate();
		genTable.setId(tableId);
		genTable.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genTableDao.updateByPrimaryKeySelective(genTable);
	}

	@Override
	public void generate(Integer tableId) {
		genManager.execute(new GenericParameter(tableId));
	}

	/**
	 * Gets {@link MetadataResolver}
	 * 
	 * @param gen
	 * @return
	 */
	private MetadataResolver getMetadataPaser(GenDatabase gen) {
		return beanFactory.getPrototypeBean(gen.getType());
	}

}
