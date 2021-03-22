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
package com.wl4g.dopaas.lcdp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.component.common.bean.BeanUtils2;
import com.wl4g.component.common.id.SnowflakeIdGenerator;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.dopaas.common.bean.lcdp.GenDataSource;
import com.wl4g.dopaas.common.bean.lcdp.GenProject;
import com.wl4g.dopaas.common.bean.lcdp.GenTable;
import com.wl4g.dopaas.common.bean.lcdp.GenTableColumn;
import com.wl4g.dopaas.common.bean.lcdp.extra.TableExtraOptionDefinition.GenTableExtraOption;
import com.wl4g.dopaas.common.bean.lcdp.model.GeneratedResult;
import com.wl4g.dopaas.common.bean.lcdp.model.TableMetadata;
import com.wl4g.dopaas.common.bean.lcdp.model.TableMetadata.ColumnMetadata;
import com.wl4g.dopaas.lcdp.codegen.engine.GenProviderSetDefinition;
import com.wl4g.dopaas.lcdp.codegen.engine.GenerateEngine;
import com.wl4g.dopaas.lcdp.codegen.engine.context.GenericParameter;
import com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter;
import com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter.DbType;
import com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter.TypeMappingWrapper;
import com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter.TypeMappingWrapper.MappedMatcher;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.specs.BaseSpecs.*;
import com.wl4g.dopaas.lcdp.codegen.engine.specs.JavaSpecs;
import com.wl4g.dopaas.lcdp.codegen.i18n.CodegenResourceMessageBundler;
import com.wl4g.dopaas.lcdp.codegen.utils.BuiltinColumnDefinition;
import com.wl4g.dopaas.lcdp.data.GenTableColumnDao;
import com.wl4g.dopaas.lcdp.service.GenDataSourceService;
import com.wl4g.dopaas.lcdp.service.GenProjectService;
import com.wl4g.dopaas.lcdp.service.GenTableService;
import com.wl4g.dopaas.lcdp.service.GenerateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static com.wl4g.component.common.lang.Assert2.isTrue;
import static com.wl4g.component.common.lang.Assert2.*;
import static com.wl4g.component.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.IAM_SPINGCLOUD_MVN;
import static com.wl4g.dopaas.lcdp.codegen.config.CodegenAutoConfiguration.BEAN_CODEGEN_MSG_SOURCE;
import static com.wl4g.dopaas.lcdp.codegen.engine.GenProviderSetDefinition.getProviders;
import static com.wl4g.dopaas.lcdp.codegen.engine.specs.BaseSpecs.*;
import static com.wl4g.dopaas.lcdp.codegen.engine.specs.JavaSpecs.underlineToHump;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.*;


/**
 * {@link GenerateServiceImpl}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@Service
public class GenerateServiceImpl implements GenerateService {

	private @Resource(name = BEAN_CODEGEN_MSG_SOURCE) CodegenResourceMessageBundler bundle;
	private @Autowired NamingPrototypeBeanFactory beanFactory;
	private @Autowired GenerateEngine generateEngine;
	private @Autowired GenDataSourceService genDataSourceService;
	private @Autowired GenProjectService genProjectService;
	private @Autowired GenTableService genTableService;
	private @Autowired GenTableColumnDao genColumnDao;

	// ------------------------------------------
	// Generate tables/columns configuration.
	// ------------------------------------------

	@Override
	public RespBase<GenTable> findGenTableDetail(Long tableId) {
		notNullOf(tableId, "tableId");
		RespBase<GenTable> resp = RespBase.create();

		// Gets genTable
		GenTable table = notNullOf(genTableService.getGenTable(tableId), "genTable");
		// Populate extraOptions
		if (!isBlank(table.getExtraOptionsJson())) {
			table.setExtraOptions(parseJSON(table.getExtraOptionsJson(), new TypeReference<List<GenTableExtraOption>>() {
			}));
		}

		// Gets genTable columns.
		table.setGenTableColumns(genColumnDao.selectByTableId(table.getId()));

		// Gets genProject
		GenProject project = notNullOf(genProjectService.detail(table.getProjectId()), "genProject");

		// Gets genDatasource
		GenDataSource datasource = notNullOf(genDataSourceService.detail(project.getDatasourceId()), "genDataSource");

		// Check builtin genTable columns.
		String warningTip = checkBuiltinColumns(datasource, project, table);
		if (!isBlank(warningTip)) {
			resp.setStatus("warningTip");
			resp.setMessage(warningTip);
		}

		resp.setData(table);
		return resp;
	}

	@Override
	public void saveGenConfig(GenTable genTable) {
		GenProject project = notNullOf(genProjectService.detail(genTable.getProjectId()), "genProject");
		GenDataSource datasource = notNullOf(genDataSourceService.detail(project.getDatasourceId()), "genDatasource");

		// Sets SqlType/attrType
		GenProviderSetDefinition providerSet = notNullOf(GenProviderSetDefinition.of(project.getProviderSet()), "genProviderSet");
		for (GenTableColumn col : genTable.getGenTableColumns()) {
			DbTypeConverter conv = providerSet.converter();
			col.setSqlType(conv.convertBy(datasource.getType(), MappedMatcher.Column2Sql, col.getSimpleColumnType()));
		}
		genTable.setExtraOptionsJson(toJSONString(genTable.getExtraOptions()));

		if (nonNull(genTable.getId())) {
			genTable.preUpdate();
			doBatchUpdate(genTable);
		} else {
			genTable.preInsert();
			doBatchInsert(genTable);
		}
	}

	@Override
	public void deleteGenTable(Long id) {
		GenTable genTable = new GenTable();
		genTable.preUpdate();
		genTable.setId(id);
		genTable.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		genTableService.save(genTable);
	}

	@Override
	public void setGenTableStatus(Long id, String status) {
		GenTable genTable = new GenTable();
		genTable.preUpdate();
		genTable.setId(id);
		genTable.setStatus(status);
		genTableService.save(genTable);
	}

	// ------------------------------------------
	// Generate configuration.
	// ------------------------------------------

	@Override
	public List<TableMetadata> loadTableMetadata(Long projectId) throws Exception {
		notNullOf(projectId, "projectId");
		GenProject genProject = notNullOf(genProjectService.detail(projectId), "genProject");
		GenDataSource genDataSource = genDataSourceService.detail(genProject.getDatasourceId());
		notNullOf(genDataSource, "genDatasource");

		try (MetadataResolver resolver = beanFactory.getPrototypeBean(genDataSource.getType(), genDataSource);) {
			List<GenTable> genTables = genTableService.findGenTables(projectId);
			List<TableMetadata> metadatas = resolver.findTablesAll();
			// Filtering configured tables.
			return safeList(metadatas)
					.stream().filter(md -> !safeList(genTables).stream()
							.filter(t -> equalsIgnoreCase(md.getTableName(), t.getTableName())).findAny().isPresent())
					.collect(toList());
		}
	}

	@Override
	public RespBase<GenTable> loadGenColumns(Long projectId, String tableName) throws Exception {
		RespBase<GenTable> resp = RespBase.create();
		notNullOf(projectId, "projectId");

		// Gets genPoject
		GenProject project = notNullOf(genProjectService.detail(projectId), "genProject");
		GenProviderSetDefinition providerSet = GenProviderSetDefinition.of(project.getProviderSet());

		// Gets genDatasource
		GenDataSource datasource = notNullOf(genDataSourceService.detail(project.getDatasourceId()), "genDataSource");

		try (MetadataResolver resolver = beanFactory.getPrototypeBean(datasource.getType(), datasource);) {
			// Gets genTable
			TableMetadata tmetadata = notNullOf(resolver.findTableDescribe(tableName), "tableMetadata");
			// Gets genTable columns
			tmetadata.setColumns(notEmptyOf(resolver.findTableColumns(tableName), "genTableColumns"));

			// New genTable
			GenTable table = new GenTable();
			table.setProjectId(project.getId());
			// Optimization entityName specification.
			switch (providerSet.converter()) {
			case JAVA:
				table.setEntityName(capf(JavaSpecs.tableName2ClassName(tmetadata.getTableName())));
				break;
			default: // TODO
				table.setEntityName(tmetadata.getTableName());
				break;
			}
			table.setTableName(tmetadata.getTableName());
			table.setComments(tmetadata.getComments());
			// Sets genTable defaults.
			table.setFunctionAuthor("unascribed");
			table.setFunctionName(tmetadata.getComments());
			table.setFunctionNameSimple(extractComment(tmetadata.getComments(), CommentExtractor.simple));
			table.setRemark(tmetadata.getComments());

			// Gen table columns
			List<GenTableColumn> cols = new ArrayList<>();
			for (ColumnMetadata cmetadata : tmetadata.getColumns()) {
				GenTableColumn col = new GenTableColumn();
				col.setColumnName(cmetadata.getColumnName());
				// Cleanup comment '\n' or '\r\n'
				if (!isBlank(cmetadata.getComments())) {
					col.setColumnComment(cleanComment(cmetadata.getComments()));
				}
				col.setColumnType(cmetadata.getColumnType());
				col.setSimpleColumnType(cmetadata.getSimpleColumnType());
				col.setAttrName(underlineToHump(cmetadata.getColumnName()));

				// Converting java type
				DbTypeConverter conv = providerSet.converter();
				col.setAttrType(conv.convertBy(datasource.getType(), MappedMatcher.Column2Attr, col.getSimpleColumnType()));

				// Sets column defaults.
				col.setIsInsert("1");
				col.setIsUpdate("1");
				col.setNoNull(cmetadata.isNullable() ? "0" : "1");
				col.setQueryType("1");
				col.setIsQuery("0");
				col.setShowType("1");

				if (DEFAULT_NO_EDIT.contains(cmetadata.getColumnName())) {
					col.setIsEdit("0");
				} else {
					col.setIsEdit("1");
				}
				if (DEFAULT_NO_LIST.contains(cmetadata.getColumnName())) {
					col.setIsList("0");
				} else {
					col.setIsList("1");
				}

				if (cmetadata.isPk()) {
					col.setIsPk("1");
					col.setIsList("0");
					col.setIsEdit("0");
					col.setNoNull("0");
				} else {
					col.setIsPk("0");
				}

				// Sets default show attrType
				applyDefaultShowType(cmetadata, col);
				cols.add(col);
			}
			table.setGenTableColumns(cols);

			// Check table struct specification.
			String warningTip = checkBuiltinColumns(datasource, project, table);
			if (isNotBlank(warningTip)) {
				resp.setStatus("warningTip");
				resp.setMessage(warningTip);
			}
			resp.setData(table);
		}

		return resp;
	}

	@Override
	public Set<String> getAttrTypes(Long projectId) {
		GenProject project = notNullOf(genProjectService.detail(projectId), "genProject");
		GenDataSource datasource = genDataSourceService.detail(project.getDatasourceId());

		GenProviderSetDefinition providerSet = GenProviderSetDefinition.of(project.getProviderSet());
		List<TypeMappingWrapper> mapping = providerSet.converter().getTypeMappings(datasource.getType());

		// To attrTypes.
		return mapping.stream().map(m -> m.getAttrType()).collect(toSet());
	}

	@Override
	public void syncTableColumns(Long id, boolean force) throws Exception {
		// Gets older genTable.
		GenTable oldTable = genTableService.getGenTable(id);
		if (force) {
			// Gets new genTable.
			GenTable newTable = loadGenColumns(oldTable.getProjectId(), oldTable.getTableName()).getData();

			// Overriding columns with force.
			try {
				BeanUtils2.deepCopyFieldState(oldTable, newTable);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}

			// Resaving.
			saveGenConfig(oldTable);
		} else {
			// Overriding columns with non force.
			applyOverridingColumnsWithNonForce(oldTable);

			// Resaving.
			saveGenConfig(oldTable);
		}
	}

	// ------------------------------------------
	// Execution.
	// ------------------------------------------

	@Override
	public GeneratedResult generate(Long projectId) {
		return generateEngine.execute(new GenericParameter(projectId));
	}

	/**
	 * Apply sets overriding columns with non force. </br>
	 * </br>
	 * Only synchronize the information that covers the added or deleted
	 * columns.
	 * 
	 * @param oldTable
	 * @throws Exception
	 */
	private void applyOverridingColumnsWithNonForce(GenTable oldTable) throws Exception {
		// Gets gen table columns.
		List<GenTableColumn> oldColumns = genColumnDao.selectByTableId(oldTable.getId());
		oldTable.setGenTableColumns(oldColumns);

		// Reload the latest table metadata.
		GenTable newTable = loadGenColumns(oldTable.getProjectId(), oldTable.getTableName()).getData();
		List<GenTableColumn> newColumns = newTable.getGenTableColumns();

		// Calculation table column complement set.
		List<GenTableColumn> adding = new ArrayList<>();
		List<GenTableColumn> deleting = new ArrayList<>();
		// Extract new columns.
		for (GenTableColumn newCol : newColumns) {
			if (!existGenTableColumn(oldColumns, newCol.getColumnName())) {
				adding.add(newCol);
			}
		}
		// Extract removed columns.
		for (GenTableColumn oldCol : oldColumns) {
			if (!existGenTableColumn(newColumns, oldCol.getColumnName())) {
				deleting.add(oldCol);
			}
		}
		oldColumns.removeAll(deleting);
		oldColumns.addAll(adding);

		oldTable.setGenTableColumns(oldColumns);
	}

	private void doBatchInsert(GenTable genTable) {
		// Check distinct genTables.
		Long count = genTableService.getGenTableCount(genTable.getProjectId(), genTable.getTableName());
		if (nonNull(count)) {
			isTrue(count <= 0, "Already saved gen table: '%s'", genTable.getTableName());
		}
		List<GenTableColumn> cols = genTable.getGenTableColumns();
		for (int i = 0; i < cols.size(); i++) {
			GenTableColumn col = cols.get(i);
			col.preInsert();
			col.setId(SnowflakeIdGenerator.getDefault().nextId());
			col.setTableId(genTable.getId());
			col.setColumnSort(i);
		}
		genTableService.save(genTable);
		genColumnDao.insertBatch(cols);
	}

	private void doBatchUpdate(GenTable genTable) {
		genColumnDao.deleteByTableId(genTable.getId());
		genTableService.save(genTable);

		List<GenTableColumn> columns = genTable.getGenTableColumns();
		int i = 0;
		for (GenTableColumn col : columns) {
			col.preInsert();
			col.setId(SnowflakeIdGenerator.getDefault().nextId());
			col.setTableId(genTable.getId());
			col.setColumnSort(i++);
		}
		genColumnDao.insertBatch(genTable.getGenTableColumns());
	}

	/**
	 * Is exist {@link GenTableColumn} by column name.
	 * 
	 * @param columns
	 * @param columnName
	 * @return
	 */
	private boolean existGenTableColumn(List<GenTableColumn> columns, String columnName) {
		return safeList(columns).stream().filter(col -> equalsIgnoreCase(col.getColumnName(), columnName)).findAny().isPresent();
	}

	/**
	 * Check {@link GenTable} columns type with builtin definitions.
	 * 
	 * @param datasource
	 * @param project
	 * @param table
	 * @return Warning tip message.
	 */
	private String checkBuiltinColumns(@NotNull GenDataSource datasource, @NotNull GenProject project, @NotNull GenTable table) {
		notNullOf(datasource, "datasource");
		notNullOf(project, "project");
		notNullOf(table, "table");

		// Check requires provider.
		List<String> providers = getProviders(project.getProviderSet());
		if (!providers.contains(IAM_SPINGCLOUD_MVN)) {
			return null; // Pass
		}
		// Check to extract the matched builtin columns.
		DbType dbType = DbType.of(datasource.getType());
		Set<BuiltinColumnDefinition> matches = asList(BuiltinColumnDefinition.values()).stream()
				// Check the specific database column name and type that are
				// supported?
				.filter(def -> safeList(table.getGenTableColumns()).stream()
						.filter(col -> equalsIgnoreCase(col.getColumnName(), def.getColumnName())
								&& def.getColumnTypes().getOrDefault(dbType, emptyList()).contains(col.getSimpleColumnType()))
						.findAny().isPresent())
				.collect(toSet());

		String title = null;
		StringBuilder warningTip = new StringBuilder();
		for (BuiltinColumnDefinition missing : difference(newHashSet(BuiltinColumnDefinition.values()), matches)) {
			if (isNull(title)) {
				warningTip.append(title = bundle.getMessage("gen.coltypes.missing.title") + "</br>");
			}
			List<String> missingColumnTypes = missing.getColumnTypes().entrySet().stream().filter(e -> e.getKey() == dbType)
					.map(e -> e.getValue()).findFirst().orElse(null);
			warningTip.append(missing.getColumnName() + " " + missingColumnTypes.toString() + " - "
					+ (missing.isRequired() ? bundle.getMessage("gen.coltypes.missing.require")
							: bundle.getMessage("gen.coltypes.missing.suggest")));
			warningTip.append("</br>");
		}
		return warningTip.toString();
	}

	/**
	 * Apply sets default show type.
	 *
	 * @param cmetadata
	 * @param col
	 */
	private void applyDefaultShowType(ColumnMetadata cmetadata, GenTableColumn col) {
		if (equalsAnyIgnoreCase(cmetadata.getSimpleColumnType(), "DATE")) {
			col.setShowType("7");// Date
		} else if (equalsAnyIgnoreCase(cmetadata.getSimpleColumnType(), "DATETIME", "TIMESTAMP")) {
			col.setShowType("8");// DateTime
		} else if (equalsAnyIgnoreCase(cmetadata.getSimpleColumnType(), "TEXT")) {
			col.setShowType("2");// textarea
		} else {
			col.setShowType("1");// normal input
		}
	}

	// TODO 后续增加专门自动分析设置默认值
	private static final List<String> DEFAULT_NO_EDIT = asList("create_by", "create_date", "update_by", "update_date",
			"del_flag");
	private static final List<String> DEFAULT_NO_LIST = asList("create_by", "create_date", "del_flag");

}