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
package com.wl4g.devops.tool.common.web;

import java.net.URI;
import static com.wl4g.devops.tool.common.web.WebUtils2.*;

public class WebUtils2Tests {

	public static void main(String[] args) {
		// URI s =
		// URI.create("http://portal.wl4g.com/portal/authenticator?fragment=eleIndex/elecReport#/eleIndex/index");
		URI s = URI.create(
				"http://portal.wl4g.com/portal/authenticator?redirect_url=http://portal.wl4g.com/?fragment=eleIndex/elecReport#/authLogin");
		System.out.println(s.getScheme());
		System.out.println(s.getHost());
		System.out.println(s.getPort());
		System.out.println(s.getPath());
		System.out.println(s.getQuery());
		System.out.println(s.getFragment());
		System.out.println("-----------------");

		System.out.println(getBaseURIForDefault("http", "my.com", 8080));
		System.out.println(getBaseURIForDefault("http", "my.com", 80));
		System.out.println(getBaseURIForDefault("https", "my.com", 443));
		System.out.println(getBaseURIForDefault("http", "my.com", -1));

		System.out.println(URI.create("http://my.com/index/#/me").getQuery());
		System.out.println(toQueryParams("application=iam-example&gt=aaa&redirect_url=http://my.com/index"));
		System.out.println(toQueryParams("application=iam-example&gt=aaa&redirect_url=http://my.com/index/#/me"));

		System.out.println(extractDomainString("http://*.aaa.anjiancloud.test/API/v2"));

		System.out.println(isSameWithOrigin("http://*.aa.domain.com/API/v2", "http://bb.aa.domain.com/API/v2", true));
		System.out.println(isSameWithOrigin("http://*.aa.domain.com/API/v2", "https://bb.aa.domain.com/API/v2", true));
		System.out.println(isSameWithOrigin("http://*.aa.domain.com/api/v2/", "http://bb.aa.domain.com/API/v2", true));
		System.out.println(isSameWithOrigin("http://bb.*.domain.com", "https://bb.aa.domain.com", false));
		System.out.println(isSameWithOrigin("http://*.aa.domain.com", "https://bb.aa.domain.com", true));
		System.out.println(isSameWithOrigin("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8080/", true));
	}

}
