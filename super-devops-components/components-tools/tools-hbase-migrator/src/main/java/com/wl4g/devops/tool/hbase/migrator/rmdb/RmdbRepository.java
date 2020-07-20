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
package com.wl4g.devops.tool.hbase.migrator.rmdb;

import java.sql.Connection;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.tool.hbase.migrator.utils.HikariDataSourceFactory;

/**
 * {@link RmdbRepository}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月18日
 * @since
 */
public class RmdbRepository {
	final protected Log log = LogFactory.getLog(getClass());

	/**
	 * Datasource factory.
	 */
	final private HikariDataSourceFactory dbFactory;

	public RmdbRepository(String driver, String url, String username, String password, int maxConnections) {
		this.dbFactory = new HikariDataSourceFactory(driver, url, username, password, maxConnections);
	}

	/**
	 * Save row data.
	 * 
	 * @param insertSql
	 * @throws Exception
	 */
	public void saveRowdata(String insertSql) throws Exception {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = dbFactory.getConnection();
			int num = runner.update(conn, insertSql);
			if (num <= 0) {
				log.warn("Failed save rowdata for sql: " + insertSql);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.close(conn);
		}

	}

}