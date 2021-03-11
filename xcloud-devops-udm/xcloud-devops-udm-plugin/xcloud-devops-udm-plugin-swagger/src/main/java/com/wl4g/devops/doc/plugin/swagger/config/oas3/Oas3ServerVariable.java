package com.wl4g.devops.doc.plugin.swagger.config.oas3;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.models.servers.ServerVariable;
import lombok.Getter;

import org.apache.maven.plugins.annotations.Parameter;

@Getter
public class Oas3ServerVariable {

	/**
	 * REQUIRED. The default value to use for substitution, and to send, if an
	 * alternate value is not supplied. Unlike the Schema Object's default, this
	 * value MUST be provided by the consumer.
	 */
	@Parameter(required = true)
	private String defaultValue;

	/**
	 * An enumeration of string values to be used if the substitution options
	 * are from a limited set.
	 */
	@Parameter
	private List<String> enumValues = Collections.emptyList();

	/**
	 * An optional description for the server variable. CommonMark syntax MAY be
	 * used for rich text representation.
	 */
	@Parameter
	private String description;

	@Parameter
	private Map<String, Object> extensions;

	public ServerVariable createServerVariableModel() {
		ServerVariable serverVar = new ServerVariable();

		serverVar.setDefault(defaultValue);
		if (enumValues != null && !enumValues.isEmpty()) {
			serverVar.setEnum(enumValues);
		}
		serverVar.setDescription(description);

		if (extensions != null && !extensions.isEmpty()) {
			serverVar.setExtensions(extensions);
		}

		return serverVar;
	}

	@JsonPropertyOrder({ "description", "default", "enum" })
	public static abstract class ServerVariableMixin {
		@JsonAnyGetter
		public abstract Map<String, Object> getExtensions();
	}

}
