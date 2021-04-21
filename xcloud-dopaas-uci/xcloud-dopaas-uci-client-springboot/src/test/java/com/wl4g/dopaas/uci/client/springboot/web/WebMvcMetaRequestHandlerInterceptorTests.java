/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.client.springboot.web;

import static java.lang.System.out;
import static org.springframework.util.ClassUtils.getDefaultClassLoader;

import org.junit.Test;

/**
 * {@link WebMvcMetaRequestHandlerInterceptorTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-20
 * @sine v1.0
 * @see
 */
public class WebMvcMetaRequestHandlerInterceptorTests {

	WebMvcMetaRequestHandlerInterceptor interceptor = new WebMvcMetaRequestHandlerInterceptor();

	@Test
	public void getMetaFileCase1() {
		out.println(interceptor.determineMetaFile(getDefaultClassLoader().getResource("").getPath()));
		out.println(interceptor.determineMetaFile("/opt/apps/acm/portal-master-bin/portal-master-bin.jar!/BOOT-INF/classes!/"));
		out.println(interceptor.determineMetaFile("/home/myuser/safecloud-web-portal/portal-web/target/classes/"));
		out.println(interceptor.determineMetaFile("/home/myuser/safecloud-web-portal/portal-web/target/test-classes/"));
	}

}
