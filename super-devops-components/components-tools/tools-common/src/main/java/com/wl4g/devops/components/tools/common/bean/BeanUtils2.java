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

import java.lang.reflect.Field;

import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.*;
import static com.wl4g.devops.components.tools.common.reflect.TypeUtils2.isSimpleCollectionType;
import static com.wl4g.devops.components.tools.common.reflect.TypeUtils2.isSimpleType;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
	 * @param dest
	 *            The target object to copy to
	 * @param src
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void deepCopyFieldState(Object dest, Object src, FieldFilter ff)
			throws IllegalArgumentException, IllegalAccessException {
		deepCopyFieldState(dest, src, ff, DEFAULT_FIELD_COPYER);
	}

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * It will contain all the fields defined by all parent or superclasses. At
	 * the same time, the target and the source object must be compatible.
	 *
	 * @param dest
	 *            The target object to copy to
	 * @param src
	 *            Source object
	 * @param fc
	 *            Field copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void deepCopyFieldState(Object dest, Object src, FieldProcessor fc)
			throws IllegalArgumentException, IllegalAccessException {
		deepCopyFieldState(dest, src, DEFAULT_FIELD_FILTER, fc);
	}

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * It will contain all the fields defined by all parent or superclasses. At
	 * the same time, the target and the source object must be compatible.
	 * 
	 * @param dest
	 *            The target object to copy to
	 * @param src
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @param fp
	 *            Customizable copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void deepCopyFieldState(Object dest, Object src, FieldFilter ff, FieldProcessor fp)
			throws IllegalArgumentException, IllegalAccessException {
		if (!(dest != null && src != null && ff != null && fp != null)) {
			throw new IllegalArgumentException("Target and source FieldFilter and FieldProcessor must not be null");
		}

		// Check if the target is compatible with the source object
		Class<?> targetClass = dest.getClass(), sourceClass = src.getClass();
		if (!isCompatibleType(dest.getClass(), src.getClass())) {
			throw new IllegalArgumentException(
					String.format("Incompatible two objects, target class: %s, source class: %s", targetClass, sourceClass));
		}

		Class<?> targetCls = dest.getClass(); // [MARK0]
		do {
			doDeepCopyFields(targetCls, dest, src, ff, fp);
		} while ((targetCls = targetCls.getSuperclass()) != Object.class);
	}

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * Note: that it does not contain fields defined by the parent or super
	 * class. At the same time, the target and the source object must be
	 * compatible.</br>
	 * Note: Attribute fields of parent and superclass are not included
	 * 
	 * @param hierarchyDestClass
	 *            The level of the class currently copied to (upward recursion)
	 * @param dest
	 *            The target object to copy to
	 * @param src
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @param fp
	 *            Customizable copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void doDeepCopyFields(Class<?> hierarchyDestClass, Object dest, Object src, FieldFilter ff, FieldProcessor fp)
			throws IllegalArgumentException, IllegalAccessException {
		if (isNull(hierarchyDestClass) || isNull(ff) || isNull(fp)) {
			throw new IllegalArgumentException("Hierarchy target class or source FieldFilter and FieldProcessor can't null");
		}

		// Skip the current level copy.
		if (isNull(src) || isNull(dest)) {
			return;
		}

		// Recursive traversal matching and processing
		Class<?> sourceClass = src.getClass();
		for (Field tf : hierarchyDestClass.getDeclaredFields()) {
			makeAccessible(tf);
			Object targetPropertyValue = tf.get(dest); // See:[MARK0]

			Object sourcePropertyValue = null;
			Field sf = findField(sourceClass, tf.getName());
			if (nonNull(sf)) {
				makeAccessible(sf);
				sourcePropertyValue = sf.get(src);
			}

			// Base or general collection type?
			if (isSimpleType(tf.getType()) || isSimpleCollectionType(tf.getType())) {
				// [MARK2] Filter matching property
				if (nonNull(fp) && ff.matches(tf) && nonNull(sourcePropertyValue)) {
					fp.doProcess(dest, tf, sf, sourcePropertyValue);
				}
			} else {
				doDeepCopyFields(tf.getType(), targetPropertyValue, sourcePropertyValue, ff, fp);
			}
		}

	}

	/**
	 * Enhanced callback interface invoked on each field in the hierarchy.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldProcessor {

		/**
		 * Use the given field processing(for example: copying).
		 * 
		 * @param target
		 * @param tf
		 * @param sf
		 * @param sourcePropertyValue
		 *            The value of the attributes of the source bean
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 */
		void doProcess(Object target, Field tf, Field sf, Object sourcePropertyValue)
				throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Default field filter of {@link FieldFilter}.
	 * @see:{@link com.wl4g.devops.common.utils.reflect.ReflectionUtils2#isGenericAccessibleModifier(int)}
	 */
	final public static FieldFilter DEFAULT_FIELD_FILTER = targetField -> isGenericModifier(targetField.getModifiers());

	/**
	 * Default copyer of {@link FieldProcessor}.
	 */
	final public static FieldProcessor DEFAULT_FIELD_COPYER = (target, tf, sf, sourcePropertyValue) -> {
		if (sourcePropertyValue != null) {
			tf.setAccessible(true);
			tf.set(target, sourcePropertyValue);
		}
	};

}