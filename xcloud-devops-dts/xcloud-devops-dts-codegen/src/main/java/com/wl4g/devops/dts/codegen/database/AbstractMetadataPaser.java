package com.wl4g.devops.dts.codegen.database;


import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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
        String path = SQL_BASE_PATH+sqlType+"/" + fileName + ".sql";
        //String sql = Resources.toString(getResource(path), UTF_8);

        File jsonFile = ResourceUtils.getFile("classpath:"+path);
        String sql = FileUtils.readFileToString(jsonFile);

        return String.format(sql, args);
    }

    static Connection getConnect(String databaseUrl, String user, String password, String driverClass) throws SQLException, ClassNotFoundException {
        Class.forName(driverClass);
        return DriverManager.getConnection(databaseUrl, user, password);
    }

    @Override
    public String ColumnType2AttrType(String columnType) {
        if(StringUtils.isBlank(columnType)){
            return null;
        }
        switch (columnType){
            case "varchar": return "String";
            case "int": return "Integer";
            case "datetime": return "Date";
            //TODO ......
            default: throw new UnsupportedOperationException();
        }
    }

    //final private static String SQL_BASE_PATH = "com/wl4g/devops/dts/codegen/database/sql/";
    final private static String SQL_BASE_PATH = "sql/";

    final static String QUERY_TABLE_SQL = "queryTable";

    final static String QUERY_COLUMNS_SQL = "queryColumns";

    final static String QUERY_FOREIGN_SQL = "queryForeign";


}
