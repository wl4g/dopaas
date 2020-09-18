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
package com.wl4g.devops.dts.codegen.engine.resolver;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * {@link TableMetadata}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-09
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
public class TableMetadata {

	private String tableName;

	private String comments;

	private List<ColumnMetadata> columns;

	/**
	 * {@link ColumnMetadata}
	 * 
	 * @see
	 */
	@Getter
	@Setter
	public static class ColumnMetadata {

		// 列名
		private String columnName;

		// 主键？
		private String columnKey;

		// 列名类型
		private String columnType;

		// 列名备注
		private String comments;

		// 属性类型
		private String attrType;

		// extra config
		private String extra;

	}

	/**
	 * {@link ColumnMetadata}
	 * 
	 * @see
	 */
	@Getter
	@Setter
	public static class ForeignMetadata {

		private String dbName;
		private String forTableName;
		private String refTableName;
		private String forColumnName;
		private String refColumnName;

	}

}