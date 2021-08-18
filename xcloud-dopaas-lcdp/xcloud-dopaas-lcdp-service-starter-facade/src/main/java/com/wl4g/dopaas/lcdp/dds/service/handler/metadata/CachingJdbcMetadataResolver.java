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
package com.wl4g.dopaas.lcdp.dds.service.handler.metadata;

import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.List;

import javax.sql.DataSource;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wl4g.dopaas.lcdp.dds.service.util.JdbcUtil;

/**
 * {@link CachingJdbcMetadataResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-08-18 v1.0.0
 * @since v1.0.0
 */
public class CachingJdbcMetadataResolver implements MetadataResolver {

    private final Cache<String, List<String>> primaryKeysCaching;

    public CachingJdbcMetadataResolver() {
        this.primaryKeysCaching = CacheBuilder.newBuilder().initialCapacity(16).expireAfterAccess(30_000L, MILLISECONDS).build();
    }

    @Override
    public List<String> getTablePrimaryKeys(DataSource dataSource, String tableName) throws Exception {
        @Nullable
        List<String> keys = primaryKeysCaching.getIfPresent(tableName);
        if (isNull(keys)) {
            synchronized (this) {
                keys = primaryKeysCaching.getIfPresent(tableName);
                if (isNull(keys)) {
                    keys = JdbcUtil.getTablePrimaryKeys(dataSource, tableName);
                }
            }
        }
        return keys;
    }

}
