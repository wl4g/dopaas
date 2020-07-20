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
package com.wl4g.devops.common.utils.web;

import javax.servlet.http.HttpServletRequest;

import static java.util.Objects.nonNull;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.wl4g.devops.components.tools.common.web.WebUtils2;

/**
 * {@link WebUtils3}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月21日
 * @since
 */
public class WebUtils3 extends WebUtils2 {

	/**
	 * Gets request parameter.
	 * 
	 * @param name
	 * @return
	 */
	public static String getRequestParameter(String name) {
		ServletRequestAttributes attr = (ServletRequestAttributes) getRequestAttributes();
		if (nonNull(attr)) {
			HttpServletRequest request = attr.getRequest();
			return nonNull(request) ? request.getParameter(name) : null;
		}
		return null;
	}

	public static void getRequestParameter() {
	}
}