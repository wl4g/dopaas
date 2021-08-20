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
package com.wl4g.dopaas.lcdp.dds.service.util;

import java.util.List;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.dopaas.common.utils.JdbcUtil;
import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link JdbcUtilTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-18 v1.0.0
 * @since v1.0.0
 */
public class JdbcUtilTests {

    @Test
    public void testGetPrimaryKeys() throws Exception {
        HikariDataSource ds = initTestingDatabase();
        List<String> primaryKeys = JdbcUtil.getTablePrimaryKeys(ds, "t_user");
        System.out.println(primaryKeys);
    }

    private HikariDataSource initTestingDatabase() {
        final HikariDataSource ds = new HikariDataSource();
        // ds.setDriverClassName("org.h2.Driver");
        // ds.setJdbcUrl("jdbc:h2:/tmp/h2testdb2;FORBID_CREATION=FALSE");

        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/mysql?serverTimezone=UTC&useSSL=false&characterEncoding=utf-8");
        ds.setUsername("root");
        ds.setPassword("root");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        // Create database.
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS `test_db`");
        jdbcTemplate.execute("CREATE SCHEMA `test_db`");

        // Create tables.
        jdbcTemplate.execute("DROP TABLE IF EXISTS `test_db`.`t_user`");

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS `test_db`.`t_user`(`id` bigint(25) PRIMARY KEY NOT NULL,`name` varchar(32) NOT NULL)");

        return ds;
    }

}
