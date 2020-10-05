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
package com.wl4g.devops.dts.codegen.utils;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderAlias.IAM_SPINGCLOUD_MVN;
import static com.wl4g.devops.dts.codegen.engine.generator.GeneratorProvider.GenProviderSet.getProviders;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotNull;

import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;

import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.DbType;

/**
 * {@link GenUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-05
 * @sine v1.0.0
 * @see
 */
public abstract class GenUtils {

	/**
	 * Check {@link GenTable} columns type with builtin definitions.
	 * 
	 * @param datasource
	 * @param project
	 * @param table
	 * @return Warning tip message.
	 */
	public static String checkBuiltinColumns(@NotNull GenDataSource datasource, @NotNull GenProject project,
			@NotNull GenTable table) {
		notNullOf(datasource, "datasource");
		notNullOf(project, "project");
		notNullOf(table, "table");

		// Check requires provider.
		List<String> providers = getProviders(project.getProviderSet());
		if (!providers.contains(IAM_SPINGCLOUD_MVN)) {
			return null; // Pass
		}

		// Check to extract the matched builtin columns.
		DbType dbType = DbType.of(datasource.getType());
		Set<BuiltinTableColumns> matches = asList(BuiltinTableColumns.values()).stream()
				// Check the specific database column name and type that are
				// supported?
				.filter(def -> safeList(table.getGenTableColumns()).stream()
						.filter(col -> equalsIgnoreCase(col.getColumnName(), def.getColumnName())
								&& def.getColumnTypes().getOrDefault(dbType, emptyList()).contains(col.getSimpleColumnType()))
						.findAny().isPresent())
				.collect(toSet());

		String title = null;
		StringBuilder warningTip = new StringBuilder();
		for (BuiltinTableColumns missing : difference(newHashSet(BuiltinTableColumns.values()), matches)) {
			if (isNull(title)) {
				warningTip.append(title = "检测到当前表无以下基础字段，这可能导致部分功能不可用（详情请参考页面提示信息）：</br>");
			}
			List<String> missingColumnTypes = missing.getColumnTypes().entrySet().stream().filter(e -> e.getKey() == dbType)
					.map(e -> e.getValue()).findFirst().orElse(null);
			warningTip.append(
					missing.getColumnName() + " " + missingColumnTypes.toString() + " - " + (missing.isRequired() ? "必须" : "建议"));
			warningTip.append("</br>");
		}

		return warningTip.toString();
	}

	/**
	 * Default builtin table columns definitions.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-10-05
	 * @sine v1.0.0
	 * @see
	 */
	public static enum BuiltinTableColumns {

		@SuppressWarnings("serial")
		ID(true, "id", new HashMap<DbType, List<String>>() {
			{
				put(DbType.MySQLV5, asList("bigint"));
				put(DbType.OracleV11g, asList("number"));
				put(DbType.PostgreSQLV10, asList("bigint"));
			}
		}),

		@SuppressWarnings("serial")
		CREATE_BY(false, "create_by", new HashMap<DbType, List<String>>() {
			{
				put(DbType.MySQLV5, asList("bigint"));
				put(DbType.OracleV11g, asList("number"));
				put(DbType.PostgreSQLV10, asList("bigint"));
			}
		}),

		@SuppressWarnings("serial")
		CREATE_DATE(false, "create_date", new HashMap<DbType, List<String>>() {
			{
				put(DbType.MySQLV5, asList("datetime"));
				put(DbType.OracleV11g, asList("datetime"));
				put(DbType.PostgreSQLV10, asList("datetime"));
			}
		}),

		@SuppressWarnings("serial")
		UPDATE_BY(false, "update_by", new HashMap<DbType, List<String>>() {
			{
				put(DbType.MySQLV5, asList("bigint"));
				put(DbType.OracleV11g, asList("number"));
				put(DbType.PostgreSQLV10, asList("bigint"));
			}
		}),

		@SuppressWarnings("serial")
		UPDATE_DATE(false, "update_date", new HashMap<DbType, List<String>>() {
			{
				put(DbType.MySQLV5, asList("datetime"));
				put(DbType.OracleV11g, asList("datetime"));
				put(DbType.PostgreSQLV10, asList("datetime"));
			}
		}),

		@SuppressWarnings("serial")
		DEL_FLAG(false, "del_flag", new HashMap<DbType, List<String>>() {
			{
				put(DbType.MySQLV5, asList("int"));
				put(DbType.OracleV11g, asList("int"));
				put(DbType.PostgreSQLV10, asList("int"));
			}
		});

		private final boolean required;
		private final String columnName;
		// Candidates db column types.
		private final Map<DbType, List<String>> columnTypes;

		private BuiltinTableColumns(boolean required, String columnName, Map<DbType, List<String>> columnTypes) {
			this.required = required;
			this.columnName = hasTextOf(columnName, "columnName");
			this.columnTypes = notEmptyOf(columnTypes, "columnTypes");
		}

		public boolean isRequired() {
			return required;
		}

		public String getColumnName() {
			return columnName;
		}

		public Map<DbType, List<String>> getColumnTypes() {
			return columnTypes;
		}

	}

}
