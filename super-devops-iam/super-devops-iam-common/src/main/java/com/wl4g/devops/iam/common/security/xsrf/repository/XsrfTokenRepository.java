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
package com.wl4g.devops.iam.common.security.xsrf.repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * An API to allow changing the method in which the expected {@link XsrfToken}
 * is associated to the {@link HttpServletRequest}. For example, it may be
 * stored in {@link HttpSession}.
 *
 * @see HttpSessionCsrfTokenRepository
 *
 * @author Rob Winch
 * @since 3.2
 *
 */
public interface XsrfTokenRepository {

	/**
	 * Generates a {@link XsrfToken}
	 *
	 * @param request
	 *            the {@link HttpServletRequest} to use
	 * @return the {@link XsrfToken} that was generated. Cannot be null.
	 */
	XsrfToken generateXToken(HttpServletRequest request);

	/**
	 * Saves the {@link XsrfToken} using the {@link HttpServletRequest} and
	 * {@link HttpServletResponse}. If the {@link XsrfToken} is null, it is the
	 * same as deleting it.
	 *
	 * @param token
	 *            the {@link XsrfToken} to save or null to delete
	 * @param request
	 *            the {@link HttpServletRequest} to use
	 * @param response
	 *            the {@link HttpServletResponse} to use
	 */
	void saveXToken(XsrfToken token, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Loads the expected {@link XsrfToken} from the {@link HttpServletRequest}
	 *
	 * @param request
	 *            the {@link HttpServletRequest} to use
	 * @return the {@link XsrfToken} or null if none exists
	 */
	XsrfToken getXToken(HttpServletRequest request);

}
