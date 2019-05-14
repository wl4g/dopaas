/*
 * Copyright 2015 the original author or authors.
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

import static java.lang.reflect.Modifier.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Java class type processing tool.</br>
 * See: {@link com.wl4g.devops.common.utils.reflect.Types}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年11月10日
 * @since
 */
public abstract class Types {

	/**
	 * Local base non-customized wrap classes.
	 */
	final private static Collection<Class<?>> nativeClasses = new ArrayList<Class<?>>() {
		private static final long serialVersionUID = -4726036260392327337L;
		{
			add(boolean.class);
			add(int.class);
			add(long.class);
			add(double.class);
			add(float.class);
			add(byte.class);
			add(Boolean.class);
			add(String.class);
			add(Integer.class);
			add(Long.class);
			add(Double.class);
			add(Float.class);
			add(Byte.class);
			add(Class.class);
			add(Enum.class);
			add(Date.class);
			add(URI.class);
			add(Locale.class);
		}
	};

	/**
	 * Local base non-customized wrap classes packages.
	 */
	final private static Collection<String> nativePackages = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add("com.sun.");
			add("sun.");
			add("java.");
			add("javax.");
			add("jdk.");
			add("javafx.");
			add("oracle.");
		}
	};

	/**
	 * Is native non-customized wrap classes type?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isBaseType(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return clazz.isPrimitive() || nativeClasses.contains(clazz) || nativePackages.contains(clazz.getName());
	}

	/**
	 * Is general collection classes type?</br>
	 * Contains: Map,Collection,Array
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isGeneralSetType(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}
		return Map.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz) || clazz.isArray();
	}

	/**
	 * Java base type conversion
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertToBase(String value, Class<T> clazz) {
		Object object = null;
		if (isBaseType(clazz)) {
			if (clazz == int.class || clazz == Integer.class) {
				object = Integer.valueOf(value);
			} else if (clazz == long.class || clazz == Long.class) {
				object = Long.valueOf(value);
			} else if (clazz == double.class || clazz == Double.class) {
				object = Double.valueOf(value);
			} else if (clazz == byte.class || clazz == Byte.class) {
				object = Byte.valueOf(value);
			} else if (clazz == String.class) {
				object = new String(value);
			}
		}
		return (T) object;
	}

	/**
	 * Java general collection type conversion
	 * 
	 * @param value
	 * @param fieldClazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object convertToSimpleSet(String value, Class<?> fieldClazz) {
		if (!isGeneralSetType(fieldClazz)) {
			return null;
		}

		// Check general types.(Only support generics as basic types (including
		// String, int, integer, long...))
		Class<?>[] generalTypes = getGeneralTypes(fieldClazz);
		if (generalTypes != null) {
			for (Class<?> gt : generalTypes) {
				if (!isBaseType(gt) && gt != Object.class) {
					throw new IllegalStateException(
							String.format("No support bean class field type: %s, general type: %s", fieldClazz, gt));
				}
			}
		}

		Object object = null;
		try {
			if (Map.class.isAssignableFrom(fieldClazz)) {
				Map map = null;
				if (!isInstantiatable(fieldClazz)) {
					map = (Map) fieldClazz.newInstance();
				} else if (Map.class.isAssignableFrom(fieldClazz)) {
					map = (Map) new HashMap<>();
				}
				if (map == null) {
					throw new IllegalStateException(String.format("No support bean class field type: %s", fieldClazz));
				}

				// See:[com.wl4g.devops.shell.cli.HelpOption.HelpOption.MARK0]
				for (String ele : split(trimToEmpty(value), ",")) {
					if (isNotBlank(ele)) {
						String[] kv = split(trimToEmpty(ele), "=");
						if (kv.length >= 2) {
							map.put(kv[0], kv[1]);
						}
					}
				}
				object = map;
			} else if (fieldClazz.isArray() || Collection.class.isAssignableFrom(fieldClazz)) {
				Collection set = null;
				if (!isInstantiatable(fieldClazz)) {
					set = (Collection) fieldClazz.newInstance();
				} else if (List.class.isAssignableFrom(fieldClazz)) {
					set = (Collection) new ArrayList<>();
				} else if (Set.class.isAssignableFrom(fieldClazz)) {
					set = (Collection) new HashSet<>();
				}
				if (set == null) {
					throw new IllegalStateException(String.format("No support bean class field type: %s", fieldClazz));
				}

				// See:[com.wl4g.devops.shell.cli.HelpOption.HelpOption.MARK0]
				for (String ele : split(trimToEmpty(value), ",")) {
					if (isNotBlank(ele)) {
						set.add(ele);
					}
				}

				if (fieldClazz.isArray()) {
					object = set.toArray();
				} else {
					object = set;
				}
			}

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return object;
	}

	/**
	 * Java base and general collection type conversion
	 * 
	 * @param value
	 * @param fieldClazz
	 * @return
	 */
	public static Object convertToBaseOrSimpleSet(String value, Class<?> fieldClazz) {
		if (isBaseType(fieldClazz)) {
			return convertToBase(value, fieldClazz);
		} else if (isGeneralSetType(fieldClazz)) {
			return convertToSimpleSet(value, fieldClazz);
		}
		return null;
	}

	/**
	 * Is it possible to instantiate classes directly (such as abstract classes,
	 * interfaces can't)
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isInstantiatable(Class<?> clazz) {
		return clazz.isInterface() && isAbstract(clazz.getModifiers());
	}

	/**
	 * Get general types.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?>[] getGeneralTypes(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}

		ParameterizedType paramType = (ParameterizedType) clazz.getGenericSuperclass();
		if (paramType != null) {
			Type[] types = paramType.getActualTypeArguments();
			if (types != null) {
				Class<?>[] cls = new Class<?>[types.length];
				for (int i = 0; i < types.length; i++) {
					cls[i] = (Class<?>) types[i];
				}
				return cls;
			}
		}
		return null;
	}

}