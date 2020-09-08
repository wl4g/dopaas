package com.wl4g.devops.dts.codegen.database;

import java.util.List;

/**
 * @author vjay
 * @date 2020-09-07 11:12:00
 */
public class TableMetadata {

    //表的名称
    private String tableName;
    //表的备注
    private String comments;
    //表的主键
    private ColumnMetadata pk;
    //表的字段
    private List<ColumnMetadata> columns;
    //类名(第一个字母大写)，如：sys_user => SysUser
    private String className;
    //类名(第一个字母小写)，如：sys_user => sysUser
    private String classname;

    public static class ColumnMetadata {
        //列名
        private String columnName;
        //主键？
        private String columnKey;
        //列名类型
        private String dataType;

        private String columnType;
        //列名备注
        private String comments;
        //属性名称(第一个字母大写)，如：user_name => UserName
        private String attrName;
        //属性名称(第一个字母小写)，如：user_name => userName
        private String attrname;
        //属性类型
        private String attrType;
        //auto_increment
        private String extra;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public String getAttrName() {
            return attrName;
        }

        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        public String getAttrname() {
            return attrname;
        }

        public void setAttrname(String attrname) {
            this.attrname = attrname;
        }

        public String getAttrType() {
            return attrType;
        }

        public void setAttrType(String attrType) {
            this.attrType = attrType;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getColumnKey() {
            return columnKey;
        }

        public void setColumnKey(String columnKey) {
            this.columnKey = columnKey;
        }

        public String getColumnType() {
            return columnType;
        }

        public void setColumnType(String columnType) {
            this.columnType = columnType;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ColumnMetadata getPk() {
        return pk;
    }

    public void setPk(ColumnMetadata pk) {
        this.pk = pk;
    }

    public List<ColumnMetadata> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMetadata> columns) {
        this.columns = columns;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
