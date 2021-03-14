package com.wl4g.dopaas.udm.plugin.swagger.config.oas3;

import io.swagger.v3.oas.models.info.License;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Configuring Swagger license.
 */
@Getter
public class Oas3License {

	/**
	 * REQUIRED. The license name used for the API.
	 */
	@Parameter(required = true)
	private String name;

	/**
	 * A URL to the license used for the API. MUST be in the format of a URL.
	 */
	@Parameter
	private String url;

	@Parameter
	private Map<String, Object> extensions = Collections.emptyMap();

	public License createLicenseModel() {
		License license = new License();

		if (name != null) {
			license.setName(name);
		}

		if (url != null) {
			license.setUrl(url);
		}

		license.setExtensions(extensions);

		return license;
	}

}
