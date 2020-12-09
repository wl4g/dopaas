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

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Configuring Swagger in compliance with the way the com.github.kongchen
 * Swagger plugin does it.
 */
public class SwaggerConfig {

	/**
	 * REQUIRED. Provides metadata about the API. The metadata MAY be used by
	 * tooling as required.
	 * 
	 * This might be overridden by ReaderListener or SwaggerDefinition
	 * annotation.
	 */
	@Parameter
	private SwaggerInfo info;

	/**
	 * Convenience for reading the informational description from file instead
	 * of embedding it.
	 */
	@Parameter
	private File descriptionFile;

	/**
	 * An array of Server Objects, which provide connectivity information to a
	 * target server. If the servers property is not provided, or is an empty
	 * array, the default value would be a Server Object with a url value of /.
	 */
	@Parameter
	private List<SwaggerServer> servers = Collections.emptyList();

	/**
	 * An element to hold various schemas for the specification.
	 */
	@Parameter
	private SwaggerComponents components;

	/**
	 * A declaration of which security mechanisms can be used across the API.
	 * The list of values includes alternative security requirement objects that
	 * can be used. Only one of the security requirement objects need to be
	 * satisfied to authorize a request. Individual operations can override this
	 * definition.
	 */
	@Parameter
	private List<SwaggerSecurityRequirement> securityRequirements = Collections.emptyList();;

	/**
	 * A list of tags used by the specification with additional metadata. The
	 * order of the tags can be used to reflect on their order by the parsing
	 * tools. Not all tags that are used by the Operation Object must be
	 * declared. The tags that are not declared MAY be organized randomly or
	 * based on the tools' logic. Each tag name in the list MUST be unique.
	 */
	@Parameter
	private List<SwaggerTag> tags = Collections.emptyList();;

	/**
	 * Additional external documentation.
	 */
	@Parameter
	private SwaggerExternalDoc externalDoc;

	/**
	 * Providing extension attributes to the OpenAPI spec.
	 */
	@Parameter
	private Map<String, Object> extensions = Collections.emptyMap();

	public OpenAPI createSwaggerModel() {
		OpenAPI spec = new OpenAPI();

		if (info != null) {
			spec.setInfo(info.createInfoModel());
		}

		if (descriptionFile != null) {
			if (spec.getInfo() == null) {
				spec.setInfo(new Info());
			}
			try {
				spec.getInfo()
						.setDescription(Files.readAllLines(descriptionFile.toPath()).stream().collect(Collectors.joining("\n")));
			} catch (IOException e) {
				throw new RuntimeException("Unable to read descriptor file " + descriptionFile, e);
			}
		}

		if (components != null) {
			spec.setComponents(components.createComponentsModel());
		}

		if (externalDoc != null) {
			spec.setExternalDocs(externalDoc.createExternalDocModel());
		}

		spec.setExtensions(extensions);
		servers.forEach(s -> spec.addServersItem(s.createServerModel()));
		securityRequirements.forEach(s -> spec.addSecurityItem(s.createSecurityModel()));
		tags.forEach(t -> spec.addTagsItem(t.createTagModel()));

		return spec;
	}
}