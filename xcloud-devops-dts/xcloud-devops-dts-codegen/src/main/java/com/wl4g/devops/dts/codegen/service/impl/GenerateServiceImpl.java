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
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.dao.GenDataSourceDao;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.GenerateEngine;
import com.wl4g.devops.dts.codegen.engine.context.GenericParameter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.CodeLanguage;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.ConverterKind;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.TypeMappedWrapper.MappedMatcher;
import com.wl4g.devops.dts.codegen.engine.naming.JavaSpecs;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
import com.wl4g.devops.dts.codegen.service.GenerateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.engine.naming.JavaSpecs.underlineToHump;

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
	protected GenericOperatorAdapter<ConverterKind, DbTypeConverter> converter;

	@Autowired
	protected GenerateEngine genManager;

	@Autowired
	protected GenDataSourceDao genDatabaseDao;

	@Autowired
	protected GenProjectDao genProjectDao;

	@Autowired
	protected GenTableDao genTableDao;

	@Autowired
	protected GenTableColumnDao genColumnDao;

	@Override
	public List<TableMetadata> loadTables(Integer databaseId) {
		notNullOf(databaseId, "databaseId");
		GenDataSource genDatabase = genDatabaseDao.selectByPrimaryKey(databaseId);
		notNullOf(genDatabase, "genDatabase");
		MetadataResolver resolver = getMetadataPaser(genDatabase);
		return resolver.findTablesAll();
	}

	@Override
	public GenTable loadMetadata(Integer databaseId, Integer projectId, String tableName) {
		notNullOf(databaseId, "databaseId");
		GenDataSource genDS = genDatabaseDao.selectByPrimaryKey(databaseId);
		notNullOf(genDS, "genDatabase");

		// GenProject project = genProjectDao.selectByPrimaryKey(projectId);

		MetadataResolver resolver = getMetadataPaser(genDS);
		TableMetadata metadata = resolver.findTableDescribe(tableName);
		notNullOf(metadata, "tableMetadata");

		metadata.setColumns(resolver.findTableColumns(tableName));

		// TableMetadata to GenTable
		GenTable tab = new GenTable();
		tab.setEntityName(JavaSpecs.tableName2ClassName(metadata.getTableName()));
		tab.setTableName(metadata.getTableName());
		tab.setComments(metadata.getComments());

		List<GenTableColumn> cols = new ArrayList<>();
		for (ColumnMetadata colmd : metadata.getColumns()) {
			GenTableColumn col = new GenTableColumn();
			col.setColumnName(colmd.getColumnName());
			col.setColumnComment(colmd.getComments());
			col.setColumnType(colmd.getColumnType());
			col.setSimpleColumnType(colmd.getSimpleColumnType());
			col.setAttrName(underlineToHump(colmd.getColumnName()));
			// TODO
			// Converting java type
			DbTypeConverter conv = converter.forOperator(genDS.getType());
			col.setAttrType(conv.convertBy(CodeLanguage.JAVA, MappedMatcher.Column2Lang, col.getSimpleColumnType()));

			// Sets defaults
			col.setIsInsert("1");
			col.setIsUpdate("1");
			col.setIsList("1");
			col.setIsEdit("1");
			col.setNoNull(colmd.isNullable() ? "0" : "1");
			col.setQueryType("1");
			col.setIsQuery("0");
			col.setShowType("1");
			if (colmd.isPk()) {
				col.setIsPk("1");
				col.setIsList("0");
				col.setNoNull("0");
			} else {
				col.setIsPk("0");
			}
			cols.add(col);
		}
		tab.setGenTableColumns(cols);

		return tab;
	}

	@Override
	public PageModel page(PageModel pm, String tableName, Integer projectId) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(genTableDao.list(tableName, projectId));
		return pm;
	}

	@Override
	public GenTable detail(Integer tableId) {
		notNullOf(tableId, "tableId");

		GenTable oldGenTab = notNullOf(genTableDao.selectByPrimaryKey(tableId), "genTable");
		List<GenTableColumn> oldGenCols = genColumnDao.selectByTableId(tableId);
		oldGenTab.setGenTableColumns(oldGenCols);

		// Reload the latest table/columns metadata (sure you get the
		// latest information)
		GenTable newGenTab = loadMetadata(oldGenTab.getDatabaseId(), oldGenTab.getProjectId(), oldGenTab.getTableName());
		List<GenTableColumn> newGenCols = newGenTab.getGenTableColumns();

		List<GenTableColumn> needAdd = new ArrayList<>();
		List<GenTableColumn> needDel = new ArrayList<>();

		for (GenTableColumn newCol : newGenCols) {
			GenTableColumn column = genTableColumnByName(oldGenCols, newCol.getColumnName());
			if (column == null) {
				needAdd.add(newCol);
			}
		}
		for (GenTableColumn oldCol : oldGenCols) {
			GenTableColumn column = genTableColumnByName(newGenCols, oldCol.getColumnName());
			if (column == null) {
				needDel.add(oldCol);
			}
		}
		oldGenCols.removeAll(needDel);
		oldGenCols.addAll(needAdd);

		oldGenTab.setGenTableColumns(oldGenCols);
		return oldGenTab;
	}

	private GenTableColumn genTableColumnByName(List<GenTableColumn> genTableColumns, String name) {
		for (GenTableColumn genTableColumn : genTableColumns) {
			if (StringUtils.equals(genTableColumn.getColumnName(), name)) {
				return genTableColumn;
			}
		}
		return null;
	}

	@Override
	public void saveGenConfig(GenTable genTable) {
		GenDataSource genDS = genDatabaseDao.selectByPrimaryKey(genTable.getDatabaseId());

		for (GenTableColumn column : genTable.getGenTableColumns()) {
			DbTypeConverter conv = converter.forOperator(genDS.getType());
			column.setSqlType(conv.convertBy(CodeLanguage.JAVA, MappedMatcher.Column2Sql, column.getSimpleColumnType()));
		}

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
		int i = 0;
		for (GenTableColumn column : genTableColumns) {
			column.preInsert();
			column.setTableId(genTable.getId());
			column.setColumnSort(i++);
		}
		genTableDao.insertSelective(genTable);
		genColumnDao.insertBatch(genTableColumns);
	}

	private void update(GenTable genTable) {
		genColumnDao.deleteByTableId(genTable.getId());
		genTableDao.updateByPrimaryKeySelective(genTable);
		List<GenTableColumn> genTableColumns = genTable.getGenTableColumns();
		int i = 0;
		for (GenTableColumn column : genTableColumns) {
			column.preInsert();
			column.setTableId(genTable.getId());
			column.setColumnSort(i++);
		}
		genColumnDao.insertBatch(genTable.getGenTableColumns());
	}

	@Override
	public void delete(Integer id) {
		GenTable genTable = new GenTable();
		genTable.preUpdate();
		genTable.setId(id);
		genTable.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genTableDao.updateByPrimaryKeySelective(genTable);
	}

	@Override
	public String generate(Integer tableId) {
		return genManager.execute(new GenericParameter(tableId));
	}

	/**
	 * Gets {@link MetadataResolver}
	 * 
	 * @param gen
	 * @return
	 */
	private MetadataResolver getMetadataPaser(GenDataSource gen) {
		return beanFactory.getPrototypeBean(gen.getType(), gen);
	}

}