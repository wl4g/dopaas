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
package com.wl4g.dopaas.lcdp.dds.service.handler;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.common.lang.ClassUtils2.resolveClassNameNullable;
import static com.wl4g.component.common.reflect.ReflectionUtils2.findFieldNullable;
import static com.wl4g.component.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.component.common.reflect.ReflectionUtils2.makeAccessible;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.component.common.lang.StringUtils2;
import com.wl4g.dopaas.lcdp.dds.service.util.JdbcDefinition;

/**
 * {@link SQLImageEvaluatorFactory}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
public class SQLImageEvaluatorFactory {

    private static final Map<String, Class<? extends SQLImageEvaluator>> REGISTRY = new HashMap<>(4);

    static {
        REGISTRY.put("default", StandardImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.MYSQL_DRIVER, MySQLImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.MARIADB_DRIVER, MariadbImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.POSTGRESQL_DRIVER, PostgresqlImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.ORACLE_DRIVER, OracleImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.SQL_SERVER_DRIVER, SqlServerImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.DB2_DRIVER, Db2ImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.H2_DRIVER, H2ImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.SQLITE_DRIVER, SqliteImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.DERBY_DRIVER, DerbyImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.SYBASE_DRIVER, SybaseImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.TIDB_DRIVER, TidbImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.PHOENIX_DRIVER, PhoenixImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.KYLIN_DRIVER, KylinImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.PRESTO_DRIVER, PrestoImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.CLICKHOUSE_DRIVER, ClickhouseImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.LOG4JDBC_DRIVER, Log4jdbcImageEvaluator.class);
        REGISTRY.put(JdbcDefinition.ALI_ELASTICSEARCH_DRIVER, Log4jdbcImageEvaluator.class);
    }

    public static SQLImageEvaluator getEvaluator(JdbcTemplate jdbcTemplate) {
        String driverClassName = null;
        DataSource dataSource = jdbcTemplate.getDataSource();
        notNull(dataSource, IllegalStateException.class, "Unable get JdbcTemplate.dataSource is null.");

        if (nonNull(hikariDriverClassField)) {
            makeAccessible(hikariDriverClassField);
            driverClassName = getField(hikariDriverClassField, dataSource);
        } else if (nonNull(druidDriverClassField)) {
            makeAccessible(druidDriverClassField);
            driverClassName = getField(druidDriverClassField, dataSource);
        } else {
            throw new UnsupportedOperationException(format("No supported dataSource driver parse. - %s", dataSource));
        }
        if (isNull(driverClassName)) {
            throw new IllegalStateException(format("Failed to get dataSource driver class. - %s", dataSource));
        }

        for (Entry<String, Class<? extends SQLImageEvaluator>> ent : REGISTRY.entrySet()) {
            if (StringUtils2.equals(driverClassName, ent.getKey())) {
                try {
                    return ent.getValue().getConstructor(JdbcTemplate.class).newInstance(jdbcTemplate);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return null;
    }

    private static final Field hikariDriverClassField = findFieldNullable(
            resolveClassNameNullable("com.zaxxer.hikari.HikariDataSource"), "driverClassName", String.class);

    private static final Field druidDriverClassField = findFieldNullable(
            resolveClassNameNullable("com.alibaba.druid.pool.DruidDataSource"), "driverClass", String.class);

}
