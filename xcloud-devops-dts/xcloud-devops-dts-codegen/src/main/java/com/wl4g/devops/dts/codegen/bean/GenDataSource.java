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
import lombok.experimental.Wither;

/**
 * {@link GenDataSource}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
@Getter
@Setter
@Wither
@ToString
public class GenDataSource extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	@RenderProperty(propertyName = GEN_DB_NAME)
	private String name;

	@RenderProperty(propertyName = GEN_DB_TYPE)
	private String type;

	@RenderProperty(propertyName = GEN_DB_HOST)
	private String host;

	@RenderProperty(propertyName = GEN_DB_PORT)
	private String port;

	@RenderProperty(propertyName = GEN_DB_DATABAES)
	private String database;

	@RenderProperty(propertyName = GEN_DB_USERNAME)
	private String username;

	@RenderProperty(propertyName = GEN_DB_PASSWORD)
	private String password;

	public GenDataSource() {
		super();
	}

	public GenDataSource(String name, String type, String host, String port, String database, String username, String password) {
		super();
		this.name = name;
		this.type = type;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

}