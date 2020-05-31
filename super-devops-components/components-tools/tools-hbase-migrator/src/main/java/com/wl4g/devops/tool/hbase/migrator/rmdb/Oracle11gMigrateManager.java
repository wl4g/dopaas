package com.wl4g.devops.tool.hbase.migrator.rmdb;

import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.cli.CommandLine;

/**
 * Oracle11g sql builder
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public class Oracle11gMigrateManager extends RmdbMigrateManager {

	public Oracle11gMigrateManager(CommandLine line) {
		super(line);
	}

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
		return "oracle.jdbc.driver.OracleDriver";
	}

}
