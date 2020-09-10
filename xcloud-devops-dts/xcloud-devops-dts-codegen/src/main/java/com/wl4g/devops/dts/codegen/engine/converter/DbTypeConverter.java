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
package com.wl4g.devops.dts.codegen.engine.converter;

import java.io.IOException;

import com.wl4g.components.core.framework.operator.Operator;
import com.wl4g.devops.dts.codegen.utils.ResourceBundleUtils;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.DbConverterType;

/**
 * {@link DbTypeConverter}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public abstract class DbTypeConverter implements Operator<DbConverterType> {

	public abstract String convertToJavaType(String sqlType);

	public abstract String convertToSqlType(String javaType);

	/**
	 * Load converting SQL.
	 * 
	 * @param dbType
	 * @param filename
	 * @param args
	 * @return
	 * @throws IOException
	 */
	protected String loadConvertingSql(String dbType, String filename, String... args) throws IOException {
		return ResourceBundleUtils.readResource(false, TYPES_BASE_PATH, dbType, filename, args);
	}

	// Databsse types definitions.
	public final static String TYPES_BASE_PATH = DbTypeConverter.class.getName().replace(".", "/")
			.replace(DbTypeConverter.class.getSimpleName(), "") + "types/";
	public final static String TYPES_SQL_TO_JAVA = "sql-to-java.types";
	public final static String TYPES_JAVA_TO_SQL = "java-to-sql.types";

	/**
	 * {@link DbType}
	 * 
	 * @see
	 */
	public static enum DbConverterType {
		MySQL
	}

}
