package com.wl4g.devops.doc.plugin.swagger.jaxrs2.model;

import java.util.Collections;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

import io.swagger.v3.oas.models.ExternalDocumentation;

public class SwaggerExternalDoc {

	/**
	 * A short description of the target documentation. CommonMark syntax MAY be
	 * used for rich text representation.
	 */
	@Parameter
	private String description;

	/**
	 * REQUIRED. The URL for the target documentation. Value MUST be in the
	 * format of a URL.
	 */
	@Parameter(required = true)
	private String url;

	@Parameter
	private Map<String, Object> extensions = Collections.emptyMap();

	public ExternalDocumentation createExternalDocModel() {
		ExternalDocumentation externalDoc = new ExternalDocumentation();

		if (description != null) {
			externalDoc.setDescription(description);
		}

		if (url != null) {
			externalDoc.setUrl(url);
		}

		externalDoc.setExtensions(extensions);

		return externalDoc;
	}

}
