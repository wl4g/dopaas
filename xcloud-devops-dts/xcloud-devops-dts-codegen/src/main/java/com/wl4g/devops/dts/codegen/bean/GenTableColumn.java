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
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link GenTableColumn}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020-09-08
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
@ToString
public class GenTableColumn extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer tableId;

	@RenderProperty(propertyName = GEN_COLUMN_NAME)
	private String columnName;

	@RenderProperty(propertyName = GEN_COLUMN_TYPE)
	private String columnType;

	@RenderProperty(propertyName = GEN_COLUMN_SIMPLE_TYPE)
	private String simpleColumnType;

	@RenderProperty(propertyName = GEN_COLUMN_SQLTYPE)
	private String sqlType;

	@RenderProperty(propertyName = GEN_COLUMN_COMMENT)
	private String columnComment;

	@RenderProperty(propertyName = GEN_COLUMN_SORT)
	private Integer columnSort;

	/**
	 * The property type of the class or structure of the generated project
	 * source code (for example, the property type of JavaBean or the field type
	 * of the golang structure).
	 * 
	 * @see {@link #attrName}
	 */
	@RenderProperty(propertyName = GEN_COLUMN_ATTRTYPE)
	private String attrType;

	/**
	 * The property name of the class or structure of the generated project
	 * source code (for example, the property name of JavaBean or the field name
	 * of the golang structure).
	 * 
	 * @see {@link #attrType}
	 */
	@RenderProperty(propertyName = GEN_COLUMN_ATTRNAME)
	private String attrName;

	@RenderProperty(propertyName = GEN_COLUMN_QUERYTYPE)
	private String queryType;

	@RenderProperty(propertyName = GEN_COLUMN_SHOWTYPE)
	private String showType;

	@RenderProperty(propertyName = GEN_COLUMN_DICTTYPE)
	private String dictType;

	/**
	 * Front form valid rule.
	 */
	@RenderProperty(propertyName = GEN_COLUMN_VALIDRULE)
	private String validRule;

	@RenderProperty(propertyName = GEN_COLUMN_ISPK)
	private String isPk;

	@RenderProperty(propertyName = GEN_COLUMN_NONULL)
	private String noNull;

	@RenderProperty(propertyName = GEN_COLUMN_ISINSERT)
	private String isInsert;

	@RenderProperty(propertyName = GEN_COLUMN_ISUPDATE)
	private String isUpdate;

	@RenderProperty(propertyName = GEN_COLUMN_ISLIST)
	private String isList;

	@RenderProperty(propertyName = GEN_COLUMN_ISQUERY)
	private String isQuery;

	@RenderProperty(propertyName = GEN_COLUMN_ISEDIT)
	private String isEdit;

}