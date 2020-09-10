package com.wl4g.devops.dts.codegen.engine.resolver;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.dts.codegen.utils.ResourceBundleUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

/**
 * {@link AbstractMetadataResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-09
 * @sine v1.0.0
 * @see
 */
public abstract class AbstractMetadataResolver implements MetadataResolver {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * New open {@link Connection}
	 * 
	 * @param databaseUrl
	 * @param user
	 * @param password
	 * @param driverClass
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	protected Connection openConnection(String databaseUrl, String user, String password, String driverClass)
			throws SQLException, ClassNotFoundException {
		Class.forName(driverClass);
		return DriverManager.getConnection(databaseUrl, user, password);
	}

	/**
	 * Load parsing SQL.
	 * 
	 * @param sqlType
	 * @param filename
	 * @param args
	 * @return
	 * @throws IOException
	 */
	protected String loadResolvingSql(String sqlType, String filename, String... args) throws IOException {
		return ResourceBundleUtils.readResource(true, SQL_BASE_PATH, sqlType, filename, args);
	}

	// Database metadata query SQLs.
	public final static String SQL_BASE_PATH = AbstractMetadataResolver.class.getName().replace(".", "/")
			.replace(AbstractMetadataResolver.class.getSimpleName(), "") + "/sql/";
	public final static String SQL_QUERY_COLUMNS = "query_columns.sql";
	public final static String SQL_QUERY_FOREIGN = "query_foreign.sql";
	public final static String SQL_QUERY_TABLE = "query_table.sql";

}
