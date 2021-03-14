/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.udm.service.template;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.isTrue;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

import com.wl4g.component.common.lang.StringUtils2;

/**
 * Rendering template resource wrapper.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author Vjay
 * @version v1.0 2020-09-16
 * @since
 */
public final class TemplateResource implements Serializable {
	private static final long serialVersionUID = 4336198329362479594L;

	final public static String GEN_MD_NAME = "mdPath";

	/**
	 * Raw template resource filename.</br>
	 * for example:
	 * 
	 * <pre>
	 * ....../bean/MyUser.java.ftl
	 * </pre>
	 */
	private final String rawFilename;

	/**
	 * Template resource short filename.</br>
	 * for example:
	 * 
	 * <pre>
	 * ....../bean/MyUser.java.ftl => MyUser.java.ftl
	 * </pre>
	 */
	private final String shortFilename;

	/**
	 * Template resource path name.</br>
	 * for example:
	 * 
	 * <pre>
	 * ....../bean/MyUser.java.ftl => ....../bean/MyUser.java
	 * </pre>
	 */
	private final String pathname;

	/**
	 * Template file content bytes.
	 */
	private final byte[] content;

	/**
	 * Do you want fill rendering?
	 */
	private final boolean isRender;

	/**
	 * Is it necessary to traverse the entity to generate the syntax identifier
	 * of multiple files.
	 */
	private final boolean isForeachMds;

	/**
	 * 'if' directives expression.
	 */
	private final String ifDirectivesExpr;

	/**
	 * Constructor
	 */
	public TemplateResource(@NotBlank String rawFilename, @Nullable byte[] content) {
		this.rawFilename = hasTextOf(rawFilename, "rawFilename");
		this.shortFilename = hasTextOf(StringUtils2.getFilename(rawFilename), "filename");
		this.content = content;
		this.isRender = rawFilename.endsWith(DEFAULT_TPL_EXT);

		// Analysis of special syntax identification.
		/**
		 * {@link com.wl4g.dopaas.udc.codegen.bean.GenTable#moduleName}
		 */
		this.isForeachMds = contains(rawFilename, GEN_MD_NAME); // Modules

		// 'if' directives.
		int pindex1 = rawFilename.indexOf(DIRECTIVE_IF_PREFIX);
		int pindex2 = rawFilename.indexOf(DIRECTIVE_IF_PREFIX);
		int sindex1 = rawFilename.indexOf(DIRECTIVE_IF_SUFFIX);
		int sindex2 = rawFilename.indexOf(DIRECTIVE_IF_SUFFIX);
		isTrue((pindex1 == pindex2 && sindex1 == sindex2),
				"Syntax of illegal directive: %s{ifDirectivesExpr}%s, same template path can only be used once.",
				DIRECTIVE_IF_PREFIX, DIRECTIVE_IF_SUFFIX);

		// Resolving template path name.
		String pathname0 = rawFilename, ifDirectivesExpr0 = null;
		// e.g1: @if-{isEdit}!
		// e.g2: @if-{javaSpecs.isConf(extraOptions,'gen.xx','true')}!
		if (pindex1 > 0 && pindex1 < sindex1) {
			ifDirectivesExpr0 = rawFilename.substring(pindex1 + DIRECTIVE_IF_PREFIX.length(), sindex1);
			pathname0 = new StringBuffer(rawFilename).delete(pindex1, sindex1 + DIRECTIVE_IF_SUFFIX.length()).toString();
		}
		this.pathname = isRender ? pathname0.substring(0, pathname0.length() - DEFAULT_TPL_EXT.length()) : pathname0;
		this.ifDirectivesExpr = ifDirectivesExpr0;
	}

	@NotBlank
	public final String getRawFilename() {
		return rawFilename;
	}

	@NotBlank
	public final String getShortFilename() {
		return shortFilename;
	}

	@NotBlank
	public final String getPathname() {
		return pathname;
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

	public final boolean isForeachMds() {
		return isForeachMds;
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
		hasTextOf(rawFilename, "templatePath");
		hasTextOf(shortFilename, "templateName");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat("pathname: ").concat(rawFilename).concat(", isRender: ")
				.concat(valueOf(isRender)).concat(", isForeachMds: ").concat(valueOf(isForeachMds)).concat(", ifDirectivesExpr: ")
				.concat(valueOf(ifDirectivesExpr));
	}

	/**
	 * Default load template file extension suffix name.
	 */
	public static final String DEFAULT_TPL_EXT = ".ftl";

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