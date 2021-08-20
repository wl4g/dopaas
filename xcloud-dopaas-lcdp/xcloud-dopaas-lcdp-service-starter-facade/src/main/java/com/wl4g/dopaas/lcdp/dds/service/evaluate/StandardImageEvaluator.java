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
package com.wl4g.dopaas.lcdp.dds.service.evaluate;

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static com.wl4g.component.common.lang.Assert2.notEmpty;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.component.common.collection.CollectionUtils2;
import com.wl4g.component.common.lang.StringUtils2;
import com.wl4g.dopaas.lcdp.dds.service.evaluate.metadata.MetadataResolver;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

/**
 * {@link StandardImageEvaluator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-15 v1.0.0
 * @since v1.0.0
 */
public class StandardImageEvaluator extends AbstractImageEvaluator {

    public StandardImageEvaluator(EvaluatorSpec config, JdbcTemplate jdbcTemplate, MetadataResolver resolver) {
        super(config, jdbcTemplate, resolver);
    }

    @Override
    public void evaluate(String sql) throws Exception {
        Statement stmt = CCJSqlParserUtil.parse(sql);
        if (stmt instanceof Insert) {
            Insert insert = (Insert) stmt;
            log.info("Original insert SQL: {}", insert);
            processInsertSQL(insert);
        } else if (stmt instanceof Delete) {
            Delete delete = (Delete) stmt;
            log.info("Original delete SQL: {}", delete);
            processDeleteSQL(delete);
        } else if (stmt instanceof Update) {
            Update update = (Update) stmt;
            log.info("Original update SQL: {}", update);
            processUpdateSQL(update);
        }
    }

    /**
     * Processing for insert SQL.
     * 
     * @param insert
     */
    protected void processInsertSQL(Insert insert) {
        // Deleted due of insertion.
        setUndoDeleteSqls(generateUndoDeleteSql(insert));
    }

    /**
     * Processing for delete SQL.
     * 
     * @param delete
     */
    protected void processDeleteSQL(Delete delete) {
        // Notice: for example, [delete from tab1 where id>=100]
        // Only need to treat the conditions after delete where as a whole.
        // When generating undo insert SQL, only need the overall result
        // sets.

        StringBuilder undoSelectSql = new StringBuilder("SELECT * FROM ");
        undoSelectSql.append(delete.getTable());
        undoSelectSql.append(" ");
        for (Join join : safeList(delete.getJoins())) {
            undoSelectSql.append(join);
        }

        Expression where = delete.getWhere(); // EqualsTo/GreaterThan/GreaterThanEquals/MinorThan/MinorThanEquals/InExpression/LikeExpression/...
        if (nonNull(where) && !isBlank(where.toString())) {
            undoSelectSql.append(" WHERE ");
            undoSelectSql.append(where);
        }

        if (nonNull(delete.getLimit())) {
            undoSelectSql.append(" ");
            undoSelectSql.append(delete.getLimit());
        }

        log.info("Generated undo select SQL: {}", undoSelectSql);

        // Insert due to deletion.
        setUndoInsertSqls(generateUndoInsertSql(delete, findOperationRecords(undoSelectSql.toString())));
    }

    /**
     * Processing for update SQL.
     * 
     * @param update
     */
    protected void processUpdateSQL(Update update) {
        // No columns were modified.
        if (isNull(update.getColumns())) {
            return;
        }

        StringBuilder undoSelectSql = new StringBuilder("SELECT ");
        // Update set fields.
        for (int i = 0, size = update.getColumns().size(); i < size; i++) {
            Column col = update.getColumns().get(i);
            undoSelectSql.append(col.getColumnName());
            if (i < (size - 1)) {
                undoSelectSql.append(",");
            }
        }
        // Update table primary key fields.
        String tableName = update.getTable().toString();
        List<String> primaryKeys = getTablePrimaryKeys(tableName);
        notEmpty(primaryKeys, IllegalStateException.class, "Unable get primaryKeys for tableName: %s", tableName);
        for (String key : primaryKeys) {
            undoSelectSql.append(",");
            undoSelectSql.append(getColumnSymbol());
            undoSelectSql.append(key);
            undoSelectSql.append(getColumnSymbol());
        }
        undoSelectSql.append(" FROM ");
        undoSelectSql.append(update.getTable());

        undoSelectSql.append(" ");
        for (Join join : safeList(update.getJoins())) {
            undoSelectSql.append(join);
        }

        Expression where = update.getWhere(); // EqualsTo/GreaterThan/GreaterThanEquals/MinorThan/MinorThanEquals/InExpression/LikeExpression/...
        if (nonNull(where) && !isBlank(where.toString())) {
            undoSelectSql.append(" WHERE ");
            undoSelectSql.append(where);
        }

        if (nonNull(update.getLimit())) {
            undoSelectSql.append(" ");
            undoSelectSql.append(update.getLimit());
        }

        log.info("Generated undo select SQL: {}", undoSelectSql);

        // Update due to updation.
        setUndoUpdateSqls(generateUndoUpdateSql(update, primaryKeys, findOperationRecords(undoSelectSql.toString())));
    }

    /**
     * Generate undo delete SQL.
     * 
     * @param insert
     * @return
     */
    protected List<String> generateUndoDeleteSql(Insert insert) {
        List<String> undoDeleteSqls = new ArrayList<>(1);

        // e.g: insert into tab1 (id,name) values (1,'jack')
        if (insert.getItemsList() instanceof ExpressionList) {
            ExpressionList items = (ExpressionList) insert.getItemsList();
            undoDeleteSqls.addAll(doGenerateUndoDeleteSqlForItemList(insert, items));
        }
        // e.g: insert into tab1 (id,name) values (1,'jack'), (2, 'jack2')
        else if (insert.getItemsList() instanceof MultiExpressionList) {
            MultiExpressionList itemsList = (MultiExpressionList) insert.getItemsList();
            safeList(itemsList.getExpressionLists())
                    .forEach(items -> undoDeleteSqls.addAll(doGenerateUndoDeleteSqlForItemList(insert, items)));
        }
        // e.g:
        else if (insert.getItemsList() instanceof SubSelect) {
            // SubSelect subSelect = (SubSelect) insert.getItemsList();
            throw new UnsupportedOperationException(format("No supported insert select SQL. - %s", insert.toString()));
        }
        // e.g: insert into tab1 select ...
        else if (nonNull(insert.getSelect())) {
            Select select = insert.getSelect();
            List<OperationRecord> records = findOperationRecords(select.toString());
            if (CollectionUtils2.isEmpty(records)) {
                return null;
            }
            // Each insert-select result.
            for (OperationRecord record : records) {
                StringBuilder deleteSql = new StringBuilder("DELETE FROM ");
                deleteSql.append(insert.getTable());
                deleteSql.append(" ");
                if (!record.isEmpty()) {
                    deleteSql.append("WHERE ");
                }

                // e.g: insert into tab1 select * from (select id,name
                // from tab2 a inner join tab3 b on a.bid=b.id where id>1) as
                // tab
                if (CollectionUtils2.isEmpty(insert.getColumns())) {
                    Iterator<Entry<String, Object>> it = record.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, Object> ent = it.next();
                        deleteSql.append(ent.getKey());
                        deleteSql.append("=");
                        boolean mark = needQuotationMark(ent.getValue());
                        if (mark) {
                            deleteSql.append(getStringValueSymbol());
                        }
                        deleteSql.append(ent.getValue());
                        if (mark) {
                            deleteSql.append(getStringValueSymbol());
                        }
                        if (it.hasNext()) {
                            deleteSql.append(" AND ");
                        }
                    }
                    undoDeleteSqls.add(deleteSql.toString());
                }
                // e.g: insert into tab1 (id,name) select * from (select id,name
                // from tab2 a inner join tab3 b on a.bid=b.id where id>1) as
                // tab
                else {
                    int columnCount = insert.getColumns().size();
                    if (columnCount != record.size()) { // MARK1
                        throw new IllegalStateException(
                                format("Insert select SQL: %s, column count: %s, select record column count: %s", insert,
                                        columnCount, record.size()));
                    }
                    Iterator<Entry<String, Object>> it = record.entrySet().iterator();
                    for (int i = 0, size = insert.getColumns().size(); i < size; i++) {
                        Object value = null;
                        if (it.hasNext()) {
                            value = it.next().getValue();
                        } else {
                            throw new Error("Shouldn't be here."); // @see:MARK1
                        }
                        Column col = insert.getColumns().get(i);
                        deleteSql.append(col.getColumnName());
                        deleteSql.append("=");
                        boolean mark = needQuotationMark(value);
                        if (mark) {
                            deleteSql.append(getStringValueSymbol());
                        }
                        deleteSql.append(value);
                        if (mark) {
                            deleteSql.append(getStringValueSymbol());
                        }
                        if (i < (size - 1)) {
                            deleteSql.append(" AND ");
                        }
                    }
                    undoDeleteSqls.add(deleteSql.toString());
                }
            }
        }

        return undoDeleteSqls;
    }

    /**
     * Generate undo delete SQL.
     * 
     * @param insert
     * @param items
     * @return
     */
    protected List<String> doGenerateUndoDeleteSqlForItemList(Insert insert, ExpressionList items) {
        List<String> undoDeleteSqls = new ArrayList<>(items.getExpressions().size());

        StringBuilder deleteSql = new StringBuilder("DELETE FROM ");
        deleteSql.append(insert.getTable());
        deleteSql.append(" ");

        List<Column> columns = safeList(insert.getColumns());
        List<Expression> exprs = safeList(items.getExpressions());
        if (columns.size() != exprs.size()) {
            throw new IllegalStateException(
                    format("Insert SQL: %s, columns: %s, values: %s", insert, columns.size(), exprs.size()));
        }
        if (!exprs.isEmpty()) {
            deleteSql.append("WHERE ");
        }
        for (int i = 0, size = exprs.size(); i < size; i++) {
            Column col = columns.get(i);
            Expression value = exprs.get(i);
            deleteSql.append(col.getColumnName());
            deleteSql.append("=");
            boolean mark = needQuotationMark(value);
            if (mark) {
                deleteSql.append(getStringValueSymbol());
            }
            deleteSql.append(value);
            if (mark) {
                deleteSql.append(getStringValueSymbol());
            }
            if (i < (size - 1)) {
                deleteSql.append(" AND ");
            }
        }
        undoDeleteSqls.add(deleteSql.toString());

        return undoDeleteSqls;
    }

    /**
     * Generate undo insert SQL.
     * 
     * @param delete
     * @param records
     *            The result of executing delete SQL, affected row records.
     * @return
     */
    protected List<String> generateUndoInsertSql(Delete delete, List<OperationRecord> records) {
        if (CollectionUtils2.isEmpty(records)) {
            return null;
        }
        List<String> undoInsertSqls = new ArrayList<>(records.size());

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
            // Build insert SQL values.
            insertSql.append(") VALUES (");
            for (int j = 0, size = values.size(); j < size; j++) {
                Object value = values.get(j);
                boolean mark = needQuotationMark(value);
                if (mark) {
                    insertSql.append(getStringValueSymbol());
                }
                insertSql.append(value);
                if (mark) {
                    insertSql.append(getStringValueSymbol());
                }
                if (j < (size - 1)) {
                    insertSql.append(",");
                }
            }
            insertSql.append(")");

            // Add insert SQL.
            undoInsertSqls.add(insertSql.toString());
        }

        return undoInsertSqls;
    }

    /**
     * Testing for
     * {@link SQLImageEvaluatorFactoryTests#testSQLImageEvaluateForUpdateSelectSQL}
     * 
     * @param update
     * @param primaryKeys
     * @param records
     *            The result of executing update SQL, affected row records.
     * @return
     */
    protected List<String> generateUndoUpdateSql(Update update, List<String> primaryKeys, List<OperationRecord> records) {
        if (records.isEmpty()) {
            return null;
        }

        List<String> undoUpdateSqls = new ArrayList<>(records.size());
        for (OperationRecord record : records) {

            // Conditions for generating undo update SQL. (The update affected
            // row primary keys to generate undo SQL condition.)
            StringBuilder undoUpdateSqlWhere = new StringBuilder();
            for (int i = 0, size = primaryKeys.size(); i < size; i++) {
                String key = primaryKeys.get(i);
                Object value = record.get(valueOf(key).toUpperCase());
                value = isNull(value) ? record.get(key) : value;

                undoUpdateSqlWhere.append(key);
                undoUpdateSqlWhere.append("=");
                boolean mark = needQuotationMark(value);
                if (mark) {
                    undoUpdateSqlWhere.append(getStringValueSymbol());
                }
                undoUpdateSqlWhere.append(value);
                if (mark) {
                    undoUpdateSqlWhere.append(getStringValueSymbol());
                }
                if (i < (size - 1)) {
                    undoUpdateSqlWhere.append(" AND");
                }
            }

            // Generate update SQL set fields.
            StringBuilder updateSql = new StringBuilder(getUpdateKeyword());
            updateSql.append(" ");
            updateSql.append(update.getTable().toString());
            updateSql.append(" SET ");

            Iterator<Entry<String, Object>> it = record.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Object> ent = it.next();
                String columnName = ent.getKey();
                Object value = ent.getValue();

                // e.g: [update set `name`='jack'] The record column NAME
                // mapping to origin column `name`
                Optional<Column> origColumnOp = safeList(update.getColumns()).stream().filter(
                        c -> StringUtils2.eqIgnCase(StringUtils2.replace(c.getColumnName(), getColumnSymbol(), ""), columnName))
                        .findFirst();
                if (origColumnOp.isPresent()) {
                    updateSql.append(origColumnOp.get());
                    updateSql.append("=");
                    boolean mark = needQuotationMark(value);
                    if (mark) {
                        updateSql.append(getStringValueSymbol());
                    }
                    updateSql.append(value);
                    if (mark) {
                        updateSql.append(getStringValueSymbol());
                    }
                    updateSql.append(",");
                }
            }
            updateSql.delete(updateSql.length() - 1, updateSql.length());

            if (nonNull(update.getWhere())) {
                updateSql.append(" WHERE ");
                updateSql.append(undoUpdateSqlWhere);
            }

            // Add update SQL.
            undoUpdateSqls.add(updateSql.toString());
        }
        return undoUpdateSqls;
    }

}
