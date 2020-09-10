package com.wl4g.devops.dts.codegen.bean;

import com.wl4g.components.core.bean.BaseBean;

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
public class GenTable extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer databaseId;

	private String tableName;

	private String className;

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

	// Extend
	private List<GenTableColumn> genTableColumns;

}