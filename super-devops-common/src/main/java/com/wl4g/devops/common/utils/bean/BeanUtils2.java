/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import java.lang.reflect.Field;

import static com.wl4g.devops.common.utils.reflect.ReflectionUtils2.*;
import static com.wl4g.devops.common.utils.reflect.Types.*;

/**
 * Enhanced static convenience methods for JavaBeans: for instantiating beans,
 * checking bean property types, copying bean properties, etc. </br>
 * Enhanced for: {@link org.springframework.beans.BeanUtils}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月10日
 * @since
 */
public abstract class BeanUtils2 {

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * It will contain all the fields defined by all parent or superclasses. At
	 * the same time, the target and the source object must be compatible.
	 * 
	 * @param target
	 *            The target object to copy to
	 * @param source
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void copyFullProperties(Object target, Object source, FieldFilter ff)
			throws IllegalArgumentException, IllegalAccessException {
		copyFullProperties(target, source, ff, DEFAULT_FIELD_COPYER);
	}

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * It will contain all the fields defined by all parent or superclasses. At
	 * the same time, the target and the source object must be compatible.
	 * 
	 * @param target
	 *            The target object to copy to
	 * @param source
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @param fc
	 *            Customizable copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void copyFullProperties(Object target, Object source, FieldFilter ff, FieldCopyer fc)
			throws IllegalArgumentException, IllegalAccessException {
		if (!(target != null && source != null && ff != null && fc != null)) {
			throw new IllegalArgumentException("Target and source field filter, field copyer must not be null");
		}

		// Check if the target is compatible with the source object
		Class<?> targetClass = target.getClass(), sourceClass = source.getClass();
		if (!isCompatibleType(target.getClass(), source.getClass())) {
			throw new IllegalArgumentException(
					String.format("Incompatible two objects, target class: %s, source class: %s", targetClass, sourceClass));
		}

		Class<?> cls = target.getClass(); // [MARK0]
		do {
			copyDeepProperties(cls, target, source, ff, fc);
		} while ((cls = cls.getSuperclass()) != Object.class);
	}

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * Note: that it does not contain fields defined by the parent or super
	 * class. At the same time, the target and the source object must be
	 * compatible.</br>
	 * Note: Attribute fields of parent and superclass are not included
	 * 
	 * @param hierarchyClass
	 *            The level of the class currently copied to (upward recursion)
	 * @param target
	 *            The target object to copy to
	 * @param source
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @param fc
	 *            Customizable copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void copyDeepProperties(Class<?> hierarchyClass, Object target, Object source, FieldFilter ff, FieldCopyer fc)
			throws IllegalArgumentException, IllegalAccessException {
		if (hierarchyClass == null || ff == null || fc == null) {
			throw new IllegalArgumentException(
					"Hierarchy class and target or source field filter, field copyer must not be null");
		}

		// If the source is empty, skip the current level copy.
		if (source == null || target == null) {
			return;
		}

		// Recursive traversal matching and processing
		Class<?> sourceClass = source.getClass();
		for (Field tf : hierarchyClass.getDeclaredFields()) {
			tf.setAccessible(true);
			Object targetPropertyValue = tf.get(target); // See:[MARK0]

			Object sourcePropertyValue = null;
			Field sf = findField(sourceClass, tf.getName());
			if (sf != null) {
				sf.setAccessible(true);
				sourcePropertyValue = sf.get(source);
			}

			// If the source is not null and the target is null, the source can
			// be assigned directly to the target.
			if (targetPropertyValue == null) { // [MARK1]
				if (fc != null) {
					fc.doCopy(target, tf, sf, sourcePropertyValue);
				}
			}
			// Base or general collection type?
			else if (isBaseType(tf.getType()) || isGeneralSetType(tf.getType())) {
				// [MARK2] Filter matching property
				if (sourcePropertyValue != null && ff.match(tf, sourcePropertyValue)) {
					if (fc != null) {
						fc.doCopy(target, tf, sf, sourcePropertyValue);
					}
				}
			} else {
				copyDeepProperties(tf.getType(), targetPropertyValue, sourcePropertyValue, ff, fc);
			}
		}

	}

	/**
	 * Enhanced callback optionally used to filter fields to be operated on by a
	 * field callback.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldFilter {

		/**
		 * Filter operations using the given field.
		 * 
		 * @param f
		 * @param sourcePropertyValue
		 *            The value of the attributes of the source bean. </br>
		 *            See:[MARK1|MARK2]
		 * @return
		 */
		boolean match(Field f, Object sourcePropertyValue);
	}

	/**
	 * Enhanced callback interface invoked on each field in the hierarchy.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldCopyer {

		/**
		 * Use the given field copy operation.
		 * 
		 * @param targetAttach
		 * @param tf
		 * @param sf
		 * @param sourcePropertyValue
		 *            The value of the attributes of the source bean
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 */
		void doCopy(Object targetAttach, Field tf, Field sf, Object sourcePropertyValue)
				throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Default field filter.
	 */
	final public static FieldFilter DEFAULT_FIELD_FILTER = new FieldFilter() {
		@Override
		public boolean match(Field f, Object sourcePropertyValue) {
			return true;
		}
	};

	/**
	 * Default field copyer.
	 */
	final public static FieldCopyer DEFAULT_FIELD_COPYER = new FieldCopyer() {
		@Override
		public void doCopy(Object targetAttach, Field tf, Field sf, Object sourcePropertyValue)
				throws IllegalArgumentException, IllegalAccessException {
			if (sourcePropertyValue != null) {
				tf.setAccessible(true);
				tf.set(targetAttach, sourcePropertyValue);
			}
		}
	};

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
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

		copyFullProperties(a1, a2, new FieldFilter() {
			@Override
			public boolean match(Field f, Object sourcePropertyValue) {
				return true;
			}
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

		copyFullProperties(a3, c3, new FieldFilter() {
			@Override
			public boolean match(Field f, Object sourcePropertyValue) {
				return true;
			}
		});

		System.out.println(a3);

		System.out.println("=================");
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