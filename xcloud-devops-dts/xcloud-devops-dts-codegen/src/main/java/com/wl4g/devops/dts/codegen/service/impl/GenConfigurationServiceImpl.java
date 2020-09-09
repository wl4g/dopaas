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
import com.wl4g.devops.dts.codegen.database.MetadataPaser;
import com.wl4g.devops.dts.codegen.database.TableMetadata;
import com.wl4g.devops.dts.codegen.service.GenConfigurationService;
import com.wl4g.devops.dts.codegen.utils.ParseUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.dts.codegen.utils.ParseUtils.lineToHump;

/**
 * {@link GenConfigurationServiceImpl}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@Service
public class GenConfigurationServiceImpl implements GenConfigurationService {

    @Autowired
    private GenerateManager generateManager;

    @Autowired
    protected NamingPrototypeBeanFactory beanFactory;

    @Autowired
    private GenDatabaseDao genDatabaseDao;

    @Autowired
    private GenTableDao genTableDao;

    @Autowired
    private GenTableColumnDao genTableColumnDao;

    @Override
    public List<String> loadTables(Integer databaseId) {
        Assert2.notNullOf(databaseId,"databaseId");
        GenDatabase genDatabase = genDatabaseDao.selectByPrimaryKey(databaseId);
        Assert2.notNullOf(genDatabase,"genDatabase");
        MetadataPaser metadataPaser = getMetadataPaser(genDatabase);
        return metadataPaser.queryTables(genDatabase);
    }

    @Override
    public GenTable loadMetadata(Integer databaseId, String tableName) {
        Assert2.notNullOf(databaseId,"databaseId");
        GenDatabase genDatabase = genDatabaseDao.selectByPrimaryKey(databaseId);
        Assert2.notNullOf(genDatabase,"genDatabase");
        MetadataPaser paser = getMetadataPaser(genDatabase);
        TableMetadata tableMetadata = paser.queryTable(genDatabase, tableName);
        Assert2.notNullOf(tableMetadata,"tableMetadata");
        // TableMetadata to GenTable
        GenTable genTable = new GenTable();
        genTable.setClassName(ParseUtils.tableName2ClassName(tableMetadata.getTableName()));
        genTable.setTableName(tableMetadata.getTableName());
        genTable.setComments(tableMetadata.getComments());

        List<GenTableColumn> genTableColumns = new ArrayList<>();
        for(TableMetadata.ColumnMetadata columnMetadata : tableMetadata.getColumns()){
            GenTableColumn column = new GenTableColumn();
            column.setColumnName(columnMetadata.getColumnName());
            column.setColumnComment(columnMetadata.getComments());
            column.setColumnType(columnMetadata.getColumnType());
            column.setAttrType(paser.ColumnType2AttrType(columnMetadata.getDataType()));
            column.setAttrName(lineToHump(columnMetadata.getColumnName()));
            //TODO......

            genTableColumns.add(column);
        }
        genTable.setGenTableColumns(genTableColumns);

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
        Assert2.notNullOf(tableId,"tableId");
        GenTable genTable = genTableDao.selectByPrimaryKey(tableId);
        Assert2.notNullOf(genTable,"genTable");
        List<GenTableColumn> genTableColumns = genTableColumnDao.selectByTableId(tableId);
        genTable.setGenTableColumns(genTableColumns);
        return genTable;
    }

    @Override
    public void saveGenConfig(GenTable genTable) {
        if(Objects.nonNull(genTable.getId())){
            genTable.preUpdate();
            update(genTable);
        }else{
            genTable.preInsert();
            insert(genTable);
        }
    }

    private void insert(GenTable genTable){

        List<GenTableColumn> genTableColumns = genTable.getGenTableColumns();
        for(GenTableColumn column : genTableColumns){
            column.preInsert();
            column.setTableId(genTable.getId());
        }
        genTableColumnDao.insertBatch(genTableColumns);
        genTableDao.insertSelective(genTable);
    }

    private void update(GenTable genTable){
        genTableColumnDao.deleteByTableId(genTable.getId());
        genTableDao.updateByPrimaryKeySelective(genTable);
        List<GenTableColumn> genTableColumns = genTable.getGenTableColumns();
        for(GenTableColumn column : genTableColumns){
            column.preInsert();
            column.setTableId(genTable.getId());
        }
        genTableColumnDao.insertBatch(genTable.getGenTableColumns());
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
        //TODO find table config from db
        GenericParameter genericParameter = new GenericParameter();
        generateManager.execute(genericParameter);
    }

    private MetadataPaser getMetadataPaser(GenDatabase genDatabase){
        //TODO
        if(StringUtils.equalsIgnoreCase(genDatabase.getType(),"mysql")){
            return beanFactory.getPrototypeBean("mysqlPaser", null);
        }
        //TODO else if .......
        return beanFactory.getPrototypeBean("mysqlPaser", null);
    }
}
