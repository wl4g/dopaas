/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.dts.codegen.engine.resolver.db;

import javax.annotation.Nullable;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ForeignMetadata;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import static com.wl4g.component.common.collection.Collections2.safeList;
import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notEmpty;
import static com.wl4g.component.common.lang.StringUtils2.eqIgnCase;
import static com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver.ResolverAlias.MYSQLV5;
import static java.util.stream.Collectors.toList;

import java.util.Date;

/**
 * {@link MySQLV5MetadataResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-08
 * @sine v1.0.0
 * @see
 */
public class MySQLV5MetadataResolver extends AbstractDbMetadataResolver {

	/**
	 * New {@link MySQLV5MetadataResolver}
	 * 
	 * @param genDS
	 */
	public MySQLV5MetadataResolver(GenDataSource genDS) {
		this("jdbc:mysql://".concat(hasTextOf(genDS.getHost(), "dbHost")).concat(":").concat(hasTextOf(genDS.getPort(), "dbPort"))
				.concat("/").concat(hasTextOf(genDS.getDatabase(), "dbName")), genDS.getUsername(), genDS.getPassword());
	}

	/**
	 * New {@link MySQLV5MetadataResolver}
	 * 
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 */
	protected MySQLV5MetadataResolver(String url, String username, String password) {
		super("com.mysql.jdbc.Driver", url, username, password);
	}

	@Override
	public List<TableMetadata> findTables(@Nullable String search) {
		String sql = loadResolvingSql(MYSQLV5, SQL_TABLES, search);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return safeList(list).stream().map(row -> {
			TableMetadata table = new TableMetadata();
			table.setTableSchema((String) row.get("tableSchema"));
			table.setTableName((String) row.get("tableName"));
			table.setEngine((String) row.get("engine"));
			table.setComments((String) row.get("tableComment"));
			table.setCreateTime((Date) row.get("createTime"));
			return table;
		}).collect(toList());
	}

	@Override
	public TableMetadata findTableDescribe(@NotBlank String tableName) {
		hasTextOf(tableName, "tableName");

		String sql = loadResolvingSql(MYSQLV5, SQL_TABLE_DESCRIBE, tableName);
		Map<String, Object> row = jdbcTemplate.queryForMap(sql);

		TableMetadata table = new TableMetadata();
		table.setTableName((String) row.get("tableName"));
		table.setComments((String) row.get("tableComment"));
		return table;
	}

	@Override
	public List<ColumnMetadata> findTableColumns(@NotBlank String tableName) {
		hasTextOf(tableName, "tableName");

		String sql = loadResolvingSql(MYSQLV5, SQL_COLUMNS, tableName);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return safeList(list).stream().map(row -> {
			ColumnMetadata column = new ColumnMetadata();
			column.setColumnName((String) row.get("columnName"));
			column.setColumnType((String) row.get("columnType"));
			column.setSimpleColumnType((String) row.get("dataType"));
			column.setComments((String) row.get("columnComment"));
			column.setPk(eqIgnCase((String) row.get("columnKey"), "PRI"));
			column.setNullable(eqIgnCase((String) row.get("isNullable"), "YES"));
			column.setExtra((String) row.get("extra"));
			return column;
		}).collect(toList());
	}

	@Override
	public List<ForeignMetadata> findTableForeign(@NotBlank String tableName) {
		hasTextOf(tableName, "tableName");

		String sql = loadResolvingSql(MYSQLV5, SQL_FOREIGN, tableName);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return safeList(list).stream().map(row -> {
			ForeignMetadata foreign = new ForeignMetadata();
			foreign.setDbName((String) row.get("dbName"));
			foreign.setForTableName((String) row.get("forTableName"));
			foreign.setRefTableName((String) row.get("refTableName"));
			foreign.setForColumnName((String) row.get("forColumnName"));
			foreign.setRefColumnName((String) row.get("refColumnName"));
			return foreign;
		}).collect(toList());
	}

	@Override
	public String findDBVersion() throws Exception {
		String sql = loadResolvingSql(MYSQLV5, SQL_VERSION);
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		notEmpty(result, "Cannot find database version info");
		return (String) result.get("version");
	}

}