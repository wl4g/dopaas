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

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.devops.dts.codegen.bean.extra.GenTableExtraOption;
import com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.RenderProperty;
import lombok.Getter;
import lombok.Setter;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.collection.Collections2.isEmptyArray;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * {@link GenTable}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
@Getter
@Setter
public class GenTable extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Long projectId;

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
	private Map<String, String> optionMap;

	private String status;

	// --- Temporary fields. ---

	@RenderProperty(propertyName = GEN_TABLE_COLUMNS, describeForObjField = "No")
	private List<GenTableColumn> genTableColumns;

	@RenderProperty(propertyName = GEN_TABLE_PRIMARY, describeForObjField = "No")
	private GenTableColumn pk;

	public GenTable() {
		super();
	}

	public GenTable withProjectId(Long projectId) {
		setProjectId(projectId);
		return this;
	}

	public GenTable withTableName(String tableName) {
		setTableName(tableName);
		return this;
	}

	public GenTable withEntityName(String entityName) {
		setEntityName(entityName);
		return this;
	}

	public GenTable withComments(String comments) {
		setComments(comments);
		return this;
	}

	public GenTable withModuleName(String moduleName) {
		setModuleName(moduleName);
		return this;
	}

	public GenTable withSubModuleName(String subModuleName) {
		setSubModuleName(subModuleName);
		return this;
	}

	public GenTable withFunctionName(String functionName) {
		setFunctionName(functionName);
		return this;
	}

	public GenTable withFunctionNameSimple(String functionNameSimple) {
		setFunctionNameSimple(functionNameSimple);
		return this;
	}

	public GenTable withFunctionAuthor(String functionAuthor) {
		setFunctionAuthor(functionAuthor);
		return this;
	}

	public GenTable withOptions(String options) {
		setOptions(options);
		return this;
	}

	public GenTable withOptionMap(Map<String, String> optionMap) {
		setOptionMap(optionMap);
		return this;
	}

	public GenTable withStatus(String status) {
		setStatus(status);
		return this;
	}

	public GenTable withGenTableColumns(List<GenTableColumn> genTableColumns) {
		setGenTableColumns(genTableColumns);
		return this;
	}

	public GenTable withPk(GenTableColumn pk) {
		setPk(pk);
		return this;
	}

	/**
	 * {@link GenTable} extensible configuration options definitions.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static enum ExtraOptionDefinition {

		TableDeleteType(new GenTableExtraOption("gen.tab.del-type", "true", "false")),

		TableEditType(new GenTableExtraOption("gen.tab.edit-type", "editOnDialog", "editOnPage")),

		IsExportExcel(new GenTableExtraOption("gen.tab.export-excel", "true", "false"));

		/** Gen provider extra option of {@link GenTableExtraOption} . */
		@NotNull
		private final GenTableExtraOption option;

		private ExtraOptionDefinition(@NotNull GenTableExtraOption option) {
			notNullOf(option, "option");
			this.option = option.validate();
		}

		public final GenTableExtraOption getOption() {
			return option;
		}

		/**
		 * Gets {@link GenTableExtraOption} by names.
		 * 
		 * @param provider
		 * @return
		 */
		public static List<GenTableExtraOption> getOptions(@Nullable String... names) {
			final List<String> conditions = new ArrayList<>();
			if (!isEmptyArray(names)) {
				conditions.addAll(asList(names));
			}
			return asList(values()).stream().filter(o -> (isEmpty(conditions) || conditions.contains(o.getOption().getName())))
					.map(o -> o.getOption()).collect(toList());
		}

	}

}