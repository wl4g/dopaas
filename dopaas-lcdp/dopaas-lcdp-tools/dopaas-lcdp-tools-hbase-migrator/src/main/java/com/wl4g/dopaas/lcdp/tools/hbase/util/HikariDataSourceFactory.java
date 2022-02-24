/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.lcdp.tools.hbase.util;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.findMethodNullable;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.invokeMethod;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link HikariDataSourceFactory}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月19日
 * @since
 */
public class HikariDataSourceFactory implements Closeable {

    protected final Log log = LogFactory.getLog(getClass());

    /**
     * RDBMS DataSource
     */
    private final DataSource datasource;

    public HikariDataSourceFactory(String driver, String url, String username, String password, int maxConnections) {
        this.datasource = createDataSource0(driver, url, username, password, maxConnections);
    }

    /**
     * Gets RDBMS DataSource.
     * 
     * @throws Exception
     */
    public DataSource getDataSource() throws Exception {
        return datasource;
    }

    /**
     * Gets RDBMS connection.
     * 
     * @throws Exception
     */
    public Connection getConnection() throws Exception {
        return datasource.getConnection();
    }

    /**
     * Create DataSource.
     * 
     * @return
     */
    private DataSource createDataSource0(String driver, String jdbcUrl, String username, String password, int maxConnections) {
        hasTextOf(driver, "driver");
        hasTextOf(jdbcUrl, "jdbcUrl");
        hasTextOf(username, "username");
        hasTextOf(password, "password");

        // props.put("dataSource.logWriter", new PrintWriter(System.out));
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxConnections);
        config.setMinimumIdle((int) (maxConnections * 0.1f));
        DataSource datasource = new HikariDataSource(config);
        log.info(format("Creating datasource: %s of jdbcUrl: %s", datasource, jdbcUrl));
        return datasource;
    }

    @Override
    public void close() throws IOException {
        Method closeM = findMethodNullable(datasource.getClass(), "close");
        if (nonNull(closeM)) {
            invokeMethod(closeM, datasource);
        }
    }

}