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
package com.wl4g.devops.components.tools.common.lang;

import com.wl4g.devops.components.tools.common.lang.period.JodaPeriodFormatter;
import com.wl4g.devops.components.tools.common.lang.period.PeriodFormatterHolder;

/**
 * {@link JodaPeriodUtilsTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月16日
 * @since
 */
public class JodaPeriodUtilsTests {

	public static void main(String[] args) {
		calcMyBirthDateTest1();
	}

	public static void calcMyBirthDateTest1() {
		PeriodFormatterHolder formatter = PeriodFormatterHolder.getInstance(JodaPeriodFormatter.class);

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime0 = 266835082000L;
		// Pretend the my birth date time is: 1978-06-16 16:51:11
		long myBirthDate0 = 266835071000L;
		System.out.println(formatter.formatHumanDate(nowTime0, myBirthDate0));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:52
		long nowTime1 = 266835112000L;
		// Pretend the my birth date time is: 1978-06-16 16:51:11
		long myBirthDate1 = 266835071000L;
		System.out.println(formatter.formatHumanDate(nowTime1, myBirthDate1));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime2 = 266835082000L;
		// Pretend the my birth date time is: 1978-06-16 16:48:22
		long myBirthDate2 = 266834902000L;
		System.out.println(formatter.formatHumanDate(nowTime2, myBirthDate2));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime3 = 266835082000L;
		// Pretend the my birth date time is: 1978-06-16 14:48:22
		long myBirthDate3 = 266827702000L;
		System.out.println(formatter.formatHumanDate(nowTime3, myBirthDate3));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime4 = 266835082000L;
		// Pretend the my birth date time is: 1978-06-12 16:51:22
		long myBirthDate4 = 266489482000L;
		System.out.println(formatter.formatHumanDate(nowTime4, myBirthDate4));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime5 = 266835082000L;
		// Pretend the my birth date time is: 1978-03-16 16:51:22
		long myBirthDate5 = 258886282000L;
		System.out.println(formatter.formatHumanDate(nowTime5, myBirthDate5));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime6 = 266835082000L;
		// Pretend the my birth date time is: 1976-05-16 16:51:22
		long myBirthDate6 = 201084682000L;
		System.out.println(formatter.formatHumanDate(nowTime6, myBirthDate6));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime7 = 266835082000L;
		// Pretend the my birth date time is: 1976-01-16 16:51:22
		long myBirthDate7 = 190630282000L;
		System.out.println(formatter.formatHumanDate(nowTime7, myBirthDate7));

		System.out.println("-------------------------------------------------------");

		// Pretend the current time is: 1978-06-16 16:51:22
		long nowTime8 = 266835082000L;
		// Pretend the my birth date time is: 1977-01-16 16:51:22
		long myBirthDate8 = 222252682000L;
		System.out.println(formatter.formatHumanDate(nowTime8, myBirthDate8));

	}

}