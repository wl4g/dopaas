package com.wl4g.devops.dts.codegen.database;

import com.wl4g.components.core.framework.beans.PrototypeBean;
import com.wl4g.devops.dts.codegen.bean.GenDatabase;

import java.util.List;

/**
 * @author vjay
 * @date 2020-09-07 17:17:00
 */
public interface MetadataPaser extends PrototypeBean {

    List<String> queryTables(GenDatabase genDatabase);

    TableMetadata queryTable(GenDatabase genDatabase, String tableName);

    String ColumnType2AttrType(String columnType);

    //void queryColumns();

    void queryVersion();

    void queryForeign(String databaseName, String tableName) throws Exception;


}
