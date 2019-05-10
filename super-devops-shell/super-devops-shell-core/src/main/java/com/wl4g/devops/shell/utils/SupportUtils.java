package com.wl4g.devops.shell.utils;

import static java.lang.reflect.Modifier.*;

import com.wl4g.devops.common.utils.bean.BeanUtils2;
import com.wl4g.devops.shell.annotation.ShellOption;
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
		BeanUtils2.copyBean(target, source, f -> {
			if (nativeType(f.getType())) {
				int mod = f.getModifiers();
				return f.getAnnotation(ShellOption.class) == null || isFinal(mod) || isTransient(mod) || isStatic(mod)
						|| isNative(mod) || isAbstract(mod) || isInterface(mod);
			}
			return false;
		});

	}

}
