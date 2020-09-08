package com.wl4g.devops.dts.codegen.database;

import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import com.wl4g.devops.dts.codegen.utils.MysqlUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author vjay
 * @date 2020-09-07 17:17:00
 */
public class MySQLV57xMetadataPaser extends AbstractMetadataPaser{

    private static SmartLogger log = SmartLoggerFactory.getLogger(MysqlUtil.class);

    final private static String SQL_TYPE = "mysql";

    final private static String JDBC_CLASS_NAME = "com.mysql.jdbc.Driver";

    @Override
    public List<String> queryTables(GenDatabase genDatabase) {
        String databaseUrl = "jdbc:mysql://"+genDatabase.getHost()+":"+genDatabase.getPort()+"/"+genDatabase.getDatabase();
        return getTables(databaseUrl,genDatabase.getUsername(),genDatabase.getPassword());
    }

    @Override
    public TableMetadata queryTable(GenDatabase genDatabase,String tableName) {
        String databaseUrl = "jdbc:mysql://"+genDatabase.getHost()+":"+genDatabase.getPort()+"/"+genDatabase.getDatabase();
        return getTable(databaseUrl, genDatabase.getUsername(), genDatabase.getPassword(), tableName);
    }

    @Override
    public void queryForeign(String databaseName, String tableName) throws Exception {
        String sql = readSql(SQL_TYPE,QUERY_FOREIGN_SQL, databaseName, tableName);
        //TODO
    }

    /**
     * 获取所有表
     *
     * @return
     */
    private static List<String> getTables(String databaseUrl, String user, String password) {
        List<String> tables = new ArrayList<String>();
        Connection connect = null;
        try {
            connect = getConnect(databaseUrl, user, password, JDBC_CLASS_NAME);
            DatabaseMetaData dbmd = connect.getMetaData();

            ResultSet rs = dbmd.getTables(null, null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != connect) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    log.error("close connect fail", e);
                }
            }
        }
        return tables;
    }

    /**
     * Get Table Info
     */
    private static TableMetadata getTable(String databaseUrl, String user, String password, String tableName) {
        Connection connect = null;
        try {
            connect = getConnect(databaseUrl, user, password, JDBC_CLASS_NAME);
            TableMetadata table = getTableInfo(connect, tableName);
            Assert2.notNullOf(table, "table");
            List<TableMetadata.ColumnMetadata> tableCloumns = getTableCloumns(connect, tableName);
            table.setColumns(tableCloumns);
            return table;
        } catch (Exception e) {
            log.error("get Table Info error", e);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取表字段信息
     */
    private static List<TableMetadata.ColumnMetadata> getTableCloumns(Connection connect, String tableName) throws Exception {
        List<TableMetadata.ColumnMetadata> columns = new ArrayList<>();
        Statement stmt = connect.createStatement();
        String sql = readSql(SQL_TYPE, QUERY_COLUMNS_SQL, tableName);
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            TableMetadata.ColumnMetadata column = new TableMetadata.ColumnMetadata();
            column.setColumnName(rs.getString("columnName"));
            column.setColumnType(rs.getString("columnType"));
            column.setDataType(rs.getString("dataType"));
            column.setComments(rs.getString("columnComment"));
            column.setColumnKey(rs.getString("columnKey"));
            column.setExtra(rs.getString("extra"));
            columns.add(column);
        }
        return columns;
    }


    /**
     * 获取表字段信息
     */
    private static TableMetadata getTableInfo(Connection connect, String tableName) throws Exception {
        TableMetadata table = new TableMetadata();
        Statement stmt = connect.createStatement();
        String sql = readSql(SQL_TYPE, QUERY_TABLE_SQL, tableName);
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {//just one
            HashMap<String, String> map = new HashMap<String, String>();
            table.setTableName(rs.getString("tableName"));
            table.setComments(rs.getString("tableComment"));
            return table;
        }
        return null;
    }
}
