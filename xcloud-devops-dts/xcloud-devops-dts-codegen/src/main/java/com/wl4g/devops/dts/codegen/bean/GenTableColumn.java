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

	private String columnComment;

	private String columnType;

	private Integer columnSort;

	private String attrType;

	private String attrName;

	private String isPk;

	private String noNull;

	private String isInsert;

	private String isUpdate;

	private String isList;

	private String isQuery;

	private String queryType;

	private String isEdit;

	private String showType;

	private String options;

	private String dictType;

	private String rule;

}