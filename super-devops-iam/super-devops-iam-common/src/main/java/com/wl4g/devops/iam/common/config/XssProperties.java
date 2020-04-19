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
import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.tool.common.log.SmartLogger;

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

/**
 * XSS configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public class XssProperties implements InitializingBean, Serializable {
	final private static long serialVersionUID = -5701992202744439835L;

	final public static String KEY_XSS_PREFIX = "spring.cloud.devops.iam.xss";

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Enable internal protection, which merges expressions of internal
	 * endpoint's.
	 */
	private boolean internalProtect = true;

	/**
	 * XSS attack solves AOP section expression
	 */
	private String expression;

	public boolean isInternalProtect() {
		return internalProtect;
	}

	public void setInternalProtect(boolean enableInternalProtect) {
		this.internalProtect = enableInternalProtect;
	}

	public String getExpression() {
		hasText(expression, format("XSS interception expression is required, and the '%s' configuration item does not exist?",
				KEY_XSS_PREFIX));
		return expression;
	}

	public void setExpression(String expression) {
		hasText(expression, "expression is emtpy, please check configure");
		this.expression = expression;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mergeIamInternalEndpointXssExpr();
	}

	/**
	 * Merge IAM XSS security internal configuration.
	 */
	private void mergeIamInternalEndpointXssExpr() {
		if (!isInternalProtect()) {
			return;
		}

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

		StringBuffer basedIamProjectPkg = new StringBuffer(16);
		// e.g. com.wl4g.devops.iam
		for (int i = 0; i < pkgParts.length; i++) {
			if (i < basedIamProjectPkgIndex) {
				basedIamProjectPkg.append(pkgParts[i]);
				basedIamProjectPkg.append(".");
			} else {
				break;
			}
		}

		// Merge internal XSS expression.(Level names cannot be changed after
		// package)
		StringBuffer expression = new StringBuffer(128);
		expression.append("execution(* ");
		expression.append(basedIamProjectPkg.toString());
		expression.append("sns.web.*Controller.*(..)) or execution(* ");
		expression.append(basedIamProjectPkg.toString());
		expression.append("web.*Controller.*(..)) ");

		if (getExpression().trim().toUpperCase(Locale.ENGLISH).startsWith("OR")) {
			expression.append(getExpression());
		} else {
			expression.append("or ");
			expression.append(getExpression());
		}
		setExpression(expression.toString());

		if (log.isInfoEnabled()) {
			log.info("After merged the XSS interception expression as: {}", getExpression());
		}
	}

}