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
package com.wl4g.dopaas.common.utils;

/**
 * {@link JdbcDefinition}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-16 v1.0.0
 * @since v1.0.0
 */
public interface JdbcDefinition {

    String HSQL = "hsql";
    String HSQL_DRIVER = "org.hsqldb.jdbcDriver";

    String DB2 = "db2";
    String DB2_DRIVER = "COM.ibm.db2.jdbc.app.DB2Driver";

    String POSTGRESQL = "postgresql";
    String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    String SYBASE = "sybase";
    String SYBASE_DRIVER = "com.sybase.jdbc.SybDriver";
    String SYBASE_DRIVER3 = "com.sybase.jdbc3.jdbc.SybDriver";

    String SQL_SERVER = "sqlserver";
    String SQL_SERVER_DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
    String SQL_SERVER_DRIVER_SQLJDBC4 = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String SQL_SERVER_DRIVER_JTDS = "net.sourceforge.jtds.jdbc.Driver";

    String ORACLE = "oracle";
    String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
    String ORACLE_DRIVER2 = "oracle.jdbc.driver.OracleDriver";

    String ALI_ORACLE = "AliOracle";
    String ALI_ORACLE_DRIVER = "com.alibaba.jdbc.AlibabaDriver";

    String MYSQL = "mysql";
    String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    String MYSQL_DRIVER_6 = "com.mysql.cj.jdbc.Driver";

    String MARIADB = "mariadb";
    String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";

    String DERBY = "derby";
    String DERBY_DRIVER = "org.apache.derby.jdbc.ClientDriver";

    String H2 = "h2";
    String H2_DRIVER = "org.h2.Driver";

    String DM = "dm";
    String DM_DRIVER = "dm.jdbc.driver.DmDriver";

    String KINGBASE = "kingbase";
    String KINGBASE_DRIVER = "com.kingbase.Driver";

    String GBASE = "gbase";
    String GBASE_DRIVER = "com.gbase.jdbc.Driver";

    String OCEANBASE = "oceanbase";
    String OCEANBASE_DRIVER = "com.mysql.jdbc.Driver";
    String INFORMIX = "informix";

    String TIDB = "tidb";
    String TIDB_DRIVER = "com.mysql.jdbc.Driver";

    String ODPS = "odps";
    String ODPS_DRIVER = "com.aliyun.odps.jdbc.OdpsDriver";

    String TERADATA = "teradata";
    String TERADATA_DRIVER = "com.teradata.jdbc.TeraDriver";

    String LOG4JDBC = "log4jdbc";
    String LOG4JDBC_DRIVER = "net.sf.log4jdbc.DriverSpy";

    String HIVE = "hive";
    String HIVE_DRIVER = "org.apache.hadoop.hive.jdbc.HiveDriver";
    String HIVE_DRIVER2 = "org.apache.hive.jdbc.HiveDriver";

    String PHOENIX = "phoenix";
    String PHOENIX_DRIVER = "org.apache.phoenix.jdbc.PhoenixDriver";

    String ENTERPRISEDB = "edb";
    String ENTERPRISEDB_DRIVER = "com.edb.Driver";

    String KYLIN = "kylin";
    String KYLIN_DRIVER = "org.apache.kylin.jdbc.Driver";

    String SQLITE = "sqlite";
    String SQLITE_DRIVER = "org.sqlite.JDBC";

    String PRESTO = "presto";
    String PRESTO_DRIVER = "io.prestosql.jdbc.PrestoDriver";

    String ALI_ELASTICSEARCH = "ali_elasticsearch";
    String ALI_ELASTICSEARCH_DRIVER = "com.alibaba.xdriver.elastic.jdbc.ElasticDriver";

    String CLICKHOUSE = "clickhouse";
    String CLICKHOUSE_DRIVER = "ru.yandex.clickhouse.ClickHouseDriver";
}
