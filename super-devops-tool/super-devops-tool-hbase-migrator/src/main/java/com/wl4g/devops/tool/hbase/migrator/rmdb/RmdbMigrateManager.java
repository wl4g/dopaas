package com.wl4g.devops.tool.hbase.migrator.rmdb;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;

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
					put(new String[] { "mysql", "mysql57" }, Mysql57MigrateManager.class);
					put(new String[] { "oracle", "oracle11g" }, Oracle11gMigrateManager.class);
				}
			});

	/**
	 * Table Name
	 */
	final private String tableName;

	/**
	 * Rmdb holder
	 */
	final private RmdbHolder rmdbHolder;

	public RmdbMigrateManager(CommandLine line) {
		this.tableName = HbaseMigrateUtils.getShortTableName(line.getOptionValue("tabname"));
		this.rmdbHolder = createRmdbHolder(line);
	}

	/**
	 * Gets {@link RmdbMigrateManager} instance.
	 * 
	 * @param alias
	 * @return
	 */
	public static RmdbMigrateManager getInstance(CommandLine line) throws Exception {
		String alias = line.getOptionValue("database");

		Class<? extends RmdbMigrateManager> cls = null;
		Iterator<Entry<String[], Class<? extends RmdbMigrateManager>>> it = registers.entrySet().iterator();
		ok: while (it.hasNext()) {
			Entry<String[], Class<? extends RmdbMigrateManager>> entry = it.next();
			for (String _alias : entry.getKey()) {
				if (equalsIgnoreCase(_alias, alias)) {
					cls = entry.getValue();
					break ok;
				}
			}
		}
		notNull(cls, "Invalid rmdb database provider alias: %s", alias);
		return cls.getConstructor(CommandLine.class).newInstance(line);
	}

	/**
	 * Create rmdb holder
	 * 
	 * @param line
	 * @return
	 */
	private RmdbHolder createRmdbHolder(CommandLine line) {
		String driver = getDriverClass();
		String url = line.getOptionValue("url");
		String username = line.getOptionValue("username");
		String password = line.getOptionValue("password");
		return new RmdbHolder(driver, url, username, password);
	}

	public String getTableName() {
		return tableName;
	}

	public RmdbHolder getRmdbHolder() {
		return rmdbHolder;
	}

	public abstract String getDriverClass();

	public abstract String buildInsertSql(LinkedHashMap<String, String> fields);

}
