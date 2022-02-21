/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.lcdp.codegen.engine.resolver.db;

import javax.annotation.Nullable;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver;

import static java.lang.String.format;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.wl4g.dopaas.lcdp.codegen.utils.ResourceBundleUtil.readResource;
import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.common.reflect.ReflectionUtils2.findMethod;
import static com.wl4g.component.common.reflect.ReflectionUtils2.invokeMethod;

/**
 * {@link AbstractDbMetadataResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-09
 * @sine v1.0.0
 * @see
 */
public abstract class AbstractDbMetadataResolver implements MetadataResolver {

	protected final SmartLogger log = getLogger(getClass());

	/** {@link JdbcTemplate} */
	protected final JdbcTemplate jdbcTemplate;

	/** Data source configuration. */
	protected final String dbUrl;

	/**
	 * New parent of {@link AbstractDbMetadataResolver}
	 * 
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 */
	protected AbstractDbMetadataResolver(@NotBlank String driverClassName, @NotBlank String url, @NotBlank String username,
			@Nullable String password) {
		hasTextOf(driverClassName, "driverClassName");
		this.dbUrl = hasTextOf(url, "url");
		DataSource ds = DataSourceBuilder.create(Thread.currentThread().getContextClassLoader()).driverClassName(driverClassName)
				.url(url).username(username).password(password).build();
		this.jdbcTemplate = new JdbcTemplate(ds);
	}

	@Override
	public void close() throws IOException {
		try {
			DataSource ds = jdbcTemplate.getDataSource();
			invokeMethod(findMethod(ds.getClass(), "close"), ds);
		} catch (Throwable e) {
			log.warn(format("Cannot close medataResolver dataSource. for: %s", dbUrl), e);
		}
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
		return readResource(AbstractDbMetadataResolver.class, "sql/", sqlType, filename, true, args);
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
	public static Connection openConnection(String databaseUrl, String user, String password, String driverClass)
			throws SQLException, ClassNotFoundException {
		Class.forName(driverClass);
		return DriverManager.getConnection(databaseUrl, user, password);
	}

	// Database metadata query SQLs.
	public final static String SQL_TABLES = "find_tables.sql";
	public final static String SQL_TABLE_DESCRIBE = "find_table_describe.sql";
	public final static String SQL_COLUMNS = "find_columns.sql";
	public final static String SQL_FOREIGN = "find_foreign.sql";
	public final static String SQL_VERSION = "find_version.sql";

}