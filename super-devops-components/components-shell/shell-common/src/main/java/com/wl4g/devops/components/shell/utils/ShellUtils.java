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
package com.wl4g.devops.components.shell.utils;

import com.wl4g.devops.components.shell.annotation.ShellOption;
import com.wl4g.devops.components.tools.common.bean.BeanUtils2;

import static com.wl4g.devops.components.tools.common.reflect.TypeUtils2.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.*;

import java.util.Collection;
import java.util.Map;

/**
 * Shell CLI server support utility tools
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月10日
 * @since
 */
public abstract class ShellUtils extends BeanUtils2 {

	/**
	 * Is true <br/>
	 * 
	 * @param value
	 * @param defaultValue
	 * @return Return TRUE with true/t/y/yes/on/1/enabled
	 */
	public static boolean isTrue(String value, boolean defaultValue) {
		if (isBlank(value)) {
			return defaultValue;
		}
		return (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t") || value.equalsIgnoreCase("1")
				|| value.equalsIgnoreCase("enabled") || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("on"));
	}

	/**
	 * Execute a copy from the source object to the target object. Note that it
	 * will deeply recurse all parent or superclass and application property
	 * fields, and only contain fields annotated with {@link ShellOption}
	 * 
	 * @param dest
	 *            target the target bean
	 * @param src
	 *            the source bean
	 */
	public static <T> void copyOptionsProperties(T dest, T src) {
		copyOptionsProperties(dest, src, (destAttach, tf, sf, srcPropertyValue) -> {
			tf.setAccessible(true);
			Object obj = destAttach;
			Object value = srcPropertyValue;
			if (value == null) { // Using default-value
				ShellOption spt = sf.getAnnotation(ShellOption.class);
				if (spt != null && !isBlank(spt.defaultValue())) {
					value = instantiateWithInitOptionValue(spt.defaultValue(), tf.getType());
				}
			}
			if (obj != null && value != null) {
				tf.set(obj, value);
			}
		});
	}

	/**
	 * Execute a copy from the source object to the target object. Note that it
	 * will deeply recurse all parent or superclass and application property
	 * fields, and only contain fields annotated with {@link ShellOption}
	 * 
	 * @param dest
	 *            target the target bean
	 * @param src
	 *            the source bean
	 * @param fp
	 *            Customizable copyer
	 */
	public static <T> void copyOptionsProperties(T dest, T src, FieldProcessor fp) {
		try {
			deepCopyFieldState(dest, src, targetField -> {
				// [MARK0], See:[AbstractActuator.MARK4]
				return nonNull(targetField.getAnnotation(ShellOption.class)) && DEFAULT_FIELD_FILTER.matches(targetField);
			}, fp);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}

	/**
	 * Java base and general collection type conversion
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 */
	public static <T> T instantiateWithInitOptionValue(String value, Class<T> clazz) {
		if (isSimpleType(clazz)) {
			return instantiateSimpleType(value, clazz);
		} else if (isSimpleCollectionType(clazz)) {
			return instantiateCollectionWithInitOptionValue(value, clazz);
		}
		return null;
	}

	/**
	 * Java general collection type conversion
	 * 
	 * @param initialValue
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T instantiateCollectionWithInitOptionValue(String initialValue, Class<T> clazz) {
		// Instantiate with non default value.
		Object obj = instantiateCollectionType(null, clazz);
		if (isNull(obj)) {
			return null;
		}

		try {
			if (obj instanceof Map) {
				Map map = (Map) obj;

				// Initialize.
				// See:[com.wl4g.devops.shell.cli.HelpOption.HelpOption.MARK0]
				for (String ele : split(trimToEmpty(initialValue), ",")) {
					if (isNotBlank(ele)) {
						String[] kv = split(trimToEmpty(ele), "=");
						if (kv.length >= 2) {
							map.put(kv[0], kv[1]);
						}
					}
				}
			} else {
				if (obj.getClass().isArray()) {

				}
				if (obj instanceof Collection) {
					Collection set = (Collection) obj;

					// Initialize.
					// See:[com.wl4g.devops.shell.cli.HelpOption.HelpOption.MARK0]
					for (String ele : split(trimToEmpty(initialValue), ",")) {
						if (isNotBlank(ele)) {
							set.add(ele);
						}
					}

					if (clazz.isArray()) {
						obj = set.toArray();
					} else {
						obj = set;
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return (T) obj;
	}

}