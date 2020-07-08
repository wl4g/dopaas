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
package com.wl4g.devops.components.tools.common.matching;

import com.wl4g.devops.components.tools.common.matching.AntPathMatcher;

public class AntPathMatcherTests {

	public static void main(String[] args) {
		String path1 = "com/wl4g/devops/test1/Test1.class";
		String path2 = "com/wl4g/devops/test2/Test2.class";
		String path3 = "/c://com/wl4g/devops/Test3.java";
		String path4 = "com/wl4g/devops";
		String path5 = "com/wl4g/devops/test5";
		System.out.println("1 => " + new AntPathMatcher("/").match("com/wl4g/devops/test1/*", path1));
		System.out.println("2 => " + new AntPathMatcher("/").match("com/wl4g/devops/**", path2));
		System.out.println("3 => " + new AntPathMatcher("/").match("com/wl4g/devops/**.java", path3));
		System.out.println("4 => " + new AntPathMatcher("/").match("/c:/com/wl4g/devops/*.java", path3));
		System.out.println("5 => " + new AntPathMatcher("/").match("/c:/com/wl4g/devops/**.java", path3));
		System.out.println("6 => " + new AntPathMatcher("/").match("com/wl4g/devops**", path3));
		System.out.println("7 => " + new AntPathMatcher("/").match("com/wl4g/devops**", path4));
		System.out.println("8 => " + new AntPathMatcher("/").match("com/wl4g/devops/*", path5));
		System.out.println("9 => " + new AntPathMatcher("/").match("com/wl4g/devops**", path5));
	}

}