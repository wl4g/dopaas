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

import io.swagger.v3.oas.models.info.Contact;

import java.util.Collections;
import java.util.Map;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Configuring Swagger contact properties.
 */
public class SwaggerContact {

	/**
	 * The identifying name of the contact person/organization.
	 */
	@Parameter
	private String name;

	/**
	 * The URL pointing to the contact information. MUST be in the format of a
	 * URL.
	 */
	@Parameter
	private String url;

	/**
	 * The email address of the contact person/organization. MUST be in the
	 * format of an email address.
	 */
	@Parameter
	private String email;

	@Parameter
	private Map<String, Object> extensions = Collections.emptyMap();

	public Contact createContactModel() {
		Contact contact = new Contact();

		if (name != null) {
			contact.setName(name);
		}

		if (url != null) {
			contact.setUrl(url);
		}

		if (email != null) {
			contact.setEmail(email);
		}

		contact.setExtensions(extensions);

		return contact;
	}
}