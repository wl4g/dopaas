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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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

		Class<?> targetCls = target.getClass(); // [MARK0]
		do {
			copyDeepProperties(targetCls, target, source, ff, fc);
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
	 * @param hierarchyTargetClass
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
	public static void copyDeepProperties(Class<?> hierarchyTargetClass, Object target, Object source, FieldFilter ff,
			FieldCopyer fc) throws IllegalArgumentException, IllegalAccessException {
		if (hierarchyTargetClass == null || ff == null || fc == null) {
			throw new IllegalArgumentException(
					"Hierarchy class and target or source field filter, field copyer must not be null");
		}

		// If the source is empty, skip the current level copy.
		if (isNull(source) || isNull(target)) {
			return;
		}

		// Recursive traversal matching and processing
		Class<?> sourceClass = source.getClass();
		for (Field tf : hierarchyTargetClass.getDeclaredFields()) {
			makeAccessible(tf);
			Object targetPropertyValue = tf.get(target); // See:[MARK0]

			Object sourcePropertyValue = null;
			Field sf = findField(sourceClass, tf.getName());
			if (nonNull(sf)) {
				makeAccessible(sf);
				sourcePropertyValue = sf.get(source);
			}

			// If the source is not null and the target is null, the source can
			// be assigned directly to the target.
			if (isNull(targetPropertyValue)) { // [MARK1]
				if (fc != null) {
					fc.doCopy(target, tf, sf, sourcePropertyValue);
				}
			}
			// Base or general collection type?
			else if (isBaseType(tf.getType()) || isGeneralSetType(tf.getType())) {
				// [MARK2] Filter matching property
				if (sourcePropertyValue != null && ff.match(tf)) {
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
		 * @param targetField
		 *            Target object field.
		 * @return
		 */
		boolean match(Field targetField);
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
	 * Default field filter of {@link FieldFilter}.
	 * @see:{@link com.wl4g.devops.common.utils.reflect.ReflectionUtils2#isGenericAccessibleModifier(int)}
	 */
	final public static FieldFilter DEFAULT_FIELD_FILTER = targetField -> isGenericAccessibleModifier(targetField.getModifiers());

	/**
	 * Default copyer of {@link FieldCopyer}.
	 */
	final public static FieldCopyer DEFAULT_FIELD_COPYER = (targetAttach, tf, sf, sourcePropertyValue) -> {
		if (sourcePropertyValue != null) {
			tf.setAccessible(true);
			tf.set(targetAttach, sourcePropertyValue);
		}
	};

}