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

public abstract class Types {

	final private static Collection<Class<?>> NATIVE_CLEASSES = new ArrayList<>();
	final private static Collection<String> NATIVE_PACKAGE = new ArrayList<>();

	static {
		NATIVE_CLEASSES.add(int.class);
		NATIVE_CLEASSES.add(long.class);
		NATIVE_CLEASSES.add(double.class);
		NATIVE_CLEASSES.add(float.class);
		NATIVE_CLEASSES.add(byte.class);
		NATIVE_CLEASSES.add(String.class);
		NATIVE_CLEASSES.add(Integer.class);
		NATIVE_CLEASSES.add(Long.class);
		NATIVE_CLEASSES.add(Double.class);
		NATIVE_CLEASSES.add(Float.class);
		NATIVE_CLEASSES.add(Byte.class);
		NATIVE_CLEASSES.add(Class.class);
		NATIVE_PACKAGE.add("com.sun.");
		NATIVE_PACKAGE.add("sun.");
		NATIVE_PACKAGE.add("java.");
		NATIVE_PACKAGE.add("javax.");
		NATIVE_PACKAGE.add("jdk.");
		NATIVE_PACKAGE.add("javafx.");
		NATIVE_PACKAGE.add("oracle.");
	}

	public static boolean nativeType(Class<?> clazz) {
		return clazz.isPrimitive() || NATIVE_CLEASSES.contains(clazz) || NATIVE_PACKAGE.contains(clazz.getName());
	}

	/**
	 * Java simple type conversion
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T simpleConversion(String value, Class<T> clazz) {
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