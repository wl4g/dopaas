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
package com.wl4g.devops.common.utils.reflect;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isSynchronized;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import static com.wl4g.devops.common.utils.reflect.Types.*;

/**
 * Enhanced utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * <p>
 * Only intended for internal use.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Costin Leau
 * @author Sam Brannen
 * @author Chris Beams
 * @since 1.2.2 {@link org.springframework.util.ReflectionUtils}
 */
public abstract class ReflectionUtils2 extends ReflectionUtils {

	// --- Extended reflection's. ---

	/**
	 * Assert whether the two types are compatible
	 * 
	 * @return
	 */
	public static boolean isCompatibleType(Class<?> clazz1, Class<?> clazz2) {
		assert (clazz1 != null && clazz2 != null);
		return clazz1.isAssignableFrom(clazz2) || clazz2.isAssignableFrom(clazz1);
	}

	/**
	 * Call a given recursive callback (including all fields of the current
	 * type) for all fields in the target class, and go up into the class
	 * hierarchy to get all declared fields.</br>
	 * Note: It will contain property fields for all parent and superclass
	 * classes
	 * 
	 * @param obj
	 * @param ff
	 * @param fc
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void doWithFullFields(Object obj, FieldFilter2 ff, FieldCallback2 fc)
			throws IllegalArgumentException, IllegalAccessException {
		if (obj == null || ff == null || fc == null) {
			throw new IllegalArgumentException("Object and field filter, field callback must not be null");
		}

		Class<?> cls = obj.getClass();
		do {
			doWithDeepFields(cls, obj, ff, fc);
		} while ((cls = cls.getSuperclass()) != Object.class);
	}

	/**
	 * Call a given recursive callback (including all fields of the current
	 * type) for all fields in the target class, and go up into the class
	 * hierarchy to get all declared fields.</br>
	 * Note: Attribute fields of parent and superclass are not included
	 * 
	 * @param hierarchyClass
	 *            hierarchy Class
	 * @param obj
	 * @param ff
	 * @param fc
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void doWithDeepFields(Class<?> hierarchyClass, Object obj, FieldFilter2 ff, FieldCallback2 fc)
			throws IllegalArgumentException, IllegalAccessException {
		if (hierarchyClass == null || ff == null || fc == null) {
			throw new IllegalArgumentException("Hierarchy class and field filter, field callback must not be null");
		}
		if (obj == null) {
			return;
		}

		// Recursive traversal matching and processing
		for (Field f : hierarchyClass.getDeclaredFields()) {
			f.setAccessible(true);
			Object propertyValue = f.get(obj);

			// Base or general collection type?
			if (isBaseType(f.getType()) || isGeneralSetType(f.getType())) {
				// Filter matching property
				if (ff.match(obj, f, propertyValue)) {
					if (fc != null) {
						fc.doWith(obj, f, propertyValue);
					}
				}
			} else {
				doWithDeepFields(f.getType(), propertyValue, ff, fc);
			}
		}

	}

	/**
	 * Check for accessible general security modifiers.
	 * 
	 * @param mod
	 * @return
	 */
	public static boolean isGenericAccessibleModifier(int mod) {
		return !(isFinal(mod) || isStatic(mod) || isTransient(mod) || isNative(mod) || isVolatile(mod) || isSynchronized(mod));
	}

	/**
	 * Enhanced callback optionally used to filter fields to be operated on by a
	 * field callback.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldFilter2 {

		/**
		 * Filter operations using the given field.
		 * 
		 * @param attach
		 * @param f
		 * @param propertyValue
		 * @return
		 */
		boolean match(Object attach, Field f, Object propertyValue);
	}

	/**
	 * Enhanced callback interface invoked on each field in the hierarchy.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldCallback2 {

		/**
		 * Use the given field copy operation.
		 * 
		 * @param attach
		 * @param f
		 * @param property
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 */
		void doWith(Object attach, Field f, Object property) throws IllegalArgumentException, IllegalAccessException;
	}

}