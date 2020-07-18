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

import static com.wl4g.devops.components.tools.common.web.WebUtils2.extTopDomainString;
import static java.util.Locale.US;

import java.net.URI;

public class CookieXsrfTokenRepositoryTests {

	public static void main(String[] args) {
		getXsrfTokenCookieNameTest1();
	}

	static void getXsrfTokenCookieNameTest1() {
		// @see: iam-jssdk-core.js#[MARK55]
		String xsrfUri = "http://oss.console.wl4g.com/#/home";
		String host = URI.create(xsrfUri).getHost();
		String topDomain = extTopDomainString(xsrfUri);
		String defaultServName = host;
		int index = host.indexOf(topDomain);
		if (index > 0) {
			defaultServName = host.substring(0, index - 1);
		}
		defaultServName = defaultServName.replace(".", "_").toUpperCase(US);
		String xsrfCookieName = "IAM-" + defaultServName + "-XSRF-TOKEN";
		System.out.println(xsrfCookieName);
	}

}