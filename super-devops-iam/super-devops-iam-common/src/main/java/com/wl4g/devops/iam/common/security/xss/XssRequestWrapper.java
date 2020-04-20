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
package com.wl4g.devops.iam.common.security.xss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * XSS HttpServlet request wrapper
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public abstract class XssRequestWrapper extends HttpServletRequestWrapper {

	private HttpServletRequest orig;

	public XssRequestWrapper(HttpServletRequest request) {
		super(request);
		this.orig = request;
	}

	protected abstract <O, I> O _xssEncode(I value);

	public HttpServletRequest getOrigRequest() {
		return orig;
	}

	public static HttpServletRequest getOrigRequest(HttpServletRequest request) {
		if (request instanceof XssRequestWrapper) {
			return ((XssRequestWrapper) request).getOrigRequest();
		}
		return request;
	}

}