package com.wl4g.devops.dts.codegen.engine.resolver;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.devops.dts.codegen.utils.ResourceBundleUtils;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.reflect.ReflectionUtils2.findMethod;
import static com.wl4g.components.common.reflect.ReflectionUtils2.invokeMethod;

/**
 * {@link AbstractMetadataResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-09
 * @sine v1.0.0
 * @see
 */
public abstract class AbstractMetadataResolver implements MetadataResolver, Closeable {

	protected final SmartLogger log = getLogger(getClass());

	/** {@link JdbcTemplate} */
	protected final JdbcTemplate jdbcTemplate;

	/**
	 * New parent of {@link AbstractMetadataResolver}
	 * 
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 */
	protected AbstractMetadataResolver(String driverClassName, String url, String username, String password) {
		DataSource ds = DataSourceBuilder.create(Thread.currentThread().getContextClassLoader()).driverClassName(driverClassName)
				.url(url).username(username).password(password).build();
		this.jdbcTemplate = new JdbcTemplate(ds);
	}

	@Override
	public void close() throws IOException {
		DataSource ds = jdbcTemplate.getDataSource();
		invokeMethod(findMethod(ds.getClass(), "close"), ds);
	}

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
	@Deprecated
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
	protected String loadResolvingSql(String sqlType, String filename, String... args) {
		return ResourceBundleUtils.readResource(true, SQL_BASE_PATH, sqlType, filename, args);
	}

	// Database metadata query SQLs.
	public final static String SQL_BASE_PATH = AbstractMetadataResolver.class.getName().replace(".", "/")
			.replace(AbstractMetadataResolver.class.getSimpleName(), "") + "sql/";
	public final static String SQL_TABLES = "find_tables.sql";
	public final static String SQL_TABLE_DESCRIBE = "find_table_describe.sql";
	public final static String SQL_COLUMNS = "find_columns.sql";
	public final static String SQL_FOREIGN = "find_foreign.sql";
	public final static String SQL_VERSION = "find_version.sql";

}
