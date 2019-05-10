package com.wl4g.devops.common.utils.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

/**
 * Enhanced static convenience methods for JavaBeans: for instantiating beans,
 * checking bean property types, copying bean properties, etc.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月10日
 * @since
 */
public abstract class BeanUtils2 extends BeanUtils {

	/**
	 * Ignore properties cache.
	 */
	final private static Map<Class<?>, List<String>> ignoreCache = new ConcurrentHashMap<>();

	/**
	 * Recursively copying from the source bean to the target bean, including
	 * references to all parent class attributes and member attributes
	 * 
	 * @param target
	 *            target the target bean
	 * @param source
	 *            the source bean
	 * @param ff
	 *            the filter that determines the fields to apply the callback to
	 */
	public static <T> void copyBean(T target, T source, FieldFilter ff) {
		Class<?> clazz = target.getClass();

		// Cache ignore properties
		List<String> ignores = ignoreCache.get(clazz);

		if (CollectionUtils.isEmpty(ignores)) {
			// Filter by conditions
			List<String> _ignores = new ArrayList<>();
			ReflectionUtils.doWithFields(clazz, fcField -> _ignores.add(fcField.getName()), ff);
			ignoreCache.put(clazz, (ignores = _ignores));
		}

		// Bean copys
		BeanUtils.copyProperties(source, target, ignores.toArray(new String[] {}));
	}

}
