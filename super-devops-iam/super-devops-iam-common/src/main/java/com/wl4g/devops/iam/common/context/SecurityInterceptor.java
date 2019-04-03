/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.iam.common.context;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * IAM security processing intercept handler
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月18日
 * @since
 */
public interface SecurityInterceptor {

	/**
	 * Pre-handling before authentication, For example, the implementation of
	 * restricting client IP white-list to prevent violent cracking of large
	 * number of submission login requests.
	 * 
	 * @param filter
	 * @param request
	 * @param response
	 * @return
	 */
	boolean preAuthentication(Filter filter, ServletRequest request, ServletResponse response);

}