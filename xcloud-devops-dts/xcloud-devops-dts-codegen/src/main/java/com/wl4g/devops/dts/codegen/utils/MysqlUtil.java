package com.wl4g.devops.dts.codegen.utils;

import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.devops.dts.codegen.database.TableMetadata;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author vjay
 * @date 2020-09-07 11:42:00
 */
public class MysqlUtil {

    private static SmartLogger log = SmartLoggerFactory.getLogger(MysqlUtil.class);

    private static Connection getConnect(String databaseUrl, String user, String password, String driverClass) throws SQLException, ClassNotFoundException {
        Class.forName(driverClass);
        return DriverManager.getConnection(databaseUrl, user, password);
    }

    /**
     * Get Table Info
     *
     * @param databaseUrl
     * @param user
     * @param password
     * @param driverClass
     * @param tableName
     * @return
     */
    public static TableMetadata getTableInfo(String databaseUrl, String user, String password, String driverClass, String tableName) {
        Connection connect = null;
        try {
            connect = getConnect(databaseUrl, user, password, driverClass);
            TableMetadata table = getTableBaseInfo(connect, tableName);
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
     * 获取所有表
     *
     * @return
     */
    public static List<String> getTables(String databaseUrl, String user, String password, String driverClass) {
        List<String> tables = new ArrayList<String>();
        Connection connect = null;
        try {
            connect = getConnect(databaseUrl, user, password, driverClass);
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
     * 获取表字段信息
     *
     * @param tableName
     * @return
     */
    public static List<TableMetadata.ColumnMetadata> getTableCloumns(Connection connect, String tableName) throws SQLException {
        List<TableMetadata.ColumnMetadata> columns = new ArrayList<>();
        Statement stmt = connect.createStatement();
        String sql = "select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns " +
                "where table_name = '" + tableName + "' and table_schema = (select database()) order by ordinal_position";
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            TableMetadata.ColumnMetadata column = new TableMetadata.ColumnMetadata();
            column.setColumnName(rs.getString("columnName"));
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
     *
     * @param tableName
     * @return
     */
    public static TableMetadata getTableBaseInfo(Connection connect, String tableName) throws SQLException {
        TableMetadata table = new TableMetadata();
        Statement stmt = connect.createStatement();
        String sql = "select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables " +
                "where table_schema = (select database()) and table_name = '" + tableName + "' limit 1";
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {//just one
            HashMap<String, String> map = new HashMap<String, String>();
            table.setTableName(rs.getString("tableName"));
            table.setComments(rs.getString("tableComment"));
            return table;
        }
        return null;
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        List<String> gzsm = getTables("jdbc:mysql://10.0.0.160:3306/devops_dev", "gzsm", "gzsm@%#jh?", "com.mysql.jdbc.Driver");
        gzsm.forEach((str) -> System.out.println(str));

        /*TableMetadata tableInfo = getTableInfo("jdbc:mysql://10.0.0.160:3306/devops_dev", "gzsm", "gzsm@%#jh?", "com.mysql.jdbc.Driver", "dts_gen_field");
        System.out.println(tableInfo);*/

    }

}
