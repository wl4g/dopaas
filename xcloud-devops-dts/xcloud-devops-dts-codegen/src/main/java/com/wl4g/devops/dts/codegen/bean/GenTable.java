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
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.MODEL_FOR_MODULENAME;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.MODEL_FOR_ENTITYNAME;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Wither;

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
@Wither
@ToString
public class GenTable extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer projectId;

	@RenderProperty
	private String tableName;

	@RenderProperty(propertyName = MODEL_FOR_ENTITYNAME)
	private String entityName;

	@RenderProperty
	private String comments;

	@RenderProperty(propertyName = MODEL_FOR_MODULENAME)
	private String moduleName;

	@RenderProperty
	private String subModuleName;

	@RenderProperty
	private String functionName;

	@RenderProperty
	private String functionNameSimple;

	@RenderProperty
	private String functionAuthor;

	@RenderProperty
	private String options;

	@RenderProperty
	private String status;

	// Extends
	@RenderProperty
	private List<GenTableColumn> genTableColumns;

	@RenderProperty(describeForObjField = "No")
	private GenTableColumn pk;

	public GenTable() {
		super();
	}

	public GenTable(Integer projectId, String tableName, String entityName, String comments, String moduleName,
			String subModuleName, String functionName, String functionNameSimple, String functionAuthor, String options,
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
		this.status = status;
		this.genTableColumns = genTableColumns;
		this.pk = pk;
	}

}