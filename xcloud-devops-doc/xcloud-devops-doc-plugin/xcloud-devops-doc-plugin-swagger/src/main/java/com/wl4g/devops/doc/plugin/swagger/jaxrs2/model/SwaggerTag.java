package com.wl4g.devops.doc.plugin.swagger.jaxrs2.model;

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
