package com.wl4g.devops.dts.codegen.engine.parse;

import com.wl4g.devops.dts.codegen.bean.GenDatabase;

import java.util.List;

/**
 * {@link MetadataPaser}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-08
 * @since
 */
public interface MetadataPaser {

	default List<String> loadTable(GenDatabase gendb) {
		throw new UnsupportedOperationException();
	}

	default TableMetadata loadTable(GenDatabase gendb, String tabname) {
		throw new UnsupportedOperationException();
	}

	default String convertToJavaType(String sqlType) {
		throw new UnsupportedOperationException();
	}

	default String convertToSqlType(String javaType) {
		throw new UnsupportedOperationException();
	}

	default String queryVersion() {
		throw new UnsupportedOperationException();
	}

	default void loadForeign(String databaseName, String tableName) throws Exception {
		throw new UnsupportedOperationException();
	}

}
