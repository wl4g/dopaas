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
package com.wl4g.devops.components.tools.common.web;

import java.util.regex.Pattern;

/**
 * Provides static methods for composing URLs.
 * <p>
 * Placed into a separate class for visibility, so that changes to URL
 * formatting conventions will affect all users.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author Ben Alex
 * @version v1.0 2020年4月27日
 * @since
 */
public abstract class UrlUtils extends WebUtils2 {

	/**
	 * Decides if a URL is absolute based on whether it contains a valid scheme
	 * name, as defined in RFC 1738.
	 */
	public static boolean isAbsoluteUrl(String url) {
		if (url == null) {
			return false;
		}
		return ABSOLUTE_URL.matcher(url).matches();
	}

	private static final Pattern ABSOLUTE_URL = Pattern.compile("\\A[a-z0-9.+-]+://.*", Pattern.CASE_INSENSITIVE);

}