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
package com.wl4g.devops.dts.kit.hbase.utils;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static java.lang.String.format;

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
public class HikariDataSourceFactory {
	final protected Log log = LogFactory.getLog(getClass());

	/**
	 * Rmdb datasource
	 */
	final private DataSource datasource;

	public HikariDataSourceFactory(String driver, String url, String username, String password, int maxConnections) {
		this.datasource = createDataSource(driver, url, username, password, maxConnections);
	}

	/**
	 * Gets rmdb connection.
	 * 
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		return datasource.getConnection();
	}

	/**
	 * Create datasource.
	 * 
	 * @return
	 */
	protected DataSource createDataSource(String driver, String jdbcUrl, String username, String password, int maxConnections) {
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

}