package com.wl4g.devops.common.bean.umc.model;

/**
 * @author vjay
 * @date 2020-03-30 17:01:00
 */
public enum DataSourceProvide {
    /**
     * Mysql
     */
    MYSQL,

    /**
     * Oracle
     */
    ORACLE
    ;


    public static DataSourceProvide parse(String dataSourceProvider) {
        for (DataSourceProvide cacl : DataSourceProvide.values()) {
            if (cacl.toString().equalsIgnoreCase(dataSourceProvider) || dataSourceProvider.equalsIgnoreCase(cacl.name())) {
                return cacl;
            }
        }
        throw new IllegalArgumentException("Unable to parse the provided acl " + dataSourceProvider);
    }

    public static String[] dataSourceProvides() {
        String[] result = new String[DataSourceProvide.values().length];
        for (int i = 0; i < DataSourceProvide.values().length; i++) {
            result[i] = DataSourceProvide.values()[i].toString();
        }
        return result;
    }

}
