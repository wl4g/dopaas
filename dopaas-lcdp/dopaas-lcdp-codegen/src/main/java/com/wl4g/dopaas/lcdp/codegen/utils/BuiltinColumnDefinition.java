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
package com.wl4g.dopaas.lcdp.codegen.utils;

import static com.wl4g.component.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notEmptyOf;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wl4g.dopaas.lcdp.codegen.engine.converter.DbTypeConverter.DbType;

/**
 * Default builtin table columns definitions.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-10-05
 * @sine v1.0.0
 * @see
 */
public enum BuiltinColumnDefinition {

	@SuppressWarnings("serial")
	ID(false, "id", new HashMap<DbType, List<String>>() {
		{
			put(DbType.MySQLV5, asList("bigint"));
			put(DbType.OracleV11g, asList("number"));
			put(DbType.PostgreSQLV10, asList("bigint"));
		}
	}),

	@SuppressWarnings("serial")
	CREATE_BY(false, "create_by", new HashMap<DbType, List<String>>() {
		{
			put(DbType.MySQLV5, asList("bigint"));
			put(DbType.OracleV11g, asList("number"));
			put(DbType.PostgreSQLV10, asList("bigint"));
		}
	}),

	@SuppressWarnings("serial")
	CREATE_DATE(false, "create_date", new HashMap<DbType, List<String>>() {
		{
			put(DbType.MySQLV5, asList("datetime"));
			put(DbType.OracleV11g, asList("datetime"));
			put(DbType.PostgreSQLV10, asList("datetime"));
		}
	}),

	@SuppressWarnings("serial")
	UPDATE_BY(false, "update_by", new HashMap<DbType, List<String>>() {
		{
			put(DbType.MySQLV5, asList("bigint"));
			put(DbType.OracleV11g, asList("number"));
			put(DbType.PostgreSQLV10, asList("bigint"));
		}
	}),

	@SuppressWarnings("serial")
	UPDATE_DATE(false, "update_date", new HashMap<DbType, List<String>>() {
		{
			put(DbType.MySQLV5, asList("datetime"));
			put(DbType.OracleV11g, asList("datetime"));
			put(DbType.PostgreSQLV10, asList("datetime"));
		}
	}),

	@SuppressWarnings("serial")
	DEL_FLAG(false, "del_flag", new HashMap<DbType, List<String>>() {
		{
			put(DbType.MySQLV5, asList("int"));
			put(DbType.OracleV11g, asList("int"));
			put(DbType.PostgreSQLV10, asList("int"));
		}
	});

	private final boolean required;
	private final String columnName;
	// Candidates db column types.
	private final Map<DbType, List<String>> columnTypes;

	private BuiltinColumnDefinition(boolean required, String columnName, Map<DbType, List<String>> columnTypes) {
		this.required = required;
		this.columnName = hasTextOf(columnName, "columnName");
		this.columnTypes = notEmptyOf(columnTypes, "columnTypes");
	}

	public boolean isRequired() {
		return required;
	}

	public String getColumnName() {
		return columnName;
	}

	public Map<DbType, List<String>> getColumnTypes() {
		return columnTypes;
	}

	/**
	 * {@link #columnName} ALL.
	 */
	public static final List<String> COLUMN_NAME_VALUES = safeArrayToList(values()).stream().map(f -> f.getColumnName())
			.collect(toList());

}