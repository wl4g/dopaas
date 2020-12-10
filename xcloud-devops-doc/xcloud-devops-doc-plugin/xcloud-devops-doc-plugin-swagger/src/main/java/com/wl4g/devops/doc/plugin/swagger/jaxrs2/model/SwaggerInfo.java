package com.wl4g.devops.doc.plugin.swagger.jaxrs2.model;

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
