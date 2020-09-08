package com.wl4g.devops.dts.codegen.database;


import com.google.common.io.Resources;
import com.wl4g.devops.dts.codegen.bean.GenDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;

/**
 * @author vjay
 * @date 2020-09-07 17:20:00
 */
public abstract class AbstractMetadataPaser implements MetadataPaser {

    //init jdbc template

    @Override
    public List<String> queryTables(GenDatabase genDatabase) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TableMetadata queryTable(GenDatabase genDatabase,String tableName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryVersion() {
        throw new UnsupportedOperationException();
    }


    static String readSql(String sqlType ,String fileName, String... args) throws IOException {
        String sql = Resources.toString(getResource(SQL_BASE_PATH+sqlType+"/" + fileName + ".sql"), UTF_8);
        return format(sql, args);
    }

    static Connection getConnect(String databaseUrl, String user, String password, String driverClass) throws SQLException, ClassNotFoundException {
        Class.forName(driverClass);
        return DriverManager.getConnection(databaseUrl, user, password);
    }

    @Override
    public String ColumnType2AttrType(String columnType) {
        switch (columnType){
            case "varchar": return "String";
            case "int": return "Integer";
            case "datetime": return "Date";
            default: throw new UnsupportedOperationException();
        }
    }

    final private static String SQL_BASE_PATH = "com/wl4g/devops/dts/codegen/database/sql/";

    final static String QUERY_TABLE_SQL = "queryTable";

    final static String QUERY_COLUMNS_SQL = "queryColumns";

    final static String QUERY_FOREIGN_SQL = "queryForeign";


}
