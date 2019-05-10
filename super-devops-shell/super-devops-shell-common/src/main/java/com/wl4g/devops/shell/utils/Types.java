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
package com.wl4g.devops.shell.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Java class type processing tool
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年11月10日
 * @since
 */
public abstract class Types {

	/**
	 * Local base non-customized wrap classes.
	 */
	final private static Collection<Class<?>> nativeClasses = new ArrayList<>();

	/**
	 * Local base non-customized wrap classes packages.
	 */
	final private static Collection<String> nativePackages = new ArrayList<>();

	static {
		nativeClasses.add(int.class);
		nativeClasses.add(long.class);
		nativeClasses.add(double.class);
		nativeClasses.add(float.class);
		nativeClasses.add(byte.class);
		nativeClasses.add(String.class);
		nativeClasses.add(Integer.class);
		nativeClasses.add(Long.class);
		nativeClasses.add(Double.class);
		nativeClasses.add(Float.class);
		nativeClasses.add(Byte.class);
		nativeClasses.add(Class.class);

		nativePackages.add("com.sun.");
		nativePackages.add("sun.");
		nativePackages.add("java.");
		nativePackages.add("javax.");
		nativePackages.add("jdk.");
		nativePackages.add("javafx.");
		nativePackages.add("oracle.");
	}

	/**
	 * Is native non-customized wrapp classes type?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean nativeType(Class<?> clazz) {
		return clazz.isPrimitive() || nativeClasses.contains(clazz) || nativePackages.contains(clazz.getName());
	}

	/**
	 * Java simple type conversion
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T simpleConvert(String value, Class<T> clazz) {
		Object object = null;
		if (nativeType(clazz)) {
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

}