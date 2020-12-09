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

import io.swagger.v3.oas.models.info.Info;
import java.util.Map;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Configuring the Swagger info properties.
 */
public class SwaggerInfo {

	/**
	 * REQUIRED. The title of the application.
	 */
	@Parameter(required = true)
	private String title;

	/**
	 * REQUIRED. The version of the OpenAPI document (which is distinct from the
	 * OpenAPI Specification version or the API implementation version).
	 */
	@Parameter(required = true)
	private String version;

	/**
	 * A short description of the application. CommonMark syntax MAY be used for
	 * rich text representation.
	 */
	@Parameter
	private String description;

	/**
	 * A URL to the Terms of Service for the API. MUST be in the format of a
	 * URL.
	 */
	@Parameter
	private String termsOfService;

	/**
	 * The contact information for the exposed API.
	 */
	@Parameter
	private SwaggerContact contact;

	/**
	 * The license information for the exposed API.
	 */
	@Parameter
	private SwaggerLicense license;

	@Parameter
	private Map<String, Object> extensions;

	public Info createInfoModel() {
		Info info = new Info();

		if (title != null) {
			info.setTitle(title);
		}

		if (version != null) {
			info.setVersion(version);
		}

		if (description != null) {
			info.setDescription(description);
		}

		if (termsOfService != null) {
			info.setTermsOfService(termsOfService);
		}

		if (contact != null) {
			info.setContact(contact.createContactModel());
		}

		if (license != null) {
			info.setLicense(license.createLicenseModel());
		}

		if (extensions != null && !extensions.isEmpty()) {
			info.setExtensions(extensions);
		}

		return info;
	}

}