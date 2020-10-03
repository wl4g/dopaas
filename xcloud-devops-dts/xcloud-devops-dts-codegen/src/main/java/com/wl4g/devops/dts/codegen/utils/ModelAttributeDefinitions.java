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
package com.wl4g.devops.dts.codegen.utils;

/**
 * {@link ModelAttributeDefinitions}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-25
 * @since
 */
public abstract class ModelAttributeDefinitions {

	// --- Gen Common. ---

	public static final String GEN_COMMON_WATERMARK = "watermark";
	public static final String GEN_COMMON_BASESPECS = "baseSpecs";
	public static final String GEN_COMMON_JAVASPECS = "javaSpecs";
	public static final String GEN_COMMON_CSHARPSPECS = "csharpSpecs";
	public static final String GEN_COMMON_GOSPECS = "goSpecs";
	public static final String GEN_COMMON_PYTHONSPECS = "pythonSpecs";
	public static final String GEN_COMMON_VUESPECS = "vueSpecs";
	public static final String GEN_COMMON_NGSPECS = "ngSpecs";

	// --- Gen DataSource. ---

	public static final String GEN_DB = "datasource";
	public static final String GEN_DB_NAME = "dbname";
	public static final String GEN_DB_TYPE = "dbtype";
	public static final String GEN_DB_HOST = "dbhost";
	public static final String GEN_DB_PORT = "dbport";
	public static final String GEN_DB_DATABAES = "databaseName";
	public static final String GEN_DB_USERNAME = "dbusername";
	public static final String GEN_DB_PASSWORD = "dbpassword";
	public static final String GEN_DB_VERSION = "dbversion";

	// --- Gen Project. ---

	public static final String GEN_PROJECT_NAME = "projectName";
	public static final String GEN_PROJECT_ORGAN_TYPE = "organType";
	public static final String GEN_PROJECT_ORGAN_NAME = "organName";
	public static final String GEN_PROJECT_PROVIDER_SET = "providerSet";
	public static final String GEN_PROJECT_VERSION = "version";
	public static final String GEN_PROJECT_AUTHOR = "author";
	public static final String GEN_PROJECT_SINCE = "since"; // of GenTable???
	public static final String GEN_PROJECT_COPYRIGHT = "copyright";
	public static final String GEN_PROJECT_GEN_TABLES = "genTables";
	public static final String GEN_PROJECT_EXTRA_OPTIONS = "extraOptions";
	public static final String GEN_PROJECT_DESCRIPTION = "projectDescription";

	// --- Gen Module. ---

	/**
	 * Command syntax character for traversing entities to generate multiple
	 * files.
	 */
	public static final String GEN_MODULE_NAME = "moduleName";
	public static final String GEN_MODULE_SUB_NAME = "subModuleName";
	public static final String GEN_MODULE_MAP = "moduleMap";

	// --- Gen Table. ---

	public static final String GEN_TABLE_NAME = "tableName";

	/**
	 * The command syntax character when you need to traverse the module to
	 * generate multiple directories.
	 */
	public static final String GEN_TABLE_ENTITY_NAME = "entityName";
	public static final String GEN_TABLE_COMMENT = "comments";
	public static final String GEN_TABLE__FUNC_NAME = "functionName";
	public static final String GEN_TABLE_FUNC_SIMPLE_NAME = "functionSimpleName";
	public static final String GEN_TABLE_FUNC_AUTHOR = "functionAuthor";
	public static final String GEN_TABLE_COLUMNS = "genTableColumns";
	public static final String GEN_TABLE_PRIMARY = "pk";
	public static final String GEN_TABLE_OPTION_MAP = "optionMap";

	public static final String GEN_TABLE_PACKAGENAME = "packageName";
	public static final String GEN_TABLE_BEAN_SUBMODULE_PACKAGENAME = "beanSubModulePackageName";
	public static final String GEN_TABLE_DAO_SUBMODULE_PACKAGENAME = "daoSubModulePackageName";
	public static final String GEN_TABLE_SERVICE_SUBMODULE_PACKAGENAME = "serviceSubModulePackageName";
	public static final String GEN_TABLE_CONTROLLER_SUBMODULE_PACKAGENAME = "controllerSubModulePackageName";

	public static final String GEN_TABLE_ATTRTYPES = "attrTypes";

	// --- Gen Table Columns. ---

	public static final String GEN_COLUMN_NAME = "columnName";
	public static final String GEN_COLUMN_TYPE = "columnType";
	public static final String GEN_COLUMN_SIMPLE_TYPE = "simpleColumnType";
	public static final String GEN_COLUMN_SQLTYPE = "sqlType";
	public static final String GEN_COLUMN_COMMENT = "columnComment";
	public static final String GEN_COLUMN_SORT = "columnSort";
	public static final String GEN_COLUMN_ATTRTYPE = "attrType";
	public static final String GEN_COLUMN_ATTRNAME = "attrName";
	public static final String GEN_COLUMN_QUERYTYPE = "queryType";
	public static final String GEN_COLUMN_SHOWTYPE = "showType";
	public static final String GEN_COLUMN_DICTTYPE = "dictType";
	public static final String GEN_COLUMN_VALIDRULE = "validRule";
	public static final String GEN_COLUMN_ISPK = "isPk";
	public static final String GEN_COLUMN_NONULL = "noNull";
	public static final String GEN_COLUMN_ISINSERT = "isInsert";
	public static final String GEN_COLUMN_ISUPDATE = "isUpdate";
	public static final String GEN_COLUMN_ISLIST = "isList";
	public static final String GEN_COLUMN_ISQUERY = "isQuery";
	public static final String GEN_COLUMN_ISEDIT = "isEdit";

}
