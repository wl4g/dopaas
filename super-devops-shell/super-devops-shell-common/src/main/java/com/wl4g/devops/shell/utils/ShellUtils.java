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
package com.wl4g.devops.shell.utils;

import com.wl4g.devops.common.utils.bean.BeanUtils2;
import com.wl4g.devops.common.utils.reflect.Types;
import com.wl4g.devops.shell.annotation.ShellOption;

import static java.util.Objects.nonNull;
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
	 * @param fp
	 *            Customizable copyer
	 */
	public static <T> void copyOptionsProperties(T target, T source, FieldProcessor fp) {
		try {
			doWithDeepFields(target, source, targetField -> {
				// [MARK0], See:[AbstractActuator.MARK4]
				return nonNull(targetField.getAnnotation(ShellOption.class)) && DEFAULT_FIELD_FILTER.matches(targetField);
			}, fp);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}

}