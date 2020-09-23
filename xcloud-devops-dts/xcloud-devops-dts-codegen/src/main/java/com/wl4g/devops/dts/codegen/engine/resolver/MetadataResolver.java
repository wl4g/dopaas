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
package com.wl4g.devops.dts.codegen.engine.resolver;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ColumnMetadata;
import com.wl4g.devops.dts.codegen.engine.resolver.TableMetadata.ForeignMetadata;

import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.List;

import javax.validation.constraints.NotBlank;

/**
 * {@link MetadataResolver}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-08
 * @since
 */
public interface MetadataResolver {

	/**
	 * Query tables all info.
	 * 
	 * @param search
	 * @return
	 */
	default List<TableMetadata> findTablesAll() {
		return findTables(EMPTY);
	}

	/**
	 * Query tables info.
	 * 
	 * @param search
	 * @return
	 */
	default List<TableMetadata> findTables(@Nullable String search) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Query table metedata detail description.
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	default TableMetadata findTableDescribe(@NotBlank String tableName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Query table columns info.
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	default List<ColumnMetadata> findTableColumns(@NotBlank String tableName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Query tables foreign
	 * 
	 * @param tableName
	 * @return
	 */
	default List<ForeignMetadata> findTableForeign(@NotBlank String tableName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Query database version.
	 * 
	 * @return
	 * @throws Exception
	 */
	default String findDBVersion() throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Database handler resolver alias.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-09-17
	 * @sine v1.0.0
	 * @see
	 */
	public static interface ResolverAlias {

		public static final String MYSQLV5 = "mysqlv5";
		public static final String ORACLEV11G = "oraclev11g";
		public static final String POSTGRESQLV10 = "postgresqlv10";

		/** List of field values of class {@link ResolverAlias}. */
		public static final String[] VALUES = getFieldValues(ResolverAlias.class, null, "VALUES").toArray(new String[] {});

	}

}