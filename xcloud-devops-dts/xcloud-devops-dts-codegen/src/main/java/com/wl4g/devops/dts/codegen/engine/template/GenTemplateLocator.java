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
package com.wl4g.devops.dts.codegen.engine.template;

import com.wl4g.components.common.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.StringUtils2.getFilename;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.GEN_MODULE_NAME;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.GEN_TABLE_ENTITY_NAME;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link GenTemplateLocator}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-19
 * @sine v1.0.0
 * @see
 */
public interface GenTemplateLocator {

	/**
	 * Locate generate templates.
	 * 
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	List<TemplateResourceWrapper> locate(String provider) throws Exception;

	/**
	 * Cleanup located templates cache all.
	 * 
	 * @return
	 */
	boolean cleanAll();

	/**
	 * Rendering template resource wrapper.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @author Vjay
	 * @version v1.0 2020-09-16
	 * @since
	 */
	public static final class TemplateResourceWrapper implements Serializable {
		private static final long serialVersionUID = 4336198329362479594L;

		/**
		 * for example: Template class path.
		 */
		private final String pathname;

		/**
		 * Template file name.
		 */
		private final String name;

		/**
		 * Template file content.
		 */
		private final String content;

		/**
		 * Do you want fill rendering?
		 */
		private final boolean isTemplate;

		/**
		 * Is it necessary to traverse the entity to generate the syntax
		 * identifier of multiple files.
		 */
		private final boolean isForeachEntitys;

		/**
		 * Do you need to traverse the module to generate syntax identifiers for
		 * multiple directories.
		 */
		private final boolean isForeachModules;

		/**
		 * Constructor
		 */
		public TemplateResourceWrapper(@NotBlank String pathname, @Nullable String content) {
			hasTextOf(pathname, "pathname");
			this.name = getFilename(pathname);
			this.content = isBlank(content) ? EMPTY : content; // By default
			this.isTemplate = name.endsWith(DEFAULT_TPL_SUFFIX);

			// Analysis of special syntax identification.
			this.pathname = pathname;
			/**
			 * {@link com.wl4g.devops.dts.codegen.bean.GenTable#moduleName}
			 */
			this.isForeachModules = contains(pathname, GEN_MODULE_NAME); // Modules
			/**
			 * {@link com.wl4g.devops.dts.codegen.bean.GenTable#entityName}
			 */
			this.isForeachEntitys = contains(pathname, GEN_TABLE_ENTITY_NAME); // Entitys
		}

		public final String getPathname() {
			return pathname;
		}

		public final String getName() {
			return name;
		}

		public final String getContent() {
			return content;
		}

		public final boolean isTemplate() {
			return isTemplate;
		}

		public final boolean isForeachEntitys() {
			return isForeachEntitys;
		}

		public final boolean isForeachModules() {
			return isForeachModules;
		}

		/**
		 * Validation
		 */
		public final void validate() {
			hasTextOf(pathname, "tplPath");
			hasTextOf(name, "fileName");
			hasTextOf(content, "fileContent");
		}

	}

	/**
	 * Default load template suffix.
	 */
	public static final String DEFAULT_TPL_SUFFIX = ".ftl";

}
