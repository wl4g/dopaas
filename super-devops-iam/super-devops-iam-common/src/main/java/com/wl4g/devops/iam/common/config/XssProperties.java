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
package com.wl4g.devops.iam.common.config;

import static java.lang.String.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringEscapeUtils.*;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

/**
 * XSS configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public class XssProperties implements InitializingBean, Serializable {
	final private static long serialVersionUID = -5701992202744439835L;

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * XSS attack solves AOP section expression
	 */
	private String expression;

	/**
	 * Escape translators alias.
	 */
	private List<CharTranslator> escapeTranslators = new ArrayList<>();

	public String getExpression() {
		hasText(expression, format("XSS interception expression is required, and the '%s' configuration item does not exist?",
				KEY_XSS_PREFIX));
		return expression;
	}

	public XssProperties setExpression(String expression) {
		hasText(expression, "expression is emtpy, please check configure");
		this.expression = expression;
		return this;
	}

	public List<CharTranslator> getEscapeTranslators() {
		return escapeTranslators;
	}

	public XssProperties setEscapeTranslators(List<CharTranslator> escapeTranslators) {
		this.escapeTranslators = escapeTranslators;
		return this;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mergeIamInternalEndpointXss();
	}

	/**
	 * Merge IAM XSS security internal configuration.
	 */
	private void mergeIamInternalEndpointXss() {
		/*
		 * [Expect]: In order to solve slight package structure changes.(The
		 * first four levels of package start can be modified at will)
		 *
		 * execution(* com.wl4g.devops.iam.sns.web.*Controller.*(..)) or
		 * execution(* com.wl4g.devops.iam.web.*Controller.*(..)) or ...
		 */
		int basedIamProjectPkgIndex = 4;
		String[] pkgParts = getClass().getName().split("\\.");
		if (pkgParts == null || pkgParts.length <= basedIamProjectPkgIndex) {
			throw new Error(String.format("", basedIamProjectPkgIndex));
		}

		StringBuffer iamBasePkg = new StringBuffer(16);
		// e.g. com.wl4g.devops.iam
		for (int i = 0; i < pkgParts.length; i++) {
			if (i < basedIamProjectPkgIndex) {
				iamBasePkg.append(pkgParts[i]);
				iamBasePkg.append(".");
			} else {
				break;
			}
		}

		// Merge internal XSS expression.(Level names cannot be changed after
		// package)
		StringBuffer _expression = new StringBuffer(128);
		// e.g: com.wl4g.devops.iam.sns.web.DefaultOauth2SnsController
		_expression.append("execution(* ");
		_expression.append(iamBasePkg.toString());
		_expression.append("sns.web.*Controller.*(..))");
		// e.g: com.wl4g.devops.iam.sns.web.DefaultOauth2SnsEndpoint
		_expression.append(" or execution(* ");
		_expression.append(iamBasePkg.toString());
		_expression.append("web.*Endpoint.*(..)) ");
		// e.g: com.wl4g.devops.iam.web.LoginAuthenticatorController
		_expression.append(" or execution(* ");
		_expression.append(iamBasePkg.toString());
		_expression.append("web.*Controller.*(..)) ");
		// e.g: com.wl4g.devops.iam.web.LoginAuthenticatorEndpoint
		_expression.append(" or execution(* ");
		_expression.append(iamBasePkg.toString());
		_expression.append("web.*Endpoint.*(..)) ");

		// Merge extra config expression
		if (!isBlank(expression)) {
			if (expression.toUpperCase(US).startsWith("OR")) {
				_expression.append(getExpression());
			} else {
				_expression.append("or ");
				_expression.append(getExpression());
			}
		}
		setExpression(_expression.toString());

		log.info("After merged the XSS interception expression as: {}", getExpression());
	}

	/**
	 * Chars sequence translators definitions.
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年5月7日
	 * @since
	 */
	public static enum CharTranslator {

		escapeJava(ESCAPE_JAVA),

		escapeEcmascript(ESCAPE_ECMASCRIPT),

		escapeJson(ESCAPE_JSON),

		escapeXml10(ESCAPE_XML10),

		escapeXml11(ESCAPE_XML11),

		escapeHtml3(ESCAPE_HTML3),

		escapeHtml4(ESCAPE_HTML4),

		escapeCsv(ESCAPE_CSV);

		private final CharSequenceTranslator translator;

		private CharTranslator(CharSequenceTranslator translator) {
			notNullOf(translator, "translator");
			this.translator = translator;
		}

		public CharSequenceTranslator getTranslator() {
			return translator;
		}

	}

	final public static String KEY_XSS_PREFIX = "spring.cloud.devops.iam.xss";
}