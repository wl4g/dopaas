package com.wl4g.devops.dts.codegen.utils;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.DbType;

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
	ID(true, "id", new HashMap<DbType, List<String>>() {
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

}
