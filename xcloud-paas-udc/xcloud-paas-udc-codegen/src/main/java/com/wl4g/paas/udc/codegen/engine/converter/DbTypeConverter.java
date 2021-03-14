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
package com.wl4g.paas.udc.codegen.engine.converter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.paas.udc.codegen.engine.converter.DbTypeConverter.TypeMappingWrapper.MappedMatcher;
import static com.wl4g.component.common.lang.Assert2.*;
import static com.wl4g.component.common.lang.StringUtils2.tokenizeToStringArray;
import static com.wl4g.paas.udc.codegen.utils.ResourceBundleUtil.readResource;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * Database type or JDBC/SQL type and Java class, go structure class, python
 * class converter, etc
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public enum DbTypeConverter {

	JAVA("java", "java-sql-column.types"),

	/**
	 * e.g: NodeJS, Javascript
	 */
	JS("js", "js-sql-column.types"),

	Golang("golang", "golang-sql-column.types"),

	Python("python", "python-sql-column.types"),

	Csharp("csharp", "c_sharp-sql-column.types");

	/**
	 * Codes language.
	 */
	private final String language;
	private final String mappedResource;
	/** Attr type and sql type, column type mapping cache. */
	private final Map<DbType, List<TypeMappingWrapper>> mappingCache = new ConcurrentHashMap<>(4);

	private DbTypeConverter(String language, String mappedResource) {
		this.language = hasTextOf(language, "language");
		this.mappedResource = hasTextOf(mappedResource, "mappedResource");
		// Load converter mapping types.
		for (DbType dbType : DbType.values()) {
			this.mappingCache.put(dbType, loadTypeMapping(dbType.getDbName(), mappedResource));
		}
	}

	public String getLanguage() {
		return language;
	}

	public String getMappingResource() {
		return mappedResource;
	}

	public static DbTypeConverter of(String language) {
		return notNull(safeOf(language), "No such DB type converter of %s", language);
	}

	public static DbTypeConverter safeOf(String language) {
		for (DbTypeConverter k : values()) {
			if (k.getLanguage().equalsIgnoreCase(language) || k.name().equalsIgnoreCase(language)) {
				return k;
			}
		}
		return null;
	}

	/**
	 * Type attrType(Java class, C# class, GO struct, Python class, etc)
	 * conversion with sqlType and database column type
	 * 
	 * @param converter
	 *            {@link DbTypeConverter}
	 * @param fromType
	 * @return
	 */
	public String convertBy(@NotBlank String dbType, @NotNull MappedMatcher matcher, @NotBlank String fromType) {
		return convertBy(DbType.of(hasTextOf(dbType, "dbType")), matcher, fromType);
	}

	/**
	 * Type attrType(Java class, C# class, GO struct, Python class, etc)
	 * conversion with sqlType and database column type
	 * 
	 * @param dbType
	 *            {@link DbType}
	 * @param fromType
	 * @return
	 */
	public String convertBy(@NotNull DbType dbType, @NotNull MappedMatcher matcher, @NotBlank String fromType) {
		notNullOf(dbType, "converter");
		notNullOf(matcher, "matcher");
		hasTextOf(fromType, "fromType");

		// Gets type mapped
		List<TypeMappingWrapper> mapping = notEmpty(mappingCache.get(dbType), "No such type mapped of %s", dbType);
		return hasText(matcher.getHandler().matchs(mapping, fromType),
				"No such mapped type of lang: %s, matcher: %s, fromType: %s", dbType, matcher, fromType);
	}

	/**
	 * Gets {@link TypeMappingWrapper}
	 * 
	 * @param dbType
	 *            {@link DbType}
	 * @return
	 */
	public List<TypeMappingWrapper> getTypeMappings(String dbType) {
		return mappingCache.get(DbType.of(dbType));
	}

	/**
	 * Gets {@link TypeMappingWrapper}
	 * 
	 * @param dbType
	 *            {@link DbType}
	 * @return
	 */
	public List<TypeMappingWrapper> getTypeMappings(DbType dbType) {
		return mappingCache.get(dbType);
	}

	/**
	 * Load the {@code xxx-sql-column.types} file found in the resources.
	 * 
	 * @return a multi-value map, mapping media types to file extensions.
	 */
	private final List<TypeMappingWrapper> loadTypeMapping(@NotBlank String dbType, @NotBlank String filename) {
		hasTextOf(dbType, "dbType");
		hasTextOf(filename, "filename");
		List<TypeMappingWrapper> mappings = new ArrayList<>(16);

		try (BufferedReader reader = new BufferedReader(
				new StringReader(readResource(DbTypeConverter.class, "types", dbType, filename, false)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#') {
					continue;
				}
				String[] tokens = tokenizeToStringArray(line, " \t\n\r\f");
				mappings.add(new TypeMappingWrapper(tokens[2], tokens[1], tokens[0]));
			}
			return mappings;
		} catch (Throwable ex) {
			throw new IllegalStateException("Could not load '" + filename + "'", ex);
		}
	}

	/**
	 * {@link DbType}
	 * 
	 * @see
	 */
	public static enum DbType {
		MySQLV5("mysqlv5"),

		OracleV11g("oraclev11g"),

		PostgreSQLV10("postgresqlv10");

		private final String dbName;

		private DbType(String alias) {
			this.dbName = hasTextOf(alias, "alias");
		}

		public String getDbName() {
			return dbName;
		}

		public static DbType of(String dbType) {
			return notNull(safeOf(dbType), "No such dbType of %s", dbType);
		}

		public static DbType safeOf(String dbType) {
			for (DbType t : values()) {
				if (t.getDbName().equalsIgnoreCase(dbType) || t.name().equalsIgnoreCase(dbType)) {
					return t;
				}
			}
			return null;
		}

	}

	/**
	 * {@link TypeMappingWrapper}
	 *
	 * @since
	 */
	public static class TypeMappingWrapper {

		private final String columnType; // DB column type
		private final String sqlType; // e.g: JDBC type
		private final String attrType; // e.g: JavaBean fieldType

		public TypeMappingWrapper(String columnType, String sqlType, String attrType) {
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

			Attr2Sql((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getAttrType(), fromType))
					.map(m -> m.getSqlType()).findFirst().orElse(null)),

			Sql2Column((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getSqlType(), fromType))
					.map(m -> m.getColumnType()).findFirst().orElse(null)),

			Attr2Column((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getAttrType(), fromType))
					.map(m -> m.getColumnType()).findFirst().orElse(null)),

			Column2Sql((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getColumnType(), fromType))
					.map(m -> m.getSqlType()).findFirst().orElse(null)),

			Column2Attr((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getColumnType(), fromType))
					.map(m -> m.getAttrType()).findFirst().orElse(null)),

			Sql2Attr((mapped, fromType) -> mapped.stream().filter(m -> equalsIgnoreCase(m.getSqlType(), fromType))
					.map(m -> m.getAttrType()).findFirst().orElse(null));

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
			String matchs(List<TypeMappingWrapper> mapping, String fromType);
		}

	}

}