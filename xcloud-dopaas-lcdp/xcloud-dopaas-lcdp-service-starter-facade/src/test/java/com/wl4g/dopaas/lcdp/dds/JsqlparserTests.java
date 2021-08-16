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
package com.wl4g.dopaas.lcdp.dds;

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;

import java.util.List;

import org.junit.Test;

import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.SelectUtils;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * {@link JsqlparserTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-14 v1.0.0
 * @since v1.0.0
 * @see https://github.com/JSQLParser/JSqlParser/wiki
 */
public class JsqlparserTests {

    @Test
    public void testParserInsertSelectSQL() throws Exception {
        Statement stmt = CCJSqlParserUtil.parse(
                "insert into tab1 (id,name) select * from (select a.id,a.name from tab2 a inner join tab3 b on a.bid=b.id where a.id>1) as tab");
        System.out.println(stmt);
    }

    @Test
    public void testParserDeleteSelectSQL() throws Exception {
        Statement stmt = CCJSqlParserUtil
                .parse("delete from tab1 where id=(select a.id from tab2 a inner join tab3 b on a.bid=b.id where id>1)");
        System.out.println(stmt);
    }

    @Test
    public void testExtractTableNamesFromSQL() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT * FROM tab1");
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(select);
        System.out.println(tableList);
    }

    @Test
    public void testAddColumnOrExpressionToASelect() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT a FROM tab1");
        SelectUtils.addExpression(select, new Column("b"));
        System.out.println(select);
    }

    @Test
    public void testReplaceWhereColumns() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT * FROM tab1 where _id = 100 or _name like '%jack%'");
        System.out.println("before " + select.toString());

        ((PlainSelect) select.getSelectBody()).getWhere().accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Column column) {
                column.setColumnName(column.getColumnName().replace("_", ""));
            }
        });

        System.out.println("after " + select.toString());
    }

    @Test
    public void testExtractWhereColumns() throws Exception {
        Select select = (Select) CCJSqlParserUtil.parse("SELECT * FROM tab1 where _id = 100 or _name like '%jack%'");
        System.out.println(((PlainSelect) select.getSelectBody()).getWhere());
    }

    @Test
    public void testGenerateSelectFormDelete() throws Exception {
        String delete1 = "delete from tab1 a left join tab2 b on a.bid=b.id where b.id >= 100 and b.id <= 1000 limit 100";
        Statement stmt1 = CCJSqlParserUtil.parse(delete1);

        // String delete2 = "delete from tab1 a, tab2 b where a.bid=b.id and
        // b.id >= 100 and b.id <= 1000 limit 100";
        // Statement stmt2 = CCJSqlParserUtil.parse(delete2);

        Statement stmt = stmt1;
        if (stmt instanceof Delete) {
            Delete delete = (Delete) stmt;
            System.out.println("------- start debugging ------");
            System.out.println("Table: " + delete.getTable());
            System.out.println("Tables: " + delete.getTables());
            System.out.println("Joins: " + delete.getJoins());
            System.out.println("Where: " + delete.getWhere());
            System.out.println("Limit: " + delete.getLimit());
            System.out.println("------- end debugging ------");

            StringBuilder generateSelectSQL = new StringBuilder("SELECT * FROM ");
            generateSelectSQL.append(delete.getTable());
            generateSelectSQL.append(" ");
            for (Join join : safeList(delete.getJoins())) {
                generateSelectSQL.append(join);
            }
            generateSelectSQL.append(" WHERE ");
            generateSelectSQL.append(delete.getWhere());
            generateSelectSQL.append(" ");
            generateSelectSQL.append(delete.getLimit());

            System.out.println("\n-----------------------\n");
            System.out.println("Original delete SQL: " + delete);
            System.out.println("Generate select SQL: " + generateSelectSQL);

            // Check-syntax
            try {
                CCJSqlParserUtil.parse(generateSelectSQL.toString());
                System.out.println("Generated select SQL valid.");
            } catch (Exception e) {
                System.out.println("Generated select SQL syntax error.");
                e.printStackTrace();
                throw e;
            }
        }
    }

}
