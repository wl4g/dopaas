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
import com.wl4g.devops.dts.codegen.core.GenerateManager;
import com.wl4g.devops.dts.codegen.core.param.GenericParameter;
import com.wl4g.devops.dts.codegen.dao.GenDataSourceDao;
import com.wl4g.devops.dts.codegen.dao.GenTableColumnDao;
import com.wl4g.devops.dts.codegen.dao.GenTableDao;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.CodeKind;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.ConverterKind;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
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
	private NamingPrototypeBeanFactory beanFactory;

	@Autowired
	private GenericOperatorAdapter<ConverterKind, DbTypeConverter> typeConverter;

	@Autowired
	private GenerateManager genManager;

	@Autowired
	private GenDataSourceDao genDatabaseDao;

	@Autowired
	private GenTableDao genTableDao;

	@Autowired
	private GenTableColumnDao genColumnDao;

	@Override
	public List<String> loadTables(Integer databaseId) {
		notNullOf(databaseId, "databaseId");
		GenDataSource genDatabase = genDatabaseDao.selectByPrimaryKey(databaseId);
		notNullOf(genDatabase, "genDatabase");
		MetadataResolver resolver = getMetadataPaser(genDatabase);
		return resolver.findTables();
	}

	@Override
	public GenTable loadMetadata(Integer databaseId, String tableName) {
		notNullOf(databaseId, "databaseId");
		GenDataSource genDS = genDatabaseDao.selectByPrimaryKey(databaseId);
		notNullOf(genDS, "genDatabase");

		MetadataResolver resolver = getMetadataPaser(genDS);
		TableMetadata metadata = resolver.findTableDescribe(tableName);
		notNullOf(metadata, "tableMetadata");

		// TableMetadata to GenTable
		GenTable genTab = new GenTable();
		genTab.setClassName(ParseUtils.tableName2ClassName(metadata.getTableName()));
		genTab.setTableName(metadata.getTableName());
		genTab.setComments(metadata.getComments());

		List<GenTableColumn> genColumns = new ArrayList<>();
		for (ColumnMetadata colMetadata : metadata.getColumns()) {
			GenTableColumn col = new GenTableColumn();
			col.setColumnName(colMetadata.getColumnName());
			col.setColumnComment(colMetadata.getComments());
			col.setColumnType(colMetadata.getColumnType());
			col.setAttrName(lineToHump(colMetadata.getColumnName()));
			// Converting java type
			DbTypeConverter conv = typeConverter.forOperator(genDS.getType());
			// TODO
			col.setAttrType(conv.convertToCodeType(colMetadata.getDataType(), CodeKind.JAVA));

			// Sets defaults
			col.setIsInsert("1");
			col.setIsUpdate("1");
			col.setIsList("1");
			col.setIsEdit("1");
			col.setNoNull("1");
			col.setQueryType("1");
			col.setShowType("1");
			if (StringUtils.equalsIgnoreCase(colMetadata.getColumnKey(), "PRI")) {
				col.setIsPk("1");
				col.setIsList("0");
				col.setNoNull("0");
			}
			genColumns.add(col);
		}
		genTab.setGenTableColumns(genColumns);

		return genTab;
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
	private MetadataResolver getMetadataPaser(GenDataSource gen) {
		return beanFactory.getPrototypeBean(gen.getType());
	}

}
