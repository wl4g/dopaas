package com.wl4g.devops.doc.plugin.swagger.jaxrs2.model;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariables;

import java.util.Collections;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

public class SwaggerServer {

	/**
	 * REQUIRED. A URL to the target host. This URL supports Server Variables
	 * and MAY be relative, to indicate that the host location is relative to
	 * the location where the OpenAPI document is being served. Variable
	 * substitutions will be made when a variable is named in {brackets}.
	 */
	@Parameter(required = true)
	private String url;

	/**
	 * An optional string describing the host designated by the URL. CommonMark
	 * syntax MAY be used for rich text representation.
	 */
	@Parameter
	private String description;

	/**
	 * A map between a variable name and its value. The value is used for
	 * substitution in the server's URL template.
	 */
	@Parameter
	private Map<String, SwaggerServerVariable> variables = Collections.emptyMap();

	@Parameter
	private Map<String, Object> extensions = Collections.emptyMap();

	public Server createServerModel() {
		Server server = new Server();
		server.setUrl(url);
		server.setDescription(description);

		if (variables != null && !variables.isEmpty()) {
			ServerVariables vs = new ServerVariables();
			variables.entrySet().forEach(v -> vs.addServerVariable(v.getKey(), v.getValue().createServerVariableModel()));
			server.setVariables(vs);
		}

		server.setExtensions(extensions);

		return server;
	}

}
