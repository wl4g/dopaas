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
package com.wl4g.dopaas.common.utils;

import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static org.apache.commons.lang3.StringUtils.split;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.component.common.collection.UniqueList;

/**
 * {@link JdbcUtil}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-18 v1.0.0
 * @since v1.0.0
 */
public abstract class JdbcUtil {

    public static List<String> getTablePrimaryKeys(@NotNull DataSource dataSource, @NotBlank String tableName)
            throws SQLException {
        notNullOf(dataSource, "dataSource");
        hasTextOf(tableName, "tableName");
        // Transform table name to schema.
        String schema = null, simpleTableName = tableName;
        if (tableName.contains(".")) {
            String[] parts = split(tableName, ".");
            if (parts.length >= 2) {
                schema = parts[0];
                simpleTableName = parts[1];
            }
        }
        try (Connection conn = dataSource.getConnection();) {
            List<String> keys = new UniqueList<>(new ArrayList<>(2), false);
            // e.g: `test_db`.`t_user` => TEST_DB.T_USER
            addPrimaryKeys(conn, keys, schema.replaceAll("`", "").toUpperCase(),
                    simpleTableName.replaceAll("`", "").toUpperCase());
            if (keys.isEmpty()) {
                // e.g: `test_db`.`t_user` => test_db.t_user
                addPrimaryKeys(conn, keys, schema.replaceAll("`", ""), simpleTableName.replaceAll("`", ""));
            }
            if (keys.isEmpty()) {
                // e.g: `test_db`.`t_user`
                addPrimaryKeys(conn, keys, schema, simpleTableName);
            }
            return keys;
        }
    }

    private static void addPrimaryKeys(Connection conn, List<String> keys, String schema, String simpleTableName)
            throws SQLException {
        ResultSet rs = conn.getMetaData().getPrimaryKeys(null, schema, simpleTableName);
        while (rs.next()) {
            keys.add(rs.getString("COLUMN_NAME"));
        }
    }

}
