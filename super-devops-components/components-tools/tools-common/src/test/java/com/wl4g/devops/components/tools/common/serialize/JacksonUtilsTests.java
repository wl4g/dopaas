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
package com.wl4g.devops.components.tools.common.serialize;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.Map;

public class JacksonUtilsTests {

	public static void main(String[] args) {
		TestBar bar = new TestBar("myBar");
		TestBean1 bean1 = new TestBean1(1, "jack", singletonMap("foo", toJSONString(bar)));

		String json = toJSONString(bean1);
		System.out.println("Serialization...");
		System.out.println(json);

		System.out.println("Deserialization...");
		System.out.println(parseJSON(json, TestBean1.class));

	}

	public static class TestBean1 {

		private int id;

		private String name;

		private Map<String, String> attributes = new HashMap<>();

		public TestBean1() {
			super();
		}

		public TestBean1(int id, String name, Map<String, String> attributes) {
			super();
			this.id = id;
			this.name = name;
			this.attributes = attributes;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public void setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
		}

		@Override
		public String toString() {
			return "TestBean1 [id=" + id + ", name=" + name + ", attributes=" + attributes + "]";
		}

	}

	public static class TestBar {

		private String barName;

		public TestBar() {
			super();
		}

		public TestBar(String barName) {
			super();
			this.barName = barName;
		}

		public String getBarName() {
			return barName;
		}

		public void setBarName(String barName) {
			this.barName = barName;
		}

	}

}