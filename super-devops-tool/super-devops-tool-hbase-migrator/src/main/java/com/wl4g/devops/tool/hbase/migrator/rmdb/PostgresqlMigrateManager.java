package com.wl4g.devops.tool.hbase.migrator.rmdb;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.cli.CommandLine;

/**
 * Mysql sql builder
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public class PostgresqlMigrateManager extends RmdbMigrateManager {

	public PostgresqlMigrateManager(CommandLine line) {
		super(line);
	}

	/**
	 * <pre>
	 * INSERT INTO `safecloud_tsdb`.`tb_ammeter` (
	 * `ROW`,
	 * `activePower`,
	 * `reactivePower`,
	 * `cid`,
	 * `bid`
	 * ) VALUES (
	 * '11111112,ELE_P,111,03,20191219000242674',
	 *  '3650.4238',
	 *  '792.91797',
	 *  NULL,
	 *  NULL);
	 * </pre>
	 */
	@Override
	public String buildInsertSql(LinkedHashMap<String, String> fields) {
		StringBuffer sql = new StringBuffer("INSERT INTO ");
		sql.append("`");
		sql.append(getTableName());
		sql.append("`(");
		Iterator<String> itk = fields.keySet().iterator();
		while (itk.hasNext()) {
			String field = itk.next();
			sql.append("`");
			sql.append(field);
			sql.append("`");
			if (itk.hasNext()) {
				sql.append(",");
			}
		}
		sql.append(")VALUES(");
		Iterator<String> itv = fields.values().iterator();
		while (itv.hasNext()) {
			String value = itv.next();
			boolean isNumber = isNumeric(value);
			if (!isNumber) {
				sql.append("'");
			}
			sql.append(value);
			if (!isNumber) {
				sql.append("'");
			}
			if (itv.hasNext()) {
				sql.append(",");
			}
		}
		sql.append(");");
		return sql.toString();
	}

	@Override
	public String getDriverClass() {
		return "org.postgresql.ds.PGSimpleDataSource";
	}

}
