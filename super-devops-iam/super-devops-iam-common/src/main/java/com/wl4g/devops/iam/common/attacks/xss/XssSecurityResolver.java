package com.wl4g.devops.iam.common.attacks.xss;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.util.HtmlUtils;

/**
 * XSS security resolver.
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月29日
 * @since
 */
public interface XssSecurityResolver {

	/**
	 * Perform parsing to convert XSS attack strings to safe strings.
	 * 
	 * @param method
	 *            Current method of parsing XSS
	 * @param index
	 *            Parameter number of the current method for parsing XSS
	 * @param value
	 *            The parameter value of the current method of parsing XSS
	 * @return
	 */
	default String doResolve(final Object controller, final Method method, final int index, final String value) {
		return HtmlUtils.htmlEscape(value, "UTF-8");
	}

	/**
	 * Newly created XSS secure HttpServletRequestWrapper object
	 * 
	 * @param request
	 * @return
	 */
	default HttpServletRequestWrapper newXssHttpRequestWrapper(HttpServletRequest request) {
		return new DefaultXssHttpRequestWrapper(request);
	}

}
