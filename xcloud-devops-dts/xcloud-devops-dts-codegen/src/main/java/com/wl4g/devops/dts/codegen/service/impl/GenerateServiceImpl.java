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
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.web.rest.RespBase;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import com.wl4g.devops.dts.codegen.dao.GenDataSourceDao;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.GenerateEngine;
import com.wl4g.devops.dts.codegen.engine.context.GeneratedResult;
import com.wl4g.devops.dts.codegen.engine.context.GenericParameter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.CodeLanguage;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.DbType;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.TypeMappedWrapper.MappedMatcher;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
import com.wl4g.devops.dts.codegen.engine.specs.JavaSpecs;
import com.wl4g.devops.dts.codegen.service.GenerateService;
import com.wl4g.devops.dts.codegen.utils.GenUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.TypeMappedWrapper;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderSet;
import static com.wl4g.devops.dts.codegen.engine.specs.JavaSpecs.underlineToHump;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
	protected GenericOperatorAdapter<DbType, DbTypeConverter> converter;

	@Autowired
	protected GenerateEngine engine;

	@Autowired
	protected GenDataSourceDao genDataSourceDao;

	@Autowired
	protected GenProjectDao genProjectDao;

	@Autowired
	protected GenTableDao genTableDao;

	@Autowired
	protected GenTableColumnDao genColumnDao;

	@Override
	public List<TableMetadata> loadTables(Long projectId) {
		notNullOf(projectId, "projectId");
		GenProject genProject = genProjectDao.selectByPrimaryKey(projectId);
		notNullOf(genProject, "genProject");

		GenDataSource dataSource = genDataSourceDao.selectByPrimaryKey(genProject.getDatasourceId());
		notNullOf(dataSource, "genDatabase");

		MetadataResolver resolver = beanFactory.getPrototypeBean(dataSource.getType(), dataSource);
		List<TableMetadata> tableMetadatas = resolver.findTablesAll();

		List<GenTable> genTables = genTableDao.selectByProjectId(projectId);
		List<TableMetadata> needRemove = new ArrayList<>();
		for (TableMetadata tableMetadata : tableMetadatas) {
			for (GenTable genTable : genTables) {
				if (StringUtils.equalsIgnoreCase(tableMetadata.getTableName(), genTable.getTableName())) {
					needRemove.add(tableMetadata);
				}
			}
		}
		tableMetadatas.removeAll(needRemove);
		return tableMetadatas;
	}

	@Override
	public RespBase<Object> loadMetadata(Long projectId, String tableName) {
		RespBase<Object> resp = RespBase.create();
		// Gets gen project
		notNullOf(projectId, "projectId");
		GenProject project = genProjectDao.selectByPrimaryKey(projectId);
		notNullOf(project, "genProject");

		// Gets gen datasource
		GenDataSource datasource = genDataSourceDao.selectByPrimaryKey(project.getDatasourceId());
		notNullOf(datasource, "genDataSource");

		// Gets gen table
		MetadataResolver resolver = beanFactory.getPrototypeBean(datasource.getType(), datasource);
		TableMetadata metadata = resolver.findTableDescribe(tableName);
		notNullOf(metadata, "tableMetadata");

		// Gets gen table columns
		metadata.setColumns(notEmptyOf(resolver.findTableColumns(tableName), "genTableColumns"));

		// To {@link GenTable}
		GenTable table = new GenTable();
		table.setEntityName(JavaSpecs.tableName2ClassName(metadata.getTableName()));
		table.setTableName(metadata.getTableName());
		table.setComments(metadata.getComments());
		// Set Table default
		table.setFunctionAuthor("unascribed");
		table.setRemark(metadata.getComments());

		GenProviderSet providerSet = GenProviderSet.of(project.getProviderSet());

		List<GenTableColumn> cols = new ArrayList<>();
		for (ColumnMetadata colmd : metadata.getColumns()) {
			GenTableColumn col = new GenTableColumn();
			col.setColumnName(colmd.getColumnName());
			// ColumnComment replace \n to space
			if (StringUtils.isNotBlank(colmd.getComments())) {
				col.setColumnComment(colmd.getComments().replaceAll("\n", "  "));
			}
			col.setColumnType(colmd.getColumnType());
			col.setSimpleColumnType(colmd.getSimpleColumnType());
			col.setAttrName(underlineToHump(colmd.getColumnName()));

			// Converting java type
			if (nonNull(providerSet.language())) {
				DbTypeConverter conv = converter.forOperator(datasource.getType());
				col.setAttrType(conv.convertBy(providerSet.language(), MappedMatcher.Column2Lang, col.getSimpleColumnType()));
			}

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
				col.setIsEdit("0");
				col.setNoNull("0");
			} else {
				col.setIsPk("0");
			}
			setDefaultShowType(colmd, col);
			cols.add(col);
		}
		table.setGenTableColumns(cols);
		// Check table struct specification.
		String warningTip = GenUtils.checkBuiltinColumns(datasource, project, table);
		if (isNotBlank(warningTip)) {
			resp.setStatus("warningTip");
			resp.setMessage(warningTip);
		}
		resp.setData(table);
		return resp;
	}

	/**
	 * Set Default Query Type
	 *
	 * @param colmd
	 * @param col
	 */
	private void setDefaultShowType(ColumnMetadata colmd, GenTableColumn col) {
		if (StringUtils.equalsAnyIgnoreCase(colmd.getSimpleColumnType(), "DATE")) {
			col.setShowType("7");// Date
		} else if (StringUtils.equalsAnyIgnoreCase(colmd.getSimpleColumnType(), "DATETIME", "TIMESTAMP")) {
			col.setShowType("8");// DateTime
		} else if (StringUtils.equalsAnyIgnoreCase(colmd.getSimpleColumnType(), "TEXT")) {
			col.setShowType("2");// textarea
		} else {
			col.setShowType("1");// normal input
		}
	}

	@Override
	public PageModel page(PageModel pm, String tableName, Long projectId) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(genTableDao.list(tableName, projectId));
		return pm;
	}

	@Override
	public RespBase<Object> detail(Long tableId) {
		RespBase<Object> resp = RespBase.create();
		notNullOf(tableId, "tableId");

		// Gets gen table
		GenTable oldGenTab = notNullOf(genTableDao.selectByPrimaryKey(tableId), "genTable");

		// Gets gen table columns
		List<GenTableColumn> oldGenCols = genColumnDao.selectByTableId(tableId);
		oldGenTab.setGenTableColumns(oldGenCols);

		// Gets gen project
		GenProject project = genProjectDao.selectByPrimaryKey(oldGenTab.getProjectId());
		notNullOf(project, "genProject");

		// Gets gen datasource
		GenDataSource datasource = genDataSourceDao.selectByPrimaryKey(project.getDatasourceId());
		notNullOf(datasource, "genDataSource");

		// Reload the latest table/columns metadata (sure you get the
		// latest information)
		GenTable newGenTab = (GenTable) loadMetadata(oldGenTab.getProjectId(), oldGenTab.getTableName()).getData();
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

		String warningTip = GenUtils.checkBuiltinColumns(datasource, project, oldGenTab);
		if (isNotBlank(warningTip)) {
			resp.setStatus("warningTip");
			resp.setMessage(warningTip);
		}

		resp.setData(oldGenTab);
		return resp;
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

		GenProject genProject = notNullOf(genProjectDao.selectByPrimaryKey(genTable.getProjectId()), "genProject");
		GenDataSource genDS = genDataSourceDao.selectByPrimaryKey(genProject.getDatasourceId());

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
		Long count = genTableDao.countByProjectIdAndTableName(genTable.getProjectId(), genTable.getTableName());
		if (nonNull(count)) {
			Assert2.isTrue(count <= 0, "Cannot add the same table");
		}

		List<GenTableColumn> cols = genTable.getGenTableColumns();
		int i = 0;
		for (GenTableColumn col : cols) {
			col.preInsert();
			col.setTableId(genTable.getId());
			col.setColumnSort(i++);
		}
		genTableDao.insertSelective(genTable);
		genColumnDao.insertBatch(cols);
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
	public void delete(Long id) {
		GenTable genTable = new GenTable();
		genTable.preUpdate();
		genTable.setId(id);
		genTable.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genTableDao.updateByPrimaryKeySelective(genTable);
	}

	@Override
	public GeneratedResult generate(Long tableId) {
		return engine.execute(new GenericParameter(tableId));
	}

	@Override
	public Set<String> getAttrTypes(Long projectId) {
		GenProject project = genProjectDao.selectByPrimaryKey(projectId);
		GenProviderSet providerSet = GenProviderSet.of(project.getProviderSet());
		if (isNull(providerSet.language())) {
			return null;
		}
		GenDataSource datasource = genDataSourceDao.selectByPrimaryKey(project.getDatasourceId());

		DbTypeConverter conv = converter.forOperator(datasource.getType());
		List<TypeMappedWrapper> mappings = conv.getTypeMappedWrappers(providerSet.language());
		Set<String> attrTypes = new HashSet<>();
		for (TypeMappedWrapper map : mappings) {
			attrTypes.add(map.getAttrType());
		}
		return attrTypes;
	}

	@Override
	public void setEnable(Long id, String status) {
		GenTable genTable = new GenTable();
		genTable.preUpdate();
		genTable.setId(id);
		genTable.setStatus(status);
		genTableDao.updateByPrimaryKeySelective(genTable);
	}

	@Override
	public void synchronizeTable(Long id, boolean focus) {
		if (focus) {
			GenTable genTable = genTableDao.selectByPrimaryKey(id);
			GenTable genTableNew = (GenTable) loadMetadata(genTable.getProjectId(), genTable.getTableName()).getData();
			genTable.setGenTableColumns(genTableNew.getGenTableColumns());
			saveGenConfig(genTable);
		} else {
			GenTable genTable = (GenTable) detail(id).getData();
			saveGenConfig(genTable);
		}

	}

}