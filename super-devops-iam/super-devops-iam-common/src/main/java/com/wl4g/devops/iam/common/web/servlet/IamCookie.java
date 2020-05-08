/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.wl4g.devops.iam.common.web.servlet;

import static java.util.Objects.isNull;

import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;

/**
 * {@link IamCookie}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月8日 v1.0.0
 * @see
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
