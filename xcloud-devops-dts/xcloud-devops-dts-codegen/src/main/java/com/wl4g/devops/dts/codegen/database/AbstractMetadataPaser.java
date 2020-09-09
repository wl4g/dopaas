package com.wl4g.devops.dts.codegen.database;

import com.google.common.io.Resources;
import com.wl4g.components.common.log.SmartLogger;

import org.springframework.util.ResourceUtils;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * {@link AbstractMetadataPaser}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-09
 * @sine v1.0.0
 * @see
 */
public abstract class AbstractMetadataPaser implements MetadataPaser {

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
	protected String loadParseSql(String sqlType, String filename, String... args) throws IOException {
		return readResource(sqlType, filename, args);
	}

	/**
	 * Reading config resource file content.
	 * 
	 * @param sqlType
	 * @param filename
	 * @param args
	 * @return
	 * @throws IOException
	 */
	static String readResource(String sqlType, String filename, String... args) {
		hasTextOf(sqlType, "sqlType");
		hasTextOf(filename, "filename");

		try {
			String path = SQL_BASE_PATH.concat(sqlType).concat("/").concat(filename);
			File sqlFile = ResourceUtils.getFile("classpath:" + path);
			String sqlContent = Resources.toString(sqlFile.toURI().toURL(), UTF_8);
			return format(sqlContent, new Object[] { args });
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	// Database metadata query SQLs.
	public final static String SQL_BASE_PATH = AbstractMetadataPaser.class.getName().replace(".", "/")
			.replace(AbstractMetadataPaser.class.getName(), "") + "/sql/";
	public final static String SQL_QUERY_COLUMNS = "query_columns.sql";
	public final static String SQL_QUERY_FOREIGN = "query_foreign.sql";
	public final static String SQL_QUERY_TABLE = "query_table.sql";
	public final static String TYPES_SQL_TO_JAVA = "sql-to-java.types";
	public final static String TYPES_JAVA_TO_SQL = "java-to-sql.types";

}
