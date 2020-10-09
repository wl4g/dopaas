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
 * {@link ModelAttributeDefinition}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-25
 * @since
 */
public abstract class ModelAttributeDefinition {

	//
	// --- Gen Common. ---
	//

	public static final String GEN_COMMON_WATERMARK = "watermark";
	public static final String GEN_COMMON_BASESPECS = "baseSpecs";
	public static final String GEN_COMMON_JAVASPECS = "javaSpecs";
	public static final String GEN_COMMON_CSHARPSPECS = "csharpSpecs";
	public static final String GEN_COMMON_GOSPECS = "goSpecs";
	public static final String GEN_COMMON_PYTHONSPECS = "pythonSpecs";
	public static final String GEN_COMMON_VUESPECS = "vueSpecs";
	public static final String GEN_COMMON_NGSPECS = "ngSpecs";

	//
	// --- Gen DataSource. ---
	//

	public static final String GEN_DB = "datasource";
	public static final String GEN_DB_NAME = "dbname";
	public static final String GEN_DB_TYPE = "dbtype";
	public static final String GEN_DB_HOST = "dbhost";
	public static final String GEN_DB_PORT = "dbport";
	public static final String GEN_DB_DATABAES = "databaseName";
	public static final String GEN_DB_USERNAME = "dbusername";
	public static final String GEN_DB_PASSWORD = "dbpassword";
	public static final String GEN_DB_VERSION = "dbversion";

	//
	// --- Gen Project. ---
	//

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

	//
	// --- Gen Module. ---
	//

	/**
	 * Command syntax character for traversing entities to generate multiple
	 * files.
	 */
	public static final String GEN_MODULE_NAME = "moduleName";
	public static final String GEN_MODULE_SUB_NAME = "subModuleName";
	public static final String GEN_MODULE_MAP = "moduleMap";

	//
	// --- Gen Table. ---
	//

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
	public static final String GEN_TABLE_EXTRA_OPTIONS = "tableExtraOptions";

	public static final String GEN_TABLE_PACKAGENAME = "packageName";
	public static final String GEN_TABLE_BEAN_SUBMODULE_PACKAGENAME = "beanSubModulePackageName";
	public static final String GEN_TABLE_DAO_SUBMODULE_PACKAGENAME = "daoSubModulePackageName";
	public static final String GEN_TABLE_SERVICE_SUBMODULE_PACKAGENAME = "serviceSubModulePackageName";
	public static final String GEN_TABLE_CONTROLLER_SUBMODULE_PACKAGENAME = "controllerSubModulePackageName";

	//
	// --- Gen Table Columns. ---
	//

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

	//
	// --- Defaults configuration attributes. ---
	//

	/**
	 * Default entry application name. (e.g: The generated project springboot
	 * controller appName or spark program appName)
	 */
	public static final String GEN_DEF_ENTRYAPP_NAME = "entryAppName";
	// e.g: portal-services.mydomain.com => portal-services
	public static final String GEN_DEF_ENTRYAPP_SUBDOMAIN = "entryAppSubDomain";
	public static final String GEN_DEF_ENTRYAPP_PORT = "entryAppPort";

	// Default config of deploy topDomain. (dev)
	public static final String GEN_DEF_DEV_TOPDOMAIN = "devTopDomain";
	// Default config of backend service host. (dev)
	public static final String GEN_DEF_DEV_SERVICE_HOST = "devServiceHost";
	// Default config of frontend view service host. (dev)
	public static final String GEN_DEF_DEV_VIEW_SERVICE_HOST = "devViewServiceHost";
	// Default config of view startup port. (dev)
	public static final String GEN_DEF_DEV_VIEW_SERVICE_PORT = "devViewServicePort";
	// Default config of redis server host. (dev)
	public static final String GEN_DEF_DEV_REDIS_HOST = "devRedisHost";

	// Automatically generated according to the data source information
	// configured by the generator.

	// Default config of mysql server host. (dev)
	// public static final String GEN_DEF_DEV_MYSQL_HOST = "devMysqlHost";
	// // Default config of oracle server host. (dev)
	// public static final String GEN_DEF_DEV_ORACLE_HOST = "devOracleHost";
	// // Default config of postgreSQL server host. (dev)
	// public static final String GEN_DEF_DEV_POSTGRE_HOST = "devPostgreHost";

	// Default config of deploy topDomain. (fat)
	public static final String GEN_DEF_FAT_TOPDOMAIN = "fatTopDomain";
	// Default config of backend service host. (fat)
	public static final String GEN_DEF_FAT_SERVICE_HOST = "fatServiceHost";
	// Default config of frontend view service host. (fat)
	public static final String GEN_DEF_FAT_VIEW_SERVICE_HOST = "fatViewServiceHost";
	// Default config of redis server host. (fat)
	public static final String GEN_DEF_FAT_REDIS_HOST = "fatRedisHost";
	// Default config of mysql server host. (fat)
	public static final String GEN_DEF_FAT_MYSQL_HOST = "fatMysqlHost";
	// Default config of oracle server host. (dev)
	public static final String GEN_DEF_FAT_ORACLE_HOST = "fatOracleHost";
	// Default config of postgreSQL server host. (dev)
	public static final String GEN_DEF_FAT_POSTGRE_HOST = "fatPostgreHost";

	// Default config of deploy topDomain. (uat)
	public static final String GEN_DEF_UAT_TOPDOMAIN = "uatTopDomain";
	// Default config of backend service host. (uat)
	public static final String GEN_DEF_UAT_SERVICE_HOST = "uatServiceHost";
	// Default config of frontend view service host. (uat)
	public static final String GEN_DEF_UAT_VIEW_SERVICE_HOST = "uatViewServiceHost";
	// Default config of redis server host. (uat)
	public static final String GEN_DEF_UAT_REDIS_HOST = "uatRedisHost";
	// Default config of mysql server host. (uat)
	public static final String GEN_DEF_UAT_MYSQL_HOST = "uatMysqlHost";
	// Default config of oracle server host. (uat)
	public static final String GEN_DEF_UAT_ORACLE_HOST = "uatOracleHost";
	// Default config of postgreSQL server host. (uat)
	public static final String GEN_DEF_UAT_POSTGRE_HOST = "uatPostgreHost";

	// Default config of deploy topDomain. (pro)
	public static final String GEN_DEF_PRO_TOPDOMAIN = "proTopDomain";
	// Default config of backend service host. (pro)
	public static final String GEN_DEF_PRO_SERVICE_HOST = "proServiceHost";
	// Default config of frontend view service host. (pro)
	public static final String GEN_DEF_PRO_VIEW_SERVICE_HOST = "proViewServiceHost";
	// Default config of redis server host. (pro)
	public static final String GEN_DEF_PRO_REDIS_HOST = "proRedisHost";
	// Default config of mysql server host. (pro)
	public static final String GEN_DEF_PRO_MYSQL_HOST = "proMysqlHost";
	// Default config of oracle server host. (pro)
	public static final String GEN_DEF_PRO_ORACLE_HOST = "proOracleHost";
	// Default config of postgreSQL server host. (pro)
	public static final String GEN_DEF_PRO_POSTGRE_HOST = "proPostgreHost";

}
