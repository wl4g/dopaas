package com.wl4g.devops.dts.codegen.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2020-09-07 11:42:00
 */
public class MysqlUtil {
    private static String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static String DATABASE_URL = "jdbc:mysql://localhost:3306/news";
    private static String DATABASE_URL_PREFIX = "jdbc:mysql://";
    private static String DATABASE_USER = "root";
    private static String DATABASE_PASSWORD = "tenny123";
    private static Connection con = null;
    private static DatabaseMetaData dbmd = null;

    /**
     * 初始化数据库链接
     * @param db_url 数据库地址(ex: localhost:3306/news)
     * @param db_user 用户名
     * @param db_pw 密码
     */
    public static void init(String db_url, String db_user, String db_pw){
        try {
            DATABASE_URL = DATABASE_URL_PREFIX + db_url;
            DATABASE_USER = db_user;
            DATABASE_PASSWORD = db_pw;
            Class.forName(DRIVER_CLASS);
            con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            dbmd = con.getMetaData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前用户所有表
     * @return
     */
    public static List<String> getTables(){
        List<String> tables = new ArrayList<String>();

        try {
            ResultSet rs = dbmd.getTables(null, DATABASE_USER, null, new String[] { "TABLE" });
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    /**
     * 获取表字段信息
     * @param tableName
     * @return
     */
    public static List<Map<String,String>> getTableCloumns(String tableName){
        List<Map<String,String>> columns = new ArrayList<Map<String,String>>();
        try{
            Statement stmt = con.createStatement();

            String sql = "select column_name, data_type, column_key, is_nullable, column_comment from information_schema.columns where table_name='" + tableName + "'and table_schema='" + DATABASE_URL.substring(DATABASE_URL.lastIndexOf("/") + 1, DATABASE_URL.length()) + "'";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("columnName", rs.getString("column_name"));
                map.put("dataType", rs.getString("data_type"));
                map.put("isKey", StringUtils.isEmpty(rs.getString("column_key"))?"false":"true");
                map.put("notNull", rs.getString("is_nullable").equals("YES")?"false":"true");
                map.put("comment", rs.getString("column_comment"));
                columns.add(map);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }finally{
            if(null != con){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return columns;
    }

}
