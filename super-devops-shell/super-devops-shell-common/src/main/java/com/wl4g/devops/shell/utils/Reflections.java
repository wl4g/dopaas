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
package com.wl4g.devops.shell.utils;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isNative;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isSynchronized;
import static java.lang.reflect.Modifier.isTransient;
import static java.lang.reflect.Modifier.isVolatile;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.wl4g.devops.shell.utils.Types.*;

/**
 * Enhanced utility class for working with the reflection API and handling
 * reflection exceptions. </br>
 * See:{@link com.wl4g.devops.common.utils.reflect.ReflectionUtils2}
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
public abstract class Reflections {

	private static final Field[] NO_FIELDS = {};

	/**
	 * Cache for {@link Class#getDeclaredFields()}, allowing for fast iteration.
	 */
	private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<Class<?>, Field[]>(256);

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied {@code name}. Searches all superclasses up to
	 * {@link Object}.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the field
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with
	 * the supplied {@code name} and/or {@link Class type}. Searches all
	 * superclasses up to {@link Object}.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @param name
	 *            the name of the field (may be {@code null} if type is
	 *            specified)
	 * @param type
	 *            the type of the field (may be {@code null} if name is
	 *            specified)
	 * @return the corresponding Field object, or {@code null} if not found
	 */
	public static Field findField(Class<?> clazz, String name, Class<?> type) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		if (!(name != null || type != null)) {
			throw new IllegalArgumentException("Either name or type of the field must be specified");
		}
		Class<?> searchType = clazz;
		while (Object.class != searchType && searchType != null) {
			Field[] fields = getDeclaredFields(searchType);
			for (Field field : fields) {
				if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

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
	 * This variant retrieves {@link Class#getDeclaredFields()} from a local
	 * cache in order to avoid the JVM's SecurityManager check and defensive
	 * array copying.
	 * 
	 * @param clazz
	 *            the class to introspect
	 * @return the cached array of fields
	 * @see Class#getDeclaredFields()
	 */
	public static Field[] getDeclaredFields(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Class must not be null");
		}
		Field[] result = declaredFieldsCache.get(clazz);
		if (result == null) {
			result = clazz.getDeclaredFields();
			declaredFieldsCache.put(clazz, (result.length == 0 ? NO_FIELDS : result));
		}
		return result;
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
	public static interface FieldCallback {

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
	public static void doWithFullFields(Object obj, FieldFilter ff, FieldCallback fc)
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
	public static void doWithDeepFields(Class<?> hierarchyClass, Object obj, FieldFilter ff, FieldCallback fc)
			throws IllegalArgumentException, IllegalAccessException {
		if (hierarchyClass == null || ff == null || fc == null) {
			throw new IllegalArgumentException("Hierarchy class and field filter, field callback must not be null");
		}
		if (obj == null) {
			return;
		}

		// Recursive traversal matching and processing
		for (Field f : getDeclaredFields(hierarchyClass)) {
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
	 * Check for generic security identifiers
	 * 
	 * @param mod
	 * @return
	 */
	public static boolean isSafetyModifier(int mod) {
		return !(isFinal(mod) || isStatic(mod) || isTransient(mod) || isNative(mod) || isVolatile(mod) || isSynchronized(mod));
	}

}