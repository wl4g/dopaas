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

import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.RenderProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Wither;

import java.util.List;
import java.util.Map;

import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;

/**
 * {@link GenTable}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
@Getter
@Setter
@Wither
@ToString
public class GenTable extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer projectId;

	@RenderProperty(propertyName = GEN_TABLE_NAME)
	private String tableName;

	@RenderProperty(propertyName = GEN_TABLE_ENTITY_NAME)
	private String entityName;

	@RenderProperty(propertyName = GEN_TABLE_COMMENT)
	private String comments;

	@RenderProperty(propertyName = GEN_MODULE_NAME)
	private String moduleName;

	@RenderProperty(propertyName = GEN_MODULE_SUB_NAME)
	private String subModuleName;

	@RenderProperty(propertyName = GEN_TABLE__FUNC_NAME)
	private String functionName;

	@RenderProperty(propertyName = GEN_TABLE_FUNC_SIMPLE_NAME)
	private String functionNameSimple;

	@RenderProperty(propertyName = GEN_TABLE_FUNC_AUTHOR)
	private String functionAuthor;

	private String options;

	@RenderProperty(propertyName = GEN_TABLE_OPTION_MAP)
	private Map<String, Object> optionMap;

	private String status;

	// --- Temporary fields. ---

	@RenderProperty(propertyName = GEN_TABLE_COLUMNS, describeForObjField = "No")
	private List<GenTableColumn> genTableColumns;

	@RenderProperty(propertyName = GEN_TABLE_PRIMARY, describeForObjField = "No")
	private GenTableColumn pk;

	public GenTable() {
		super();
	}

	public GenTable(Integer projectId, String tableName, String entityName, String comments, String moduleName,
			String subModuleName, String functionName, String functionNameSimple, String functionAuthor, String options,Map<String, Object> optionMap,
			String status, List<GenTableColumn> genTableColumns, GenTableColumn pk) {
		super();
		this.projectId = projectId;
		this.tableName = tableName;
		this.entityName = entityName;
		this.comments = comments;
		this.moduleName = moduleName;
		this.subModuleName = subModuleName;
		this.functionName = functionName;
		this.functionNameSimple = functionNameSimple;
		this.functionAuthor = functionAuthor;
		this.options = options;
		this.optionMap = optionMap;
		this.status = status;
		this.genTableColumns = genTableColumns;
		this.pk = pk;
	}

}