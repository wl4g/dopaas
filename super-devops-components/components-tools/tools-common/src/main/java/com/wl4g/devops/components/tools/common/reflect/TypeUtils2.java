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
package com.wl4g.devops.components.tools.common.reflect;

import static java.lang.reflect.Modifier.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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

import org.apache.commons.lang3.reflect.TypeUtils;

import com.wl4g.devops.components.tools.common.lang.Assert2;

/**
 * Java class type processing tool.</br>
 * See: {@link com.wl4g.devops.components.tools.common.reflect.TypeUtils2}
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年11月10日
 * @since
 */
public abstract class TypeUtils2 extends TypeUtils {

	/**
	 * Local base non-customized wrap classes.
	 */
	final private static Collection<Class<?>> nativeClasses = new ArrayList<Class<?>>(16) {
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
	final private static Collection<String> nativePackages = new ArrayList<String>(16) {
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
	public static boolean isSimpleType(Class<?> clazz) {
		if (isNull(clazz)) {
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
	public static boolean isSimpleCollectionType(Class<?> clazz) {
		if (isNull(clazz)) {
			return false;
		}
		return Map.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz) || clazz.isArray();
	}

	/**
	 * Is it possible to instantiate classes directly (such as abstract classes,
	 * interfaces can't)
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isInstantiatable(Class<?> clazz) {
		return !clazz.isInterface() && !isAbstract(clazz.getModifiers()) && !isNative(clazz.getModifiers());
	}

	/**
	 * Get generic parameter types.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?>[] getGenericParameterTypes(Class<?> clazz) {
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

	/**
	 * Java simple and generic collection type instantiate.
	 * 
	 * @param initialValue
	 * @param clazz
	 * @return
	 */
	public static <T> T instantiate(Object initialValue, Class<T> clazz) {
		if (isSimpleType(clazz)) {
			return instantiateSimpleType(initialValue, clazz);
		} else if (isSimpleCollectionType(clazz)) {
			return instantiateCollectionType(initialValue, clazz);
		}
		return null;
	}

	/**
	 * Java base type conversion
	 * 
	 * @param initialValue
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T instantiateSimpleType(Object initialValue, Class<T> clazz) {
		Assert2.notNull(initialValue, "initial value must can't null");
		Assert2.isTrue(isSimpleType(clazz), String.format("Cannot instantiate, because %s non simple or primitive type.", clazz));

		Object object = null;
		if (int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz)) {
			object = Integer.valueOf(initialValue.toString());
		} else if (long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
			object = Long.valueOf(initialValue.toString());
		} else if (double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
			object = Double.valueOf(initialValue.toString());
		} else if (byte.class.isAssignableFrom(clazz) || Byte.class.isAssignableFrom(clazz)) {
			object = Byte.valueOf(initialValue.toString());
		} else if (boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)) {
			object = Boolean.valueOf(initialValue.toString());
		} else if (String.class.isAssignableFrom(clazz)) {
			object = new String(initialValue.toString());
		} else if (URI.class.isAssignableFrom(clazz)) {
			object = URI.create(initialValue.toString());
		}
		return (T) object;
	}

	/**
	 * Java general collection type conversion
	 * 
	 * @param initialValue
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T instantiateCollectionType(Object initialValue, Class<T> clazz) {
		if (!isSimpleCollectionType(clazz)) {
			return null;
		}

		// Check general types.(Only support generics as basic types (including
		// String, int, integer, long...))
		Class<?>[] generalTypes = getGenericParameterTypes(clazz);
		if (generalTypes != null) {
			for (Class<?> gt : generalTypes) {
				if (!isSimpleType(gt) && gt != Object.class) {
					throw new IllegalStateException(String.format("No support bean class type: %s, general type: %s", clazz, gt));
				}
			}
		}

		Object obj = null;
		try {
			if (Map.class.isAssignableFrom(clazz)) {
				Map map = null;
				if (isInstantiatable(clazz)) {
					map = (Map) clazz.newInstance();
				} else if (Map.class.isAssignableFrom(clazz)) {
					map = (Map) new HashMap<>();
				}
				if (isNull(map)) {
					throw new IllegalStateException(String.format("No support bean class field type: %s", clazz));
				}

				// Initialize.
				if (nonNull(initialValue)) {
					Assert2.isTrue(Map.class.isAssignableFrom(initialValue.getClass()),
							String.format("Illegal initialize type, cannot %s convert to map", initialValue));
					map.putAll((Map) initialValue);
				}
				obj = map;
			} else if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
				Collection set = null;
				if (isInstantiatable(clazz)) {
					set = (Collection) clazz.newInstance();
				} else if (List.class.isAssignableFrom(clazz)) {
					set = (Collection) new ArrayList<>();
				} else if (Set.class.isAssignableFrom(clazz)) {
					set = (Collection) new HashSet<>();
				}
				if (isNull(set)) {
					throw new IllegalStateException(String.format("No support bean class field type: %s", clazz));
				}

				// Initialize.
				if (nonNull(initialValue)) {
					Assert2.isTrue(Collection.class.isAssignableFrom(initialValue.getClass()),
							String.format("Illegal initialize type, cannot %s convert to collection", initialValue));
					set.addAll((Collection) initialValue);
				}
				if (clazz.isArray()) {
					obj = set.toArray();
				} else {
					obj = set;
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return (T) obj;
	}

}