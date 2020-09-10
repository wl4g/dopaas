package com.wl4g.devops.dts.codegen.engine.resolver;

import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ForeignMetadata;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmpty;

/**
 * {@link MySQLV5xMetadataResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-08
 * @sine v1.0.0
 * @see
 */
public class MySQLV5xMetadataResolver extends AbstractMetadataResolver {

	/**
	 * New {@link MySQLV5xMetadataResolver}
	 * 
	 * @param genDS
	 */
	public MySQLV5xMetadataResolver(GenDataSource genDS) {
		this("jdbc:mysql://".concat(genDS.getHost()).concat(":").concat(genDS.getPort()).concat("/").concat(genDS.getDatabase()),
				genDS.getUsername(), genDS.getPassword());
	}

	/**
	 * New {@link MySQLV5xMetadataResolver}
	 * 
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 */
	protected MySQLV5xMetadataResolver(String url, String username, String password) {
		super("com.mysql.jdbc.Driver", url, username, password);
	}

	@Override
	public List<String> findTables() {
		String sql = loadResolvingSql(DB_TYPE, SQL_TABLES);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		return safeList(list).stream().map(row -> (String) row.get("tableName")).collect(toList());
	}

	@Override
	public TableMetadata findTableDescribe(String tableName) {
		hasTextOf(tableName, "tableName");

		String sql = loadResolvingSql(DB_TYPE, SQL_TABLE_DESCRIBE, tableName);
		Map<String, Object> row = jdbcTemplate.queryForMap(sql, tableName);

		TableMetadata table = new TableMetadata();
		table.setTableName((String) row.get("tableName"));
		table.setComments((String) row.get("tableComment"));
		return table;
	}

	@Override
	public List<ColumnMetadata> findTableColumns(String tableName) {
		hasTextOf(tableName, "tableName");

		String sql = loadResolvingSql(DB_TYPE, SQL_COLUMNS, tableName);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

		return safeList(list).stream().map(row -> {
			ColumnMetadata column = new ColumnMetadata();
			column.setColumnName((String) row.get("columnName"));
			column.setColumnType((String) row.get("columnType"));
			column.setDataType((String) row.get("dataType"));
			column.setComments((String) row.get("columnComment"));
			column.setColumnKey((String) row.get("columnKey"));
			column.setExtra((String) row.get("extra"));
			return column;
		}).collect(toList());
	}

	@Override
	public List<ForeignMetadata> findTableForeign(String tableName) {
		hasTextOf(tableName, "tableName");

		String sql = loadResolvingSql(DB_TYPE, SQL_FOREIGN, tableName);
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
		String sql = loadResolvingSql(DB_TYPE, SQL_VERSION);
		Map<String, Object> result = jdbcTemplate.queryForMap(sql);
		notEmpty(result, "Cannot find database version info");
		return (String) result.get("version");
	}

	public final static String DB_TYPE = "mysql";

}
