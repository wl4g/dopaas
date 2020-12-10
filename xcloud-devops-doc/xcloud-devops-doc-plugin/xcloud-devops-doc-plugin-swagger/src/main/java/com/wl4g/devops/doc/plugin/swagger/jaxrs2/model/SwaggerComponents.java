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
package com.wl4g.devops.doc.plugin.swagger.jaxrs2.model;

import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

import io.swagger.v3.oas.models.Components;

public class SwaggerComponents {

	/**
	 * Security schemes (under Comtonents)
	 */
	@Parameter
	private Map<String, SwaggerSecurityScheme> securitySchemes;

	// TODO: implement schemas, responses, ... from
	// https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#componentsObject

	public Components createComponentsModel() {
		Components components = new Components();

		if (securitySchemes != null && !securitySchemes.isEmpty()) {
			securitySchemes.entrySet()
					.forEach(s -> components.addSecuritySchemes(s.getKey(), s.getValue().createSecuritySchemaModel()));
		}

		return components;
	}

}