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

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.dopaas.lcdp.dds.service.handler.AbstractImageEvaluator.EvaluatorProperties;
import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link SQLImageEvaluatorFactoryTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
public class SQLImageEvaluatorFactoryTests {

    @Test
    public void testSQLImageEvaluateForInsertSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate("insert into `test_db`.`t_user` (`id`,`name`) VALUES (1000, 'jack1000')");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    @Test
    public void testSQLImageEvaluateForInsertMultiValuesSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate("insert into `test_db`.`t_user` (`id`,`name`) VALUES (1000, 'jack1000'), (2000, 'jack2000')");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    @Test
    public void testSQLImageEvaluateForInsertValuesSelectSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate(
                    "insert into `test_db`.`t_user2` (`id`,`name`) select * from (select * from `test_db`.`t_user` where `id`>=100) as tab");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    @Test
    public void testSQLImageEvaluateForInsertSelectSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate(
                    "insert into `test_db`.`t_user2` select * from (select * from `test_db`.`t_user` where `id`>=100) as tab");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    @Test
    public void testSQLImageEvaluateForDeleteSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate("delete from `test_db`.`t_user` where id >= 100 and id < 200 or `name` like '%jack%'");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    @Test
    public void testSQLImageEvaluateForDeleteSelectSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate("delete from `test_db`.`t_user` where id in (select id from `test_db`.`t_user` where id>= 100)");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    @Test
    public void testSQLImageEvaluateForUpdateSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate("update `test_db`.`t_user` set `name`='mary' where `name` like '%jack%'");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    // [BUG]: Two undo SQL should not be generated !!!
    @Test
    public void testSQLImageEvaluateForUpdateSelectSQL() throws Exception {
        JdbcTemplate jdbcTemplate = initTestingDatabase();
        try {
            SQLImageEvaluator evaluator = SQLImageEvaluatorFactory.getEvaluator(new EvaluatorProperties(), jdbcTemplate);
            // Execution
            evaluator.evaluate(
                    "update `test_db`.`t_user` set `name`=(select `name` from `test_db`.`t_user` where id = 100) where `name` like '%jack%'");
            System.out.println("------------------- Generated all undo SQLs --------------------------");
            safeList(evaluator.getAllUndoSQLs()).forEach(s -> System.out.println(s));
            System.out.println("----------------------------------------------------------------------");
        } finally {
            ((HikariDataSource) jdbcTemplate.getDataSource()).close();
        }
    }

    private JdbcTemplate initTestingDatabase() {
        final HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setJdbcUrl("jdbc:h2:/tmp/h2test;FORBID_CREATION=FALSE");
        // ds.setUsername("");
        // ds.setPassword("");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

        // Create database.
        jdbcTemplate.execute("DROP SCHEMA IF EXISTS `test_db` CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA `test_db`");

        // Create tables.
        jdbcTemplate.execute("DROP TABLE IF EXISTS `test_db`.`t_user`");
        jdbcTemplate.execute("DROP TABLE IF EXISTS `test_db`.`t_user2`");

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS `test_db`.`t_user`(`id` bigint(25) PRIMARY KEY NOT NULL,`name` varchar(32) NOT NULL)");
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS `test_db`.`t_user2`(`id` bigint(25) PRIMARY KEY NOT NULL,`name` varchar(32) NOT NULL)");

        // Initial tables data.
        jdbcTemplate.execute("INSERT INTO `test_db`.`t_user` (`id`,`name`) VALUES (100, 'jack100')");
        jdbcTemplate.execute("INSERT INTO `test_db`.`t_user` (`id`,`name`) VALUES (110, 'jack110')");
        jdbcTemplate.execute("INSERT INTO `test_db`.`t_user` (`id`,`name`) VALUES (200, 'tom200')");

        // Print all records.
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from `test_db`.`t_user`");
        System.out.println("---------------- Print all data (`test_db`.`t_user`) -----------------");
        safeList(list).forEach(r -> System.out.println(r));
        System.out.println("----------------------------------------------------------------------");

        return jdbcTemplate;
    }

}
