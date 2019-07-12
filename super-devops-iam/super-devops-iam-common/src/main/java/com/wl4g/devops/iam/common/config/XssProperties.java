/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import java.io.Serializable;

import org.springframework.util.Assert;

/**
 * XSS configuration properties
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public class XssProperties implements Serializable {
	private static final long serialVersionUID = -5701992202744439835L;
	final public static String PREFIX = "spring.web.xss";

	/**
	 * XSS attack solves AOP section expression
	 */
	private String expression;

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		Assert.hasText(expression, "expression is emtpy, please check configure");
		this.expression = expression;
	}

}