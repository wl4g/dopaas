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

import com.wl4g.components.common.lang.StringUtils2;
import com.wl4g.components.core.framework.operator.Operator;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.ConverterKind;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.TypeMappedWrapper.MappedMatcher;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.devops.dts.codegen.utils.ResourceBundleUtils.readResource;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Database type or JDBC/SQL type and Java class, go structure class, python
 * class converter, etc
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public abstract class DbTypeConverter implements Operator<ConverterKind> {

	/** Sql and code types cache. */
	private final Map<CodeLanguage, List<TypeMappedWrapper>> typesCache = new ConcurrentHashMap<>(4);

	protected DbTypeConverter() {
		for (CodeLanguage mk : CodeLanguage.values()) {
			this.typesCache.put(mk, loadMappingTypes(kind().getDbName(), mk.getMappingResource()));
		}
	}

	/**
	 * Type attrType(Java class, C# class, GO struct, Python class, etc)
	 * conversion with sqlType and database column type
	 * 
	 * @param lang
	 *            {@link CodeLanguage}
	 * @param fromType
	 * @return
	 */
	public String convertBy(@NotBlank String lang, @NotNull MappedMatcher matcher, @NotBlank String fromType) {
		return convertBy(CodeLanguage.of(hasTextOf(lang, "lang")), matcher, fromType);
	}

	/**
	 * Type attrType(Java class, C# class, GO struct, Python class, etc)
	 * conversion with sqlType and database column type
	 * 
	 * @param lang
	 *            {@link CodeLanguage}
	 * @param fromType
	 * @return
	 */
	public String convertBy(@NotNull CodeLanguage lang, @NotNull MappedMatcher matcher, @NotBlank String fromType) {
		notNullOf(lang, "lang");
		notNullOf(matcher, "matcher");
		hasTextOf(fromType, "fromType");

		// Gets type mapped
		List<TypeMappedWrapper> mapped = notEmpty(typesCache.get(lang), "No such type mapped of %s", lang);
		return hasText(matcher.getHandler().matchs(mapped, fromType),
				"No such mapped type of lang: %s, matcher: %s, fromType: %s", lang, matcher, fromType);
	}

	/**
	 * Load the {@code xxx-sql-column.types} file found in the resources.
	 * 
	 * @return a multi-value map, mapping media types to file extensions.
	 */
	private static List<TypeMappedWrapper> loadMappingTypes(@NotBlank String dbType, @NotBlank String filename) {
		hasTextOf(dbType, "dbType");
		hasTextOf(filename, "filename");
		List<TypeMappedWrapper> mappings = new ArrayList<>(16);

		try (BufferedReader reader = new BufferedReader(
				new StringReader(readResource(false, TYPES_BASE_PATH, dbType, filename)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#') {
					continue;
				}
				String[] tokens = StringUtils2.tokenizeToStringArray(line, " \t\n\r\f");
				mappings.add(new TypeMappedWrapper(tokens[2], tokens[1], tokens[0]));
			}
			return mappings;
		} catch (IOException ex) {
			throw new IllegalStateException("Could not load '" + filename + "'", ex);
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
		MySQLV5("mysqlv5"),

		OracleV11g("oraclev11g"),

		PostgreSQLV10("postgresqlv10");

		private final String dbName;

		private ConverterKind(String alias) {
			this.dbName = hasTextOf(alias, "alias");
		}

		public String getDbName() {
			return dbName;
		}

	}

	/**
	 * Codes of {@link CodeLanguage}
	 * 
	 * @see
	 */
	public static enum CodeLanguage {

		JAVA("java", "java-sql-column.types"),

		GO("golang", "golang-sql-column.types"),

		PYTHON("python", "python-sql-column.types"),

		C_SHARP("csharp", "c_sharp-sql-column.types");

		private final String lang; // language
		private final String mappingResource;

		private CodeLanguage(String lang, String mappingResource) {
			this.lang = hasTextOf(lang, "lang");
			this.mappingResource = hasTextOf(mappingResource, "mappingResource");
		}

		public String getAlias() {
			return lang;
		}

		public String getMappingResource() {
			return mappingResource;
		}

		public static CodeLanguage of(String lang) {
			return notNull(safeOf(lang), "No such codeKind of %s", lang);
		}

		public static CodeLanguage safeOf(String lang) {
			for (CodeLanguage mk : values()) {
				if (mk.getAlias().equalsIgnoreCase(lang) || mk.name().equalsIgnoreCase(lang)) {
					return mk;
				}
			}
			return null;
		}

	}

	/**
	 * {@link TypeMappedWrapper}
	 *
	 * @since
	 */
	public static class TypeMappedWrapper {

		private final String columnType; // DB column type
		private final String sqlType; // e.g: JDBC type
		private final String attrType; // e.g: JavaBean fieldType

		public TypeMappedWrapper(String columnType, String sqlType, String attrType) {
			this.columnType = notNullOf(columnType, "columnType");
			this.sqlType = notNullOf(sqlType, "sqlType");
			this.attrType = notNullOf(attrType, "attrType");
		}

		public String getColumnType() {
			return columnType;
		}

		public String getSqlType() {
			return sqlType;
		}

		public String getAttrType() {
			return attrType;
		}

		/**
		 * {@link MappedMatcher}
		 * 
		 * @see
		 */
		public static enum MappedMatcher {

			lang2Sql((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getAttrType(),fromType)).map(m -> m.getSqlType())
					.findFirst().orElse(null)),

			Sql2Column((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getSqlType(),fromType))
					.map(m -> m.getColumnType()).findFirst().orElse(null)),

			Lang2Column((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getAttrType(),fromType))
					.map(m -> m.getColumnType()).findFirst().orElse(null)),

			Column2Sql((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getColumnType(),fromType))
					.map(m -> m.getSqlType()).findFirst().orElse(null)),

			Column2Lang((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getColumnType(),fromType))
					.map(m -> m.getAttrType()).findFirst().orElse(null)),

			Sql2Lang((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getSqlType(),fromType)).map(m -> m.getAttrType())
					.findFirst().orElse(null));

			/**
			 * {@link MappedMatcherHandler}
			 */
			private final MappedMatcherHandler handler;

			private MappedMatcher(MappedMatcherHandler handler) {
				notNullOf(handler, "handler");
				this.handler = handler;
			}

			public MappedMatcherHandler getHandler() {
				return handler;
			}

		}

		/**
		 * {@link MappedMatcherHandler}
		 */
		public static interface MappedMatcherHandler {
			String matchs(List<TypeMappedWrapper> mapped, String fromType);
		}

	}

}