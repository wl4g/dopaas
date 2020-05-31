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
package com.wl4g.devops.tool.common.collection;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

public class RegisteredSetListTests {

	public static void main(String[] args) {
		List<String> list1 = new RegisteredSetList<>(new ArrayList<>());
		list1.add("a");
		list1.add("A");
		list1.add(null);
		list1.add("b");
		list1.add("b");
		System.out.println("------- all() Remove duplicate tests -------");
		System.out.println("size=" + list1.size());
		list1.forEach(e -> out.println(e));

		List<String> list2 = new ArrayList<>();
		list1.add(null);
		list2.add("C");
		list2.add("C");
		list2.add("D");
		list1.addAll(list2);

		System.out.println("------- addAll() Remove duplicate tests -------");
		System.out.println("size=" + list1.size());
		list1.forEach(e -> out.println(e));

	}

}
