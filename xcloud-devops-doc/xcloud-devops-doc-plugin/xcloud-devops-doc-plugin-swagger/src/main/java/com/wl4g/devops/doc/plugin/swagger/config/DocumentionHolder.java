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
package com.wl4g.devops.doc.plugin.swagger.config;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.StringUtils2.trimAllWhitespace;
import static com.wl4g.devops.doc.plugin.swagger.config.DocumentionHolder.DocumentionProvider.*;

import com.wl4g.devops.doc.plugin.swagger.springfox.swagger2.SpringfoxSwagger2Configuration;
import com.wl4g.devops.doc.plugin.swagger.springfox.oas3.SpringfoxOas3Configuration;
import com.wl4g.devops.doc.plugin.swagger.springdoc.swagger2.SpringdocSwagger2Configuration;
import com.wl4g.devops.doc.plugin.swagger.springdoc.oas3.SpringdocOas3Configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * {@link DocumentionHolder}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-10
 * @sine v1.0
 * @see
 */
public class DocumentionHolder {

	private DocumentionProperties config;
	private List<String> resourcePackages = new ArrayList<String>();
	private DocumentionProvider provider = SPRINGFOX_SWAGGER2;

	private DocumentionHolder() {
	}

	public static DocumentionHolder get() {
		return holder;
	}

	public DocumentionProperties getConfig() {
		return config;
	}

	public void setConfig(@NotNull DocumentionProperties config) {
		this.config = notNullOf(config, "config");
	}

	public List<String> getResourcePackages() {
		return this.resourcePackages;
	}

	public void setResourcePackages(List<String> resourcePackages) {
		this.resourcePackages = safeList(resourcePackages).stream().map(s -> trimAllWhitespace(trimToEmpty(s))).collect(toList());
	}

	public DocumentionProvider getProvider() {
		return provider;
	}

	public void setProvider(@NotNull DocumentionProvider provider) {
		this.provider = notNullOf(provider, "provider");
	}

	public static enum DocumentionProvider {
		SPRINGFOX_SWAGGER2(SpringfoxSwagger2Configuration.class),

		SPRINGFOX_OAS3(SpringfoxOas3Configuration.class),

		SPRINGDOC_SWAGGER2(SpringdocSwagger2Configuration.class),

		SPRINGDOC_OAS3(SpringdocOas3Configuration.class),

		JAXRS2_OAS3;

		private final Class<?> autoConfigClass;

		private DocumentionProvider() {
			this(null);
		}

		private DocumentionProvider(@Nullable Class<?> autoConfigClass) {
			this.autoConfigClass = autoConfigClass;
		}

		public Class<?> getAutoConfigClass() {
			return autoConfigClass;
		}

	}

	/**
	 * Maven mojo singleton configuration instance.
	 */
	private static final DocumentionHolder holder = new DocumentionHolder();

}
