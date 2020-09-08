package com.wl4g.devops.dts.codegen.bean;

import com.wl4g.components.core.bean.BaseBean;

import java.util.List;

public class GenTable extends BaseBean {
    private static final long serialVersionUID = 6815608076300843748L;

    private Integer databaseId;

    private String tableName;

    private String className;

    private String comments;

    private String parentTableName;

    private String parentTableFkName;

    private String dataSourceName;

    private String tplCategory;

    private String packageName;

    private String moduleName;

    private String subModuleName;

    private String functionName;

    private String functionNameSimple;

    private String functionAuthor;

    private String genBaseDir;

    private String options;

    //extend
    private List<GenTableColumn> genTableColumns;

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }

    public String getParentTableName() {
        return parentTableName;
    }

    public void setParentTableName(String parentTableName) {
        this.parentTableName = parentTableName == null ? null : parentTableName.trim();
    }

    public String getParentTableFkName() {
        return parentTableFkName;
    }

    public void setParentTableFkName(String parentTableFkName) {
        this.parentTableFkName = parentTableFkName == null ? null : parentTableFkName.trim();
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName == null ? null : dataSourceName.trim();
    }

    public String getTplCategory() {
        return tplCategory;
    }

    public void setTplCategory(String tplCategory) {
        this.tplCategory = tplCategory == null ? null : tplCategory.trim();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName == null ? null : packageName.trim();
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName == null ? null : moduleName.trim();
    }

    public String getSubModuleName() {
        return subModuleName;
    }

    public void setSubModuleName(String subModuleName) {
        this.subModuleName = subModuleName == null ? null : subModuleName.trim();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName == null ? null : functionName.trim();
    }

    public String getFunctionNameSimple() {
        return functionNameSimple;
    }

    public void setFunctionNameSimple(String functionNameSimple) {
        this.functionNameSimple = functionNameSimple == null ? null : functionNameSimple.trim();
    }

    public String getFunctionAuthor() {
        return functionAuthor;
    }

    public void setFunctionAuthor(String functionAuthor) {
        this.functionAuthor = functionAuthor == null ? null : functionAuthor.trim();
    }

    public String getGenBaseDir() {
        return genBaseDir;
    }

    public void setGenBaseDir(String genBaseDir) {
        this.genBaseDir = genBaseDir == null ? null : genBaseDir.trim();
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options == null ? null : options.trim();
    }

    public List<GenTableColumn> getGenTableColumns() {
        return genTableColumns;
    }

    public void setGenTableColumns(List<GenTableColumn> genTableColumns) {
        this.genTableColumns = genTableColumns;
    }
}