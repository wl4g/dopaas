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