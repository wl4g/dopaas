package com.wl4g.devops.iam.common.attacks.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * XSS HttpServlet request wrapper
 * 
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public abstract class XssHttpRequestWrapper extends HttpServletRequestWrapper {

	private HttpServletRequest orig;

	public XssHttpRequestWrapper(HttpServletRequest request) {
		super(request);
		this.orig = request;
	}

	protected abstract <O, I> O _xssEncode(I value);

	public HttpServletRequest getOrigRequest() {
		return orig;
	}

	public static HttpServletRequest getOrigRequest(HttpServletRequest request) {
		if (request instanceof XssHttpRequestWrapper) {
			return ((XssHttpRequestWrapper) request).getOrigRequest();
		}
		return request;
	}

}