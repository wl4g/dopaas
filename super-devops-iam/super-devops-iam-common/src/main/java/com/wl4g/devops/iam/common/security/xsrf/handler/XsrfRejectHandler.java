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
package com.wl4g.devops.iam.common.security.xsrf.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link XsrfRejectHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月27日
 * @since
 */
public interface XsrfRejectHandler {

	/**
	 * Handles an access denied failure.
	 *
	 * @param request
	 *            that resulted in an <code>AccessDeniedException</code>
	 * @param response
	 *            so that the user agent can be advised of the failure
	 * @param rejectException
	 *            that caused the invocation
	 *
	 * @throws IOException
	 *             in the event of an IOException
	 * @throws ServletException
	 *             in the event of a ServletException
	 */
	void handle(HttpServletRequest request, HttpServletResponse response, XsrfException re) throws IOException, ServletException;

}