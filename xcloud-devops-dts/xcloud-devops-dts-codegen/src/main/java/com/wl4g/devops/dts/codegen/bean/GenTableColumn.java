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

import lombok.Getter;
import lombok.Setter;

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
public class GenTableColumn extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer tableId;

	private String columnName;

	private String columnType;

	private String columnComment;

	private Integer columnSort;

	private String attrType;

	private String attrName;

	private String queryType;

	private String showType;

	private String dictType;

	private String validRule; // form valid rule

	private String isPk;

	private String noNull;

	private String isInsert;

	private String isUpdate;

	private String isList;

	private String isQuery;

	private String isEdit;

}