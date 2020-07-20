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
package com.wl4g.devops.iam.common.web.servlet;

import static java.util.Objects.isNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;

/**
 * {@link IamCookie}.
 * 
 * <p>
 * Chrome 80+ plans to make lax the default. At this point, the site can choose
 * to explicitly turn off the samesite property and set it to none. However, the
 * premise is that the secure property must be set at the same time (cookies can
 * only be sent through HTTPS protocol), otherwise it is invalid.
 * 
 * <pre>
 * Invalid setting:
 * Set-Cookie: MySessionID=abc123; SameSite=None
 * 
 * Valid setting:
 * Set-Cookie: MySessionID=abc123; SameSite=None; Secure
 * </pre>
 * </p>
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月8日 v1.0.0
 * @see <a href=
 *      'https://www.zhihu.com/question/373011996?utm_source=qq'>Chrome80+
 *      SameSite Features</a>.
 */
public class IamCookie extends SimpleCookie {

	public IamCookie() {
		super();
	}

	public IamCookie(String name) {
		super(name);
	}

	public IamCookie(Cookie cookie) {
		super(cookie);
	}

	@Override
	public void saveTo(HttpServletRequest request, HttpServletResponse response) {
		if (!isSecure() && request.isSecure()) {
			setSecure(true);
		}
		super.saveTo(request, response);
	}

	/**
	 * Copy build cookie
	 * 
	 * @param cookie
	 * @return
	 */
	public static IamCookie build(javax.servlet.http.Cookie cookie) {
		if (isNull(cookie)) {
			return null;
		}
		IamCookie _that = new IamCookie();
		_that.setName(cookie.getName());
		_that.setValue(cookie.getValue());
		_that.setComment(cookie.getComment());
		_that.setDomain(cookie.getDomain());
		_that.setPath(cookie.getPath());
		_that.setMaxAge(Math.max(DEFAULT_MAX_AGE, cookie.getMaxAge()));
		_that.setVersion(Math.max(DEFAULT_VERSION, cookie.getVersion()));
		_that.setSecure(cookie.getSecure());
		_that.setHttpOnly(cookie.isHttpOnly());
		return _that;
	}

}