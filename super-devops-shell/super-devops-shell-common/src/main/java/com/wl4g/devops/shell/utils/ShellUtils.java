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

import com.wl4g.devops.shell.annotation.ShellOption;
import static com.wl4g.devops.shell.utils.Reflections.*;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Shell CLI server support utility tools
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月10日
 * @since
 */
public abstract class ShellUtils extends BeanUtils2 {

	/**
	 * Execute a copy from the source object to the target object. Note that it
	 * will deeply recurse all parent or superclass and application property
	 * fields, and only contain fields annotated with {@link ShellOption}
	 * 
	 * @param target
	 *            target the target bean
	 * @param source
	 *            the source bean
	 */
	public static <T> void copyOptionsProperties(T target, T source) {
		copyOptionsProperties(target, source, (targetAttach, tf, sf, sourcePropertyValue) -> {
			tf.setAccessible(true);
			Object obj = targetAttach;
			Object value = sourcePropertyValue;
			if (value == null) { // Using default-value
				ShellOption spt = sf.getAnnotation(ShellOption.class);
				if (spt != null && !isBlank(spt.defaultValue())) {
					value = Types.convertToBaseOrSimpleSet(spt.defaultValue(), tf.getType());
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
	 * @param target
	 *            target the target bean
	 * @param source
	 *            the source bean
	 * @param fc
	 *            Customizable copyer
	 */
	public static <T> void copyOptionsProperties(T target, T source, FieldCopyer fc) {
		try {
			copyFullProperties(target, source, (f, sourcePropertyValue) -> {
				// [MARK0], See:[AbstractActuator.MARK4]
				return f.getAnnotation(ShellOption.class) != null && isSafetyModifier(f.getModifiers());
			}, fc);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}

}