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
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.component.common.lang.StringUtils2;
import com.wl4g.component.common.log.SmartLogger;

import lombok.Getter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

/**
 * {@link AbstractSQLImageHandler}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */

public abstract class AbstractSQLImageHandler implements SQLImageHandler {
    protected final SmartLogger log = getLogger(getClass());

    protected final JdbcTemplate jdbcTemplate;

    @Getter
    private List<String> undoDeleteSqls; // due insert SQL.
    @Getter
    private List<String> undoInsertSqls; // due delete SQL.
    @Getter
    private List<String> undoUpdateSqls; // due update SQL.

    public AbstractSQLImageHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = notNullOf(jdbcTemplate, "jdbcTemplate");
    }

    protected List<OperationRecord> findOperationRecords(String selectSQL) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(selectSQL);
        return safeList(result).stream().map(r -> new OperationRecord(r)).collect(toList());
    }

    protected void generateUndoDeleteSql(Insert insert) {
        if (insert.getItemsList() instanceof ExpressionList) {
            ExpressionList itemsList = (ExpressionList) insert.getItemsList();
            this.undoDeleteSqls = new ArrayList<>(1);
            StringBuilder deleteSql = new StringBuilder("DELETE FROM ");

            deleteSql.append(insert.getTable());
            deleteSql.append(" ");

            List<Column> columns = safeList(insert.getColumns());
            List<Expression> exprs = safeList(itemsList.getExpressions());
            if (columns.size() != exprs.size()) {
                throw new IllegalStateException(
                        format("Insert SQL: %s, columns: %s, values: %s", insert, columns.size(), exprs.size()));
            }
            if (!exprs.isEmpty()) {
                deleteSql.append(" WHERE ");
            }
            for (int i = 0, size = exprs.size(); i < size; i++) {
                Column col = columns.get(i);
                Expression value = exprs.get(i);
                deleteSql.append(col.getColumnName());
                deleteSql.append("=");
                boolean mark = isNeedQuotationMark(value);
                if (mark) {
                    deleteSql.append("'");
                }
                deleteSql.append(value);
                if (mark) {
                    deleteSql.append("'");
                }
                if (i < (size - 1)) {
                    deleteSql.append(" AND ");
                }
            }
            this.undoDeleteSqls.add(deleteSql.toString());
        } else if (insert.getItemsList() instanceof MultiExpressionList) {
            // TODO
        } else if (insert.getItemsList() instanceof SubSelect) {
            // TODO
        }

    }

    protected void generateUndoInsertSql(Delete delete, List<OperationRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        this.undoInsertSqls = new ArrayList<>(records.size());
        for (OperationRecord record : records) {
            StringBuilder insertSql = new StringBuilder(getInsertKeyword());
            insertSql.append(" ");
            insertSql.append(delete.getTable().toString());
            insertSql.append(" INTO (");

            List<Object> values = new ArrayList<>(record.size());
            // Build insert sql columns.
            Iterator<Entry<String, Object>> it = record.entrySet().iterator();
            for (;;) {
                Entry<String, Object> ent = it.next();
                insertSql.append(ent.getKey());
                values.add(ent.getValue()); // Column name.
                if (it.hasNext()) {
                    insertSql.append(",");
                } else {
                    break;
                }
            }
            // Build insert sql values.
            insertSql.append(") VALUES (");
            for (int j = 0, size = values.size(); j < size; j++) {
                Object value = values.get(j);
                boolean mark = isNeedQuotationMark(value);
                if (mark) {
                    insertSql.append("'");
                }
                insertSql.append(value);
                if (mark) {
                    insertSql.append("'");
                }
                if (j < (size - 1)) {
                    insertSql.append(",");
                }
            }
            insertSql.append(")");

            // Add insert SQL.
            this.undoInsertSqls.add(insertSql.toString());
        }
    }

    protected void generateUndoUpdateSql(Update update, List<OperationRecord> records) {
        if (records.isEmpty()) {
            return;
        }

        // List<Column> columns = safeList(update.getColumns());
        // List<Expression> values = safeList(update.getExpressions());
        // if (columns.size() != values.size()) {
        // throw new IllegalStateException(
        // format("Update SQL: %s, columns: %s, values: %s", update,
        // columns.size(), values.size()));
        // }
        // Map<String, Object> updateSetValues = new HashMap<>();
        // for (int i = 0; i < columns.size(); i++) {
        // updateSetValues.put(columns.get(i).getColumnName(), values.get(i));
        // }

        this.undoUpdateSqls = new ArrayList<>(records.size());
        for (OperationRecord record : records) {
            StringBuilder updateSql = new StringBuilder(getUpdateKeyword());
            updateSql.append(" ");
            updateSql.append(update.getTable().toString());
            updateSql.append(" SET ");

            Iterator<Entry<String, Object>> it = record.entrySet().iterator();
            for (;;) {
                Entry<String, Object> ent = it.next();
                String columnName = ent.getKey();
                Object value = ent.getValue();

                // Use origin columnName. e.g: update set `name`='jack'
                // NAME => `name`
                Column origColumnName = safeList(update.getColumns()).stream()
                        .filter(c -> StringUtils2.eqIgnCase(StringUtils2.replace(c.getColumnName(), "`", ""), columnName))
                        .findFirst().orElse(new Column(columnName));

                updateSql.append(origColumnName);
                updateSql.append("=");
                boolean mark = isNeedQuotationMark(value);
                if (mark) {
                    updateSql.append("'");
                }
                updateSql.append(value);
                if (mark) {
                    updateSql.append("'");
                }
                if (it.hasNext()) {
                    updateSql.append(",");
                } else {
                    break;
                }
            }
            if (nonNull(update.getWhere())) {
                updateSql.append(" WHERE ");
                updateSql.append(update.getWhere());
            }

            // Add update SQL.
            this.undoUpdateSqls.add(updateSql.toString());
        }
    }

    protected String getInsertKeyword() {
        return "INSERT";
    }

    protected String getUpdateKeyword() {
        return "UPDATE";
    }

    protected boolean isNeedQuotationMark(Object value) {
        return value instanceof String || value instanceof Date || value instanceof java.sql.Date;
    }

    @Override
    public List<String> getAllUndoSQLs() {
        // Each handling will only be one of them.
        return nonNull(undoDeleteSqls) ? undoDeleteSqls : (nonNull(undoInsertSqls) ? undoInsertSqls : undoUpdateSqls);
    }

}
