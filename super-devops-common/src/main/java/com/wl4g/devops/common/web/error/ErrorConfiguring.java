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
package com.wl4g.devops.common.web.error;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

/**
 * Error configuration adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月1日
 * @since
 */
public interface ErrorConfiguring {

	/**
	 * Obtain exception {@link HttpStatus}
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param ex
	 * @return
	 */
	Integer getStatus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model, Exception ex);

	/**
	 * Obtain exception as string.
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param ex
	 * @return
	 */
	String getRootCause(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model, Exception ex);

}