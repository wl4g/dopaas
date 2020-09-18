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
package com.wl4g.devops.dts.codegen.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * {@link GenTable}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
@Getter
@Setter
public class GenTable extends GenProject {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer databaseId;

	private Integer projectId;

	private String tableName;

	private String entityName;

	private String comments;

	private String parentTableName;

	private String parentTableFkName;

	private String dataSourceName;

	private String tplCategory;

	private String packageName;

	private String moduleName;

	private String subModuleName;

	private String functionName;

	private String functionNameSimple;

	private String functionAuthor;

	private String genBaseDir;

	private String options;

	// Extends
	private List<GenTableColumn> genTableColumns;

	private GenTableColumn pk;

}