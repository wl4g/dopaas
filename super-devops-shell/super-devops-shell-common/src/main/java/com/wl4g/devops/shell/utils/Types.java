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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
	final private static Collection<Class<?>> nativeClasses = new ArrayList<Class<?>>() {
		private static final long serialVersionUID = -4726036260392327337L;
		{
			add(int.class);
			add(long.class);
			add(double.class);
			add(float.class);
			add(byte.class);
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
	 * Is native non-customized wrapp classes type?
	 * 
	 * @param clazz
	 * @return
	 */
	public final static boolean isBaseType(Class<?> clazz) {
		return clazz.isPrimitive() || nativeClasses.contains(clazz) || nativePackages.contains(clazz.getName());
	}

	/**
	 * Is general collection classes type?</br>
	 * Contains: Map,Collection,Array
	 * 
	 * @param clazz
	 * @return
	 */
	public final static boolean isGeneralSetType(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz) || clazz.isArray();
	}

	/**
	 * Java simple type conversion
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static <T> T baseConvert(String value, Class<T> clazz) {
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

}