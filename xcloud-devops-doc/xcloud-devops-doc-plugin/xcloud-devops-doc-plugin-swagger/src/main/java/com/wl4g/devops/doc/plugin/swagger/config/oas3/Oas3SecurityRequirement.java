package com.wl4g.devops.doc.plugin.swagger.config.oas3;

import java.util.Collections;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.Getter;

@Getter
public class Oas3SecurityRequirement {

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
