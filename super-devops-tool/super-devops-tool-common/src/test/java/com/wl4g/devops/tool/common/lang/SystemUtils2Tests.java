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
package com.wl4g.devops.tool.common.lang;

public class SystemUtils2Tests {

	public static void main(String[] args) {
		System.out.println(SystemUtils2.DEFAULT_GLOBAL_HOST_SERIAL);
		System.out.println(SystemUtils2.GLOBAL_APP_SERIAL);
		System.out.println(SystemUtils2.LOCAL_PROCESS_ID);
		System.out.println(SystemUtils2.GLOBAL_PROCESS_SERIAL);
		System.out.println(SystemUtils2.cleanSystemPath("E:\\dir\\"));
		System.out.println(SystemUtils2.cleanSystemPath("E:\\log\\a.log\\"));
		System.out.println(SystemUtils2.cleanSystemPath("/var/log//a.log/"));
	}

}