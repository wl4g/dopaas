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
package com.wl4g.devops.components.tools.common.bean;

import static com.wl4g.devops.components.tools.common.bean.BeanUtils2.DEFAULT_FIELD_FILTER;
import static com.wl4g.devops.components.tools.common.bean.BeanUtils2.deepCopyFieldState;
import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.makeAccessible;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;

public class BeanUtils2Tests {

	public static void main(String[] args) throws Exception {
		test1();
		// test2();
	}

	public static void test1() throws Exception {
		TestRole r = new TestRole(2, "jack");
		r.setId(1);
		r.setName("tom");

		deepCopyFieldState(r, r, (targetField) -> {
			return String.class.isAssignableFrom(targetField.getType()) && DEFAULT_FIELD_FILTER.matches(targetField);
		}, (target, tf, sf, sourcePropertyValue) -> {
			if (sourcePropertyValue != null) {
				makeAccessible(tf);
				tf.set(target, (String) sourcePropertyValue);
			}
		});

		System.out.println(toJSONString(r));
	}

	public static void test2() throws Exception {
		B b1 = new B();
		b1.bb = "22";
		A a1 = new A();
		a1.cc = "33";
		// a1.b = b1;

		B b2 = new B();
		b2.bb = "222";
		A a2 = new A();
		a2.cc = "333";
		a2.b = b2;

		System.out.println(a1);

		deepCopyFieldState(a1, a2, (targetField) -> {
			return true;
		});

		System.out.println(a1);

		System.out.println("=================");

		B b3 = new B();
		b3.bb = "22";
		A a3 = new A();
		a3.cc = "33";
		a3.b = b3;

		C c3 = new C();
		c3.cc = "c33";

		System.out.println(a3);

		deepCopyFieldState(a3, c3, (targetField) -> {
			return true;
		});

		System.out.println(a3);

		System.out.println("=================");
	}

	static class TestBaseBean {
		final public static Integer ENABLE = 1;
		final public static Integer DISABLE = 0;
		final public static String DEFUALT_NAME = "defaultName";

		private Integer id;
		private String name;

		public TestBaseBean() {
			super();
		}

		public TestBaseBean(Integer id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	static class TestRole extends TestBaseBean {
		private Integer id;
		private String subName;

		public TestRole() {
			super();
		}

		public TestRole(Integer id, String subName) {
			super();
			this.id = id;
			this.subName = subName;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getSubName() {
			return subName;
		}

		public void setSubName(String subName) {
			this.subName = subName;
		}

	}

	static class A extends C {
		B b;

		@Override
		public String toString() {
			return "A [b=" + b + ", cc=" + cc + "]";
		}

	}

	static class B {
		String bb;

		@Override
		public String toString() {
			return "B [bb=" + bb + "]";
		}

	}

	static class C {
		String cc;

		@Override
		public String toString() {
			return "C [cc=" + cc + "]";
		}

	}

}