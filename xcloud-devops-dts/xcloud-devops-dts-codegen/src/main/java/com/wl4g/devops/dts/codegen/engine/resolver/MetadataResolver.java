package com.wl4g.devops.dts.codegen.engine.resolver;

import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ForeignMetadata;

import java.util.List;

/**
 * {@link MetadataResolver}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-08
 * @since
 */
public interface MetadataResolver {

	default List<String> findTables() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Query table metedata detail description.
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	default TableMetadata findTableDescribe(String tableName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Query table columns info.
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	default List<ColumnMetadata> findTableColumns(String tableName) {
		throw new UnsupportedOperationException();
	}

	default List<ForeignMetadata> findTableForeign(String tableName) {
		throw new UnsupportedOperationException();
	}

	default String findDBVersion() throws Exception {
		throw new UnsupportedOperationException();
	}

}
