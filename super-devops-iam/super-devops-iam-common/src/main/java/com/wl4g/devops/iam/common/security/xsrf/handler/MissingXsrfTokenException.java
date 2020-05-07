/*
 * Copyright 2002-2013 the original author or authors.
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

import com.wl4g.devops.iam.common.security.xsrf.repository.XsrfToken;

/**
 * Thrown when no expected {@link XsrfToken} is found but is required.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月27日
 * @since
 */
public class MissingXsrfTokenException extends XsrfException {
	private static final long serialVersionUID = 5710085585010069884L;

	public MissingXsrfTokenException(String actualToken) {
		super("Could not verify the provided XSRF token because your session was not found.");
	}

}