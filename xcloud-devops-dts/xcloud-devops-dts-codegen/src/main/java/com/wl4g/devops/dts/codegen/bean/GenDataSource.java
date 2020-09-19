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
public class GenDataSource extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private String name;

	private String type;

	private String host;

	private String port;

	private String database;

	private String username;

	private String password;

	private String url;

	public GenDataSource() {
		super();
	}

	public GenDataSource(String name, String type, String host, String port, String database, String username, String password,
			String url) {
		super();
		this.name = name;
		this.type = type;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		this.url = url;
	}

}