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
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.jdbc.core.JdbcTemplate;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.update.Update;

/**
 * {@link StandardImageEvaluator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
public class StandardImageEvaluator extends AbstractImageEvaluator {

    public StandardImageEvaluator(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void evaluate(String sql) throws Exception {
        Statement stmt = CCJSqlParserUtil.parse(sql);
        if (stmt instanceof Insert) {
            Insert insert = (Insert) stmt;
            log.info("Original insert SQL: {}", insert);

            // Deleted due of insertion.
            generateUndoDeleteSql(insert);

        } else if (stmt instanceof Delete) {
            Delete delete = (Delete) stmt;
            log.info("Original delete SQL: {}", delete);

            StringBuilder undoSelectSql = new StringBuilder("SELECT * FROM ");
            undoSelectSql.append(delete.getTable());
            undoSelectSql.append(" ");
            for (Join join : safeList(delete.getJoins())) {
                undoSelectSql.append(join);
            }
            Expression where = delete.getWhere();
            if (nonNull(where) && !isBlank(where.toString())) {
                undoSelectSql.append(" WHERE ");
                undoSelectSql.append(where);
            }
            if (nonNull(delete.getLimit())) {
                undoSelectSql.append(" ");
                undoSelectSql.append(delete.getLimit());
            }

            // Insert due to deletion.
            log.info("Generated undo select SQL: {}", undoSelectSql);
            generateUndoInsertSql(delete, findOperationRecords(undoSelectSql.toString()));

        } else if (stmt instanceof Update) {
            Update update = (Update) stmt;
            log.info("Original update SQL: {}", update);

            // No columns were modified.
            if (isNull(update.getColumns())) {
                return;
            }

            StringBuilder undoSelectSql = new StringBuilder("SELECT ");
            for (int i = 0, size = update.getColumns().size(); i < size; i++) {
                Column col = update.getColumns().get(i);
                undoSelectSql.append(col.getColumnName());
                if (i < (size - 1)) {
                    undoSelectSql.append(",");
                }
            }
            undoSelectSql.append(" FROM ");
            undoSelectSql.append(update.getTable());

            undoSelectSql.append(" ");
            for (Join join : safeList(update.getJoins())) {
                undoSelectSql.append(join);
            }
            Expression where = update.getWhere();
            if (nonNull(where) && !isBlank(where.toString())) {
                undoSelectSql.append(" WHERE ");
                undoSelectSql.append(where);
            }
            if (nonNull(update.getLimit())) {
                undoSelectSql.append(" ");
                undoSelectSql.append(update.getLimit());
            }

            // Update due to updation.
            log.info("Generated undo select SQL: {}", undoSelectSql);
            generateUndoUpdateSql(update, findOperationRecords(undoSelectSql.toString()));
        }
    }

}
