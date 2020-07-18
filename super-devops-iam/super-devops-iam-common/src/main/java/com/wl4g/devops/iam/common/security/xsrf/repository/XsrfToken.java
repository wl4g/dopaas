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

import java.io.Serializable;

/**
 * XSRF attacks protection token.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月7日
 */
public interface XsrfToken extends Serializable {

	/**
	 * Gets the HTTP header that the CSRF is populated on the response and can
	 * be placed on requests instead of the parameter. Cannot be null.
	 *
	 * @return the HTTP header that the CSRF is populated on the response and
	 *         can be placed on requests instead of the parameter
	 */
	String getXsrfHeaderName();

	/**
	 * Gets the HTTP parameter name that should contain the token. Cannot be
	 * null.
	 * 
	 * @return the HTTP parameter name that should contain the token.
	 */
	String getXsrfParamName();

	/**
	 * Gets the token value. Cannot be null.
	 * 
	 * @return the token value
	 */
	String getXsrfToken();

}