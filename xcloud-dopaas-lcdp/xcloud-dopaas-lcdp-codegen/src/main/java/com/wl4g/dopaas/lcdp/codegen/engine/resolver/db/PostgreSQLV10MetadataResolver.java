/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.lcdp.codegen.engine.resolver.db;

import static com.wl4g.component.common.lang.Assert2.hasTextOf;

import com.wl4g.dopaas.common.bean.lcdp.GenDataSource;

/**
 * {@link PostgreSQLV10MetadataResolver}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-11
 * @since
 */
public class PostgreSQLV10MetadataResolver extends AbstractDbMetadataResolver {

	/**
	 * New {@link PostgreSQLV10MetadataResolver}
	 * 
	 * @param genDS
	 */
	public PostgreSQLV10MetadataResolver(GenDataSource genDS) {
		this("jdbc:postgresql://".concat(hasTextOf(genDS.getHost(), "dbHost")).concat(":")
				.concat(hasTextOf(genDS.getPort(), "dbPort")).concat("/").concat(hasTextOf(genDS.getDatabase(), "dbName")),
				genDS.getUsername(), genDS.getPassword());
	}

	protected PostgreSQLV10MetadataResolver(String url, String username, String password) {
		super("org.postgresql.Driver", url, username, password);
	}

}