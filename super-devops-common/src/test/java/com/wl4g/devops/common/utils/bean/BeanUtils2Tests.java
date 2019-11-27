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
package com.wl4g.devops.common.utils.bean;

import static com.wl4g.devops.common.utils.bean.BeanUtils2.copyFullProperties;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isSynchronized;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;
import static org.springframework.util.ReflectionUtils.makeAccessible;

import java.lang.reflect.Field;

import com.wl4g.devops.common.utils.bean.BeanUtils2.FieldCopyer;
import com.wl4g.devops.common.utils.bean.BeanUtils2.FieldFilter;

public class BeanUtils2Tests {

	static class TestBaseBean {

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

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		TestRole r = new TestRole(2, "jack");
		r.setId(1);
		r.setName("tom");

		copyFullProperties(r, r, new FieldFilter() {
			@Override
			public boolean match(Field f, Object sourceProperty) {
				Class<?> clazz = f.getType();
				int mod = f.getModifiers();
				return String.class.isAssignableFrom(clazz) && !isFinal(mod) && !isStatic(mod) && !isTransient(mod)
						&& !isNative(mod) && !isVolatile(mod) && !isSynchronized(mod);
			}
		}, new FieldCopyer() {
			@Override
			public void doCopy(Object target, Field tf, Field sf, Object sourcePropertyValue)
					throws IllegalArgumentException, IllegalAccessException {
				if (sourcePropertyValue != null) {
					makeAccessible(tf);
					tf.set(target, sourcePropertyValue);
				}
			}
		});

		System.out.println(toJSONString(r));
	}

}
