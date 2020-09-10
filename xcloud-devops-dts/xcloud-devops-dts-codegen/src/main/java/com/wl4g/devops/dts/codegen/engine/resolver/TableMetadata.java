package com.wl4g.devops.dts.codegen.engine.resolver;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

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

	// 表的名称
	private String tableName;
	// 表的备注
	private String comments;
	// 表的主键
	private ColumnMetadata pk;

	// 表的字段
	private List<ColumnMetadata> columns;

	// 类名(第一个字母大写)，如：sys_user => SysUser
	private String classNameFirstUpper;
	// 类名(第一个字母小写)，如：sys_user => sysUser
	private String classNameFirstLower;

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
		private String dataType;
		private String columnType;
		// 列名备注
		private String comments;
		// 属性名称(第一个字母大写)，如：user_name => UserName
		private String attrNameFirstUpper;
		// 属性名称(第一个字母小写)，如：user_name => userName
		private String attrNameFirstLower;
		// 属性类型
		private String attrType;
		// auto_increment
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
