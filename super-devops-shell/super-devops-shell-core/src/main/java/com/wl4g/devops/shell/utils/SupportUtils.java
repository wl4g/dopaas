package com.wl4g.devops.shell.utils;

import java.lang.reflect.Field;

import com.wl4g.devops.common.utils.bean.BeanUtils2.FieldCopyer;
import com.wl4g.devops.common.utils.bean.BeanUtils2.FieldFilter;
import com.wl4g.devops.shell.annotation.ShellOption;
import static com.wl4g.devops.common.utils.bean.BeanUtils2.*;
import static com.wl4g.devops.common.utils.reflect.ReflectionUtils2.*;
import static com.wl4g.devops.shell.utils.Types.*;

/**
 * Shell CLI server support utility tools
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月10日
 * @since
 */
public abstract class SupportUtils {

	/**
	 * All attribute values of beans filled with the same class.
	 * 
	 * @param target
	 *            target the target bean
	 * @param source
	 *            the source bean
	 */
	public static <T> void copyOptionBean(T target, T source) {

		try {
			copyFullProperties(target, source, new FieldFilter() {
				@Override
				public boolean match(Field f, Object targetProperty, Object sourceProperty) {
					if (isBaseType(f.getType())) {
						int mod = f.getModifiers();
						return f.getAnnotation(ShellOption.class) == null && isSafetyModifier(mod);
					}
					return false;
				}
			}, new FieldCopyer() {
			});
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}

	}

}
