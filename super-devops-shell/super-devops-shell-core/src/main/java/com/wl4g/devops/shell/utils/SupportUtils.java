package com.wl4g.devops.shell.utils;

import java.lang.reflect.Field;

import com.wl4g.devops.common.utils.bean.BeanUtils2.FieldFilter;
import com.wl4g.devops.shell.annotation.ShellOption;
import static com.wl4g.devops.common.utils.bean.BeanUtils2.*;
import static com.wl4g.devops.common.utils.reflect.ReflectionUtils2.*;

/**
 * Shell CLI server support utility tools
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月10日
 * @since
 */
public abstract class SupportUtils {

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
		try {
			copyFullProperties(target, source, new FieldFilter() {
				@Override
				public boolean match(Field f, Object sourcePropertyValue) {
					// [MARK0], See:[AbstractActuator.MARK3]
					int mod = f.getModifiers();
					return f.getAnnotation(ShellOption.class) != null && isSafetyModifier(mod);
				}
			});
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}

}
