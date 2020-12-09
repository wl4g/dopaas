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
package com.wl4g.devops.doc.plugin.swagger.export.enhance.config;

import java.util.Collections;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

import io.swagger.v3.oas.models.security.SecurityRequirement;

public class SwaggerSecurityRequirement {

	/**
	 * Each name MUST correspond to a security scheme which is declared in the
	 * Security Schemes under the Components Object. If the security scheme is
	 * of type "oauth2" or "openIdConnect", then the value is a list of scope
	 * names required for the execution. For other security scheme types, the
	 * array MUST be empty.
	 */
	@Parameter
	private List<Entry> entries = Collections.emptyList();

	public SecurityRequirement createSecurityModel() {
		if (entries == null || entries.isEmpty()) {
			return null;
		}

		SecurityRequirement securityReq = new SecurityRequirement();
		entries.forEach(e -> securityReq.addList(e.name, e.list));
		return securityReq;
	}

	public static class Entry {

		@Parameter(required = true)
		private String name;

		@Parameter
		private List<String> list = Collections.emptyList();
	}
}