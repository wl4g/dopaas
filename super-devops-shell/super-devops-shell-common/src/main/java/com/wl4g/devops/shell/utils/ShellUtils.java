package com.wl4g.devops.shell.utils;

import com.wl4g.devops.shell.annotation.ShellOption;
import static com.wl4g.devops.shell.utils.Reflections.*;

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
		copyOptionsProperties(target, source, DEFAULT_FIELD_COPYER);
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
