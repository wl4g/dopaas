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

import java.util.regex.Pattern;

import static java.util.Locale.US;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static com.wl4g.devops.tool.common.lang.StringUtils2.*;

public class StringUtils2Tests {

	public static void main(String[] args) {
		String r = randomAlphanumeric(59).toUpperCase(US);
		System.out.println(r);

		ExpressVo vo = createRandomExpres(1, 10);
		System.out.println(vo.getExpression());
		System.out.println(vo.getResult());

		Object o = 1.0d;
		System.out.println(String.valueOf(o.toString()));
		System.out.println("=============");
		System.out.println(eqIgnCase("1.0", o));
		System.out.println(equalsIgnoreCase("1.0", o.toString()));

		Pattern p = Pattern.compile(
				"^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$|^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
		System.out.println(p.matcher("36060219890331301X").find());

		String msg = "H {\"aaa\":\"bbb\"}";
		System.out.println(unpackingMessage(msg));

		System.out.println("============");
		System.out.println(isURL("https://mp.domain.com/mp/index"));
		System.out.println(isDomain("www.next.mp.domain.com"));
		System.out.println(isDomain("*.mp.domain.com"));
		System.out.println(isDomain("www.*.mp.domain.com"));
		System.out.println(isDomain("www..mp.domain.com"));
		System.out.println(isDomain("www.123-est.domain.com"));
		System.out.println(isDomain("www.123_est.domain.com"));
	}

}
