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

import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

import io.swagger.v3.oas.models.tags.Tag;

public class SwaggerTag {

	/**
	 * REQUIRED. The name of the tag.
	 */
	@Parameter(required = true)
	private String name;

	/**
	 * A short description for the tag. CommonMark syntax MAY be used for rich
	 * text representation.
	 */
	@Parameter
	private String description;

	/**
	 * Additional external documentation for this tag.
	 */
	@Parameter
	private SwaggerExternalDoc externalDoc;

	@Parameter
	private Map<String, Object> extensions;

	public Tag createTagModel() {
		Tag tag = new Tag();

		tag.setName(name);
		tag.setDescription(description);

		if (externalDoc != null) {
			tag.setExternalDocs(externalDoc.createExternalDocModel());
		}

		if (extensions != null && !extensions.isEmpty()) {
			tag.setExtensions(extensions);
		}

		return tag;
	}

}