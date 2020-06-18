package com.wl4g.devops.tool.hbase.migrator.rmdb;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.contains;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;

import com.wl4g.devops.tool.hbase.migrator.SimpleHfileToRmdbExporter;
import com.wl4g.devops.tool.hbase.migrator.utils.HbaseMigrateUtils;

/**
 * {@link RmdbMigrateManager}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月17日 v1.0.0
 * @see
 */
public abstract class RmdbMigrateManager {

	/**
	 * Register
	 */
	final private static Map<String[], Class<? extends RmdbMigrateManager>> registers = unmodifiableMap(
			new HashMap<String[], Class<? extends RmdbMigrateManager>>() {
				private static final long serialVersionUID = 410424241261771123L;
				{
					put(new String[] { "jdbc:mysql" }, Mysql57MigrateManager.class);
					put(new String[] { "jdbc:oracle" }, Oracle11gMigrateManager.class);
					put(new String[] { "jdbc:postgresql" }, PostgresqlMigrateManager.class);
				}
			});

	/**
	 * Table Name
	 */
	final private String tableName;

	/**
	 * Rmdb holder
	 */
	final private RmdbRepository rmdbRepository;

	public RmdbMigrateManager(CommandLine line) {
		this.tableName = HbaseMigrateUtils.getShortTableName(line.getOptionValue("tabname"));
		this.rmdbRepository = createRmdbHolder(line);
	}

	/**
	 * Gets {@link RmdbMigrateManager} instance.
	 * 
	 * @param alias
	 * @return
	 */
	public static RmdbMigrateManager getInstance(CommandLine line) throws Exception {
		String jdbcUrl = line.getOptionValue("jdbcUrl");

		Class<? extends RmdbMigrateManager> cls = null;
		Iterator<Entry<String[], Class<? extends RmdbMigrateManager>>> it = registers.entrySet().iterator();
		ok: while (it.hasNext()) {
			Entry<String[], Class<? extends RmdbMigrateManager>> entry = it.next();
			for (String _alias : entry.getKey()) {
				if (contains(jdbcUrl, _alias)) {
					cls = entry.getValue();
					break ok;
				}
			}
		}
		notNull(cls, "Invalid rmdb database provider alias: %s", jdbcUrl);
		return cls.getConstructor(CommandLine.class).newInstance(line);
	}

	/**
	 * Create rmdb holder
	 * 
	 * @param line
	 * @return
	 */
	private RmdbRepository createRmdbHolder(CommandLine line) {
		String driver = getDriverClass();
		String url = line.getOptionValue("jdbcUrl");
		String username = line.getOptionValue("username");
		String password = line.getOptionValue("password");
		String maxConnections = line.getOptionValue("maxConnections", SimpleHfileToRmdbExporter.DEFAULT_RMDB_MAXCONNECTIONS + "");
		return new RmdbRepository(driver, url, username, password, Integer.parseInt(maxConnections));
	}

	public String getTableName() {
		return tableName;
	}

	public RmdbRepository getRmdbRepository() {
		return rmdbRepository;
	}

	public abstract String getDriverClass();

	public abstract String buildInsertSql(LinkedHashMap<String, String> fields);

}
