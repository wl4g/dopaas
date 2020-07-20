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

import static com.wl4g.devops.components.tools.common.lang.DateUtils2.*;

import java.util.Date;

public class DateUtils2Tests {

	public static void main(String[] args) {
		System.out.println(formatDate(parseDate("2010/3/6")));
		System.out.println(getDate("yyyy年MM月dd日 E"));
		long time = new Date().getTime() - parseDate("2012-11-19").getTime();
		System.out.println(time / (24 * 60 * 60 * 1000));
	}

}