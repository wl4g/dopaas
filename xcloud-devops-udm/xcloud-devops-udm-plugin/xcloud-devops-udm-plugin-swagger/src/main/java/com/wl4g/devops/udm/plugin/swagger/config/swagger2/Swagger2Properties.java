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
package com.wl4g.devops.udm.plugin.swagger.config.swagger2;

import com.wl4g.devops.udm.plugin.swagger.config.SwaggerConfig;

import io.swagger.models.Contact;
import io.swagger.models.Info;
import io.swagger.models.License;
import io.swagger.models.Swagger;
import lombok.Getter;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * {@link Swagger2Properties}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-14
 * @sine v1.0
 * @see
 */
@Getter
public class Swagger2Properties extends Swagger implements SwaggerConfig {

	/**
	 * REQUIRED. The apis group name.
	 */
	private String groupName = Docket.DEFAULT_GROUP_NAME;

	public Swagger2Properties() {
		this.setInfo(new Info().contact(new Contact()).license(new License()));
	}

	@Override
	public String getSwaggerGroup() {
		return getGroupName();
	}

}
