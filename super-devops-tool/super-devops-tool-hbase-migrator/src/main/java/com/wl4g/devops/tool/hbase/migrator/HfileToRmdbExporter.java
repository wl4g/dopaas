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
package com.wl4g.devops.tool.hbase.migrator;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import com.wl4g.devops.tool.common.cli.CommandUtils.Builder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.tool.hbase.migrator.mapred.HfileToRmdbMapper;
import com.wl4g.devops.tool.hbase.migrator.utils.HbaseMigrateUtils;

/**
 * HASE hfile to mysql exporter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年9月6日
 * @since
 */
public class HfileToRmdbExporter {
	final static Log log = LogFactory.getLog(HfileToRmdbExporter.class);

	final public static String DEFAULT_MAPPER_CLASS = HfileToRmdbMapper.class.getName();
	public static RmdbSqlBuilder currentRmdbSqlBuilder;
	public static String currentRmdbTable;

	/**
	 * e.g. </br>
	 * 
	 * <pre>
	 * yarn jar super-devops-tool-hbase-migrator-master.jar \
	 * com.wl4g.devops.tool.hbase.migrator.HfileToRmdbExporter \
	 * -z emr-header-1:2181 \
	 * -t safeclound.tb_elec_power \
	 * -d mysql \
	 * -s 11111112,ELE_R_P,134,01,20180919110850989 \
	 * -e 11111112,ELE_R_P,134,01,20180921124050540
	 * </pre>
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HbaseMigrateUtils.showBanner();

		Builder builder = HfileBulkExporter.getRequiresCmdLineBuilder();
		builder.option("M", "mapperClass", false, "Transfrom migration mapper class name. default: " + DEFAULT_MAPPER_CLASS);
		builder.option("d", "database", true, "Hbase to rmdb database provider. e.g: mysql|oracle");
		CommandLine line = builder.build(args);

		// Gets rmdb provider instance.
		String rmdbAlias = line.getOptionValue("database");
		currentRmdbSqlBuilder = RmdbSqlBuilder.getInstance(rmdbAlias);
		currentRmdbTable = line.getOptionValue("tabname");
		notNull(currentRmdbSqlBuilder, "Invalid rmdb database provider alias: %s", rmdbAlias);

		// DO exporting
		HfileBulkExporter.doExporting(line);
	}

	/**
	 * {@link RmdbSqlBuilder}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年5月17日 v1.0.0
	 * @see
	 */
	public static abstract class RmdbSqlBuilder {

		final private static Map<String[], RmdbSqlBuilder> providers = unmodifiableMap(new HashMap<String[], RmdbSqlBuilder>() {
			private static final long serialVersionUID = 410424241261771123L;
			{
				put(new String[] { "mysql", "mysql57" }, new Mysql57SqlBuilder());
				put(new String[] { "oracle", "oracle11g" }, new Oracle11gSqlBuilder());
			}
		});

		/**
		 * Gets {@link RmdbSqlBuilder} instance.
		 * 
		 * @param alias
		 * @return
		 */
		public static RmdbSqlBuilder getInstance(String alias) {
			RmdbSqlBuilder instance = null;
			Iterator<Entry<String[], RmdbSqlBuilder>> it = providers.entrySet().iterator();
			ok: while (it.hasNext()) {
				Map.Entry<String[], RmdbSqlBuilder> entry = it.next();
				for (String _alias : entry.getKey()) {
					if (equalsIgnoreCase(_alias, alias)) {
						instance = entry.getValue();
						break ok;
					}
				}
			}
			return instance;
		}

		public abstract String buildInsertSql(LinkedHashMap<String, String> fields);

	}

	/**
	 * Oracle11g sql builder
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年5月17日 v1.0.0
	 * @see
	 */
	public static class Oracle11gSqlBuilder extends RmdbSqlBuilder {

		@Override
		public String buildInsertSql(LinkedHashMap<String, String> fields) {
			return null;
		}

	}

	/**
	 * Mysql sql builder
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年5月17日 v1.0.0
	 * @see
	 */
	public static class Mysql57SqlBuilder extends RmdbSqlBuilder {

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
			sql.append(currentRmdbTable);
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
			sql.append(") VALUES (");
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

	}

}