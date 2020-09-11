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

import static com.wl4g.components.common.lang.Assert2.hasText;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.utils.ResourceBundleUtils.readResource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;

import com.wl4g.components.core.framework.operator.Operator;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.ConverterKind;

/**
 * Database type or JDBC type and Java class, go structure class, python class
 * ...
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public abstract class DbTypeConverter implements Operator<ConverterKind> {

	/** Sql and code types cache. */
	private final Map<CodeKind, TypePropertiesWrapper> typesCache = new ConcurrentHashMap<>(4);

	protected DbTypeConverter() {
		for (CodeKind ck : CodeKind.values()) {
			Properties sqlToCodeTypes = loadTypes(kind().name(), ck.getSqlToCodeFile());
			Properties codeToSqlTypes = loadTypes(kind().name(), ck.getCodeToSqlFile());
			this.typesCache.put(ck, new TypePropertiesWrapper(sqlToCodeTypes, codeToSqlTypes));
		}
	}

	/**
	 * Converting sql to language code type(java class, C# class, go struct,
	 * python class ...)
	 * 
	 * @param javaType
	 * @param codeKind
	 *            {@link CodeKind}
	 * @return
	 */
	public String convertToCodeType(@NotBlank String sqlType, @NotBlank String codeKind) {
		notNullOf(kind(), "codeKind");
		return hasText(typesCache.get(CodeKind.of(codeKind)).getSqlToCodeTypes().getProperty(sqlType),
				"No such sqlType: %s mapped codeType of codeKind: %s", sqlType, codeKind);
	}

	/**
	 * Converting language code type(java class, C# class, go struct, python
	 * class ...) to sql type
	 * 
	 * @param codeType
	 * @param codeKind
	 *            {@link CodeKind}
	 * @return
	 */
	public String convertToSqlType(@NotBlank String codeType, @NotBlank String codeKind) {
		notNullOf(kind(), "codeKind");
		return hasText(typesCache.get(CodeKind.of(codeKind)).getCodeToSqlTypes().getProperty(codeType),
				"No such codeType: %s mapped sqlType of codeKind: %s", codeType, codeKind);
	}

	/**
	 * Loading converting types.
	 * 
	 * @param dbType
	 * @param filename
	 * @return
	 */
	private static Properties loadTypes(@NotBlank String dbType, @NotBlank String filename) {
		hasTextOf(dbType, "dbType");
		hasTextOf(filename, "filename");
		try {
			Properties types = new Properties();
			types.load(new StringReader(readResource(false, TYPES_BASE_PATH, dbType, filename)));
			return types;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	/*
	 * Databsse types definitions base path.
	 */
	private final static String TYPES_BASE_PATH = DbTypeConverter.class.getName().replace(".", "/")
			.replace(DbTypeConverter.class.getSimpleName(), "") + "types/";

	/**
	 * {@link ConverterKind}
	 * 
	 * @see
	 */
	public static enum ConverterKind {
		MySQLV5, OracleV11g, PostgreSQLV10
	}

	/**
	 * {@link CodeKind}
	 * 
	 * @see
	 */
	public static enum CodeKind {
		JAVA("java", "sql-to-java.types", "java-to-sql.types"),

		GO("golang", "sql-to-golang.types", "golang-to-sql.types"),

		PYTHON("python", "sql-to-python.types", "python-to-sql.types"),

		C_SHARP("csharp", "sql-to-c_sharp.types", "c_sharp-to-sql.types");

		private final String alias;
		private final String sqlToCodeFile;
		private final String codeToSqlFile;

		private CodeKind(String alias, String sqlToCodeFile, String codeToSqlFile) {
			this.alias = hasTextOf(alias, "alias");
			this.sqlToCodeFile = hasTextOf(sqlToCodeFile, "sqlToCodeFile");
			this.codeToSqlFile = hasTextOf(codeToSqlFile, "codeToSqlFile");
		}

		public String getAlias() {
			return alias;
		}

		public String getSqlToCodeFile() {
			return sqlToCodeFile;
		}

		public String getCodeToSqlFile() {
			return codeToSqlFile;
		}

		public static CodeKind of(String alias) {
			return notNull(safeOf(alias), "No such codeKind of %s", alias);
		}

		public static CodeKind safeOf(String alias) {
			for (CodeKind ck : values()) {
				if (ck.getAlias().equalsIgnoreCase(alias) || ck.name().equalsIgnoreCase(alias)) {
					return ck;
				}
			}
			return null;
		}

	}

	/**
	 * {@link TypePropertiesWrapper}
	 *
	 * @since
	 */
	public static class TypePropertiesWrapper {

		private final Properties sqlToCodeTypes;
		private final Properties codeToSqlTypes;

		public TypePropertiesWrapper(Properties sqlToCodeTypes, Properties codeToSqlTypes) {
			this.sqlToCodeTypes = notNullOf(sqlToCodeTypes, "sqlToCodeTypes");
			this.codeToSqlTypes = notNullOf(codeToSqlTypes, "codeToSqlTypes");
		}

		public Properties getSqlToCodeTypes() {
			return sqlToCodeTypes;
		}

		public Properties getCodeToSqlTypes() {
			return codeToSqlTypes;
		}

	}

}
