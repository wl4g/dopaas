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
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;

import com.wl4g.component.common.log.SmartLogger;

import lombok.Getter;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;

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
    private List<String> undoDeleteSqls; // because insert SQL.
    @Getter
    private List<String> undoInsertSqls; // because delete SQL.
    @Getter
    private List<String> undoUpdateSqls; // because update SQL.

    public AbstractSQLImageHandler(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = notNullOf(jdbcTemplate, "jdbcTemplate");
    }

    protected List<OperationRecord> findOperationRecords(String selectSQL) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(selectSQL);
        return safeList(result).stream().map(r -> new OperationRecord(r)).collect(toList());
    }

    protected void generateUndoDeleteSql(Insert insert) {
        StringBuilder deleteSql = new StringBuilder();
        // TODO
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
            // Build columns.
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
            // Build values.
            insertSql.append(") VALUES (");
            for (int j = 0, size = values.size(); j < size; j++) {
                Object value = values.get(j);
                boolean needMark = isNeedQuotationMark(value);
                if (needMark) {
                    insertSql.append("'");
                }
                insertSql.append(value);
                if (needMark) {
                    insertSql.append("'");
                }
                if (j < (size - 1)) {
                    insertSql.append(",");
                }
            }
            insertSql.append(")");

            // Add insertSQL.
            this.undoInsertSqls.add(insertSql.toString());
        }
    }

    protected void generateUndoUpdateSql(List<OperationRecord> records) {
        StringBuilder updateSql = new StringBuilder();
        // TODO
    }

    protected String getInsertKeyword() {
        return "INSERT";
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
