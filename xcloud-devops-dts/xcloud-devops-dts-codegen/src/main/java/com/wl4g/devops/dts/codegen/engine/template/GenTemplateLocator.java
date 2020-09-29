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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.lang.StringUtils2.getFilename;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.GEN_MODULE_NAME;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.GEN_TABLE_ENTITY_NAME;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
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
		 * Template file content bytes.
		 */
		private final byte[] content;

		/**
		 * Do you want fill rendering?
		 */
		private final boolean isRender;

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
		 * 'if' directives expression.
		 */
		private final String ifDirectivesExpr;

		/**
		 * Constructor
		 */
		public TemplateResourceWrapper(@NotBlank String pathname, @Nullable byte[] content) {
			hasTextOf(pathname, "pathname");
			this.name = hasTextOf(getFilename(pathname), "name");
			this.content = content;
			this.isRender = name.endsWith(DEFAULT_TPL_SUFFIX);

			// Analysis of special syntax identification.
			/**
			 * {@link com.wl4g.devops.dts.codegen.bean.GenTable#moduleName}
			 */
			this.isForeachModules = contains(pathname, GEN_MODULE_NAME); // Modules
			/**
			 * {@link com.wl4g.devops.dts.codegen.bean.GenTable#entityName}
			 */
			this.isForeachEntitys = contains(pathname, GEN_TABLE_ENTITY_NAME); // Entitys

			// 'if' directives.
			int pindex1 = pathname.indexOf(DIRECTIVE_IF_PREFIX);
			int pindex2 = pathname.indexOf(DIRECTIVE_IF_PREFIX);
			int sindex1 = pathname.indexOf(DIRECTIVE_IF_SUFFIX);
			int sindex2 = pathname.indexOf(DIRECTIVE_IF_SUFFIX);
			isTrue((pindex1 == pindex2 && sindex1 == sindex2),
					"Syntax of illegal directive: %s{ifDirectivesExpr}%s, same template path can only be used once.",
					DIRECTIVE_IF_PREFIX, DIRECTIVE_IF_SUFFIX);

			/**
			 * <pre>
			 * for example1: @if-{isEdit}!
			 * for example2: @if-{javaSpecs.isConf(extraOptions,'gen.xx','true')}!
			 * </pre>
			 */
			if (pindex1 > 0 && pindex1 < sindex1) {
				this.ifDirectivesExpr = pathname.substring(pindex1 + DIRECTIVE_IF_PREFIX.length(), sindex1);
				this.pathname = new StringBuffer(pathname).delete(pindex1, sindex1 + DIRECTIVE_IF_SUFFIX.length()).toString();
			} else {
				this.ifDirectivesExpr = null;
				this.pathname = pathname;
			}
		}

		@NotBlank
		public final String getPathname() {
			return pathname;
		}

		@NotBlank
		public final String getName() {
			return name;
		}

		@Nullable
		public final byte[] getContent() {
			return content;
		}

		@Nullable
		public final String getContentAsString() {
			return isNull(content) ? null : new String(content, UTF_8);
		}

		public final boolean isRender() {
			return isRender;
		}

		public final boolean isForeachEntitys() {
			return isForeachEntitys;
		}

		public final boolean isForeachModules() {
			return isForeachModules;
		}

		/**
		 * Gets 'if' directives expression.
		 * 
		 * @return
		 */
		@Nullable
		public String getIfDirectivesExpr() {
			return ifDirectivesExpr;
		}

		/**
		 * Check if the 'if' directives is used.
		 * 
		 * @return
		 */
		public boolean isIfDirectives() {
			return !isBlank(ifDirectivesExpr);
		}

		/**
		 * Validation
		 */
		public final void validate() {
			hasTextOf(pathname, "templatePath");
			hasTextOf(name, "templateName");
		}

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat("pathname: ").concat(pathname).concat(", isRender: ")
					.concat(valueOf(isRender)).concat(", isForeachEntitys: ").concat(valueOf(isForeachEntitys))
					.concat(", isForeachModules: ")
					.concat(valueOf(isForeachModules).concat(", ifDirectivesExpr: ").concat(valueOf(ifDirectivesExpr)));
		}

	}

	/**
	 * Default load template suffix.
	 */
	public static final String DEFAULT_TPL_SUFFIX = ".ftl";

	/**
	 * Used to dynamically control whether to render and write code files.
	 * 
	 * <p>
	 * for example:
	 * 
	 * <pre>
	 * Template File: src/views/moduleName1/@if-isEdit!#{entityName}Edit.vue
	 * 
	 * <b>Case1: (When expression 'isEdit'==true , entityName=='myentity')</b>
	 * Result: WriteFile(src/views/moduleName1/myentityEdit.vue)</br>
	 * 
	 * <b>Case2: (When expression 'isEdit'==false)</b>
	 * Result: The template is not rendered and output.
	 * 
	 * </pre>
	 * 
	 * </p>
	 */
	public static final String DIRECTIVE_IF_PREFIX = "@if-";
	public static final String DIRECTIVE_IF_SUFFIX = "!";

}
