package com.wl4g.devops.iam.common.attacks.xss;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

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
	 * @param value
	 * @return
	 */
	default String doResolve(String value) {
		return HtmlUtils.htmlEscape(value, "UTF-8");
	}

	/**
	 * Newly created XSS secure HttpServletRequestWrapper object
	 * 
	 * @param request
	 * @return
	 */
	default ServletRequestWrapper newXssSecurityHttpRequestWrapper(HttpServletRequest request) {
		return new DefaultXssHttpRequestWrapper(request);
	}

}
