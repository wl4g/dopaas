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
package com.wl4g.devops.components.tools.common.resource;

import java.util.Set;

import com.wl4g.devops.components.tools.common.resource.StreamResource;
import com.wl4g.devops.components.tools.common.resource.resolver.ClassPathResourcePatternResolver;

public class PathPatternResourceMatchingResolverTests {

	public static void main(String[] args) throws Exception {
		getPatternResourcesTests();
	}

	public static void getPatternResourcesTests() throws Exception {
		ClassPathResourcePatternResolver resolver = new ClassPathResourcePatternResolver();
		System.out.println("start scanning ...");
		Set<StreamResource> ress = resolver.getResources("classpath*:/com/wl4g/devops/tool/common/resource/**/*.*");
		for (StreamResource r : ress) {
			System.out.println(r);
		}
		System.out.println(ress.size());
	}

}