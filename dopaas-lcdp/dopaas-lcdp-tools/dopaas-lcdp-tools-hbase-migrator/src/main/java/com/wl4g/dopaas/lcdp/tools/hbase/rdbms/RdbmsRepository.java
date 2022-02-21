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
package com.wl4g.dopaas.lcdp.tools.hbase.rdbms;

import java.sql.Connection;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.dopaas.lcdp.tools.hbase.util.HikariDataSourceFactory;

/**
 * {@link RdbmsRepository}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月18日
 * @since
 */
public class RdbmsRepository {
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * DataSource factory.
     */
    final private HikariDataSourceFactory factory;

    public RdbmsRepository(String driver, String url, String username, String password, int maxConnections) {
        this.factory = new HikariDataSourceFactory(driver, url, username, password, maxConnections);
    }

    /**
     * Save row data.
     * 
     * @param insertSql
     * @throws Exception
     */
    public void saveRow(String insertSql) throws Exception {
        Connection conn = null;
        try {
            QueryRunner runner = new QueryRunner();
            conn = factory.getConnection();
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