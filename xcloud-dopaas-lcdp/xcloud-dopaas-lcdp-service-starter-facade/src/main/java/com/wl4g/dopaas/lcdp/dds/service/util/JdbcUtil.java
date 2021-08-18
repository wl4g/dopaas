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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.wl4g.component.common.collection.UniqueList;

/**
 * {@link JdbcUtil}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-18 v1.0.0
 * @since v1.0.0
 */
public abstract class JdbcUtil {

    public static List<String> getTablePrimaryKeys(DataSource dataSource, String tableName) throws SQLException {
        try (Connection conn = dataSource.getConnection();) {
            List<String> keys = new UniqueList<>(new ArrayList<>(2), false);
            ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                keys.add(rs.getString("COLUMN_NAME"));
            }
            return keys;
        }
    }

}
