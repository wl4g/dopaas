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

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.cli.Option;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.wl4g.devops.shell.annotation.ShellOption;

import static com.wl4g.devops.shell.utils.Types.*;

/**
 * Reflections
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class Reflections {

	/**
	 * Extract deep full property to map.
	 * 
	 * @param clazz
	 * @param paramInfoMap
	 */
	public static void extFullParams(Class<?> clazz, Map<Option, String> paramInfoMap) {
		Class<?> cls = clazz;
		do {
			extFlatParams(cls, paramInfoMap);
		} while ((cls = cls.getSuperclass()) != null);
	}

	/**
	 * Extract flat class property to map
	 * 
	 * @param clazz
	 * @param attributes
	 */
	public static void extFlatParams(Class<?> clazz, Map<Option, String> attributes) {
		Assert.notNull(clazz, "The paramClazz must be null");
		try {
			for (Field f : clazz.getDeclaredFields()) {
				String fname = f.getName();
				ShellOption sp = f.getAnnotation(ShellOption.class);

				// Filter class property
				if (!fname.equals("class") && sp != null) {
					if (nativeType(f.getType())) {
						Option option = new Option(sp.opt(), sp.lopt(), isBlank(sp.defaultValue()), sp.help());
						attributes.put(option, fname);
					} else {
						extFlatParams(f.getType(), attributes);
					}
				}
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

}
