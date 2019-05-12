package com.wl4g.devops.common.utils.bean;

import java.lang.reflect.Field;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.utils.reflect.ReflectionUtils2.*;
import static com.wl4g.devops.common.utils.reflect.Types.*;

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
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * It will contain all the fields defined by all parent or superclasses. At
	 * the same time, the target and the source object must be compatible.
	 * 
	 * @param target
	 *            The target object to copy to
	 * @param source
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @param fc
	 *            Customizable copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void copyFullProperties(Object target, Object source, FieldFilter ff, FieldCopyer fc)
			throws IllegalArgumentException, IllegalAccessException {
		Assert.isTrue((target != null && source != null && ff != null && fc != null),
				"Target and source field filter, field copyer must not be null");

		// Check if the target is compatible with the source object
		Class<?> targetClass = target.getClass(), sourceClass = source.getClass();
		Assert.state(isCompatibleType(target.getClass(), source.getClass()),
				String.format("Incompatible two objects, target class: %s, source class: %s", targetClass, sourceClass));

		Class<?> cls = target.getClass(); // [MARK0]
		do {
			copyDeepProperties(cls, target, source, ff, fc);
		} while ((cls = cls.getSuperclass()) != Object.class);
	}

	/**
	 * Calls the given callback on all fields of the target class, recursively
	 * running the class hierarchy up to copy all declared fields.</br>
	 * Note: that it does not contain fields defined by the parent or super
	 * class. At the same time, the target and the source object must be
	 * compatible.
	 * 
	 * @param hierarchyClass
	 *            The level of the class currently copied to (upward recursion)
	 * @param target
	 *            The target object to copy to
	 * @param source
	 *            Source object
	 * @param ff
	 *            Field filter
	 * @param fc
	 *            Customizable copyer
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void copyDeepProperties(Class<?> hierarchyClass, Object target, Object source, FieldFilter ff, FieldCopyer fc)
			throws IllegalArgumentException, IllegalAccessException {
		Assert.state(hierarchyClass != null, "Object hierarchy class is null");
		Assert.notNull(ff, "Field filter must not be null");
		Assert.notNull(fc, "Field copyer must not be null");

		// If the source is empty, skip the current level copy.
		if (source == null || target == null) {
			return;
		}

		// Recursive traversal matching and processing
		Class<?> sourceClass = source.getClass();
		for (Field tf : getDeclaredFields(hierarchyClass)) {
			tf.setAccessible(true);
			Object targetPropertyValue = tf.get(target); // See:[MARK0]

			Object sourcePropertyValue = null;
			Field sf = findField(sourceClass, tf.getName());
			if (sf != null) {
				sf.setAccessible(true);
				sourcePropertyValue = sf.get(source);
			}

			// If the source is not null and the target is null, the source can
			// be assigned directly to the target.
			if (targetPropertyValue == null) {
				if (fc != null) {
					fc.doCopy(target, tf, sf, targetPropertyValue, sourcePropertyValue);
				}
			} else if (isBaseType(tf.getType())) { // Based type?
				// Filter matching property
				if (sourcePropertyValue != null && ff.match(tf, targetPropertyValue, sourcePropertyValue)) {
					if (fc != null) {
						fc.doCopy(target, tf, sf, targetPropertyValue, sourcePropertyValue);
					}
				}
			} else {
				copyDeepProperties(tf.getType(), targetPropertyValue, sourcePropertyValue, ff, fc);
			}
		}

	}

	/**
	 * Enhanced callback optionally used to filter fields to be operated on by a
	 * field callback.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldFilter {

		/**
		 * Filter operations using the given field.
		 * 
		 * @param f
		 * @param targetProperty
		 * @param sourceProperty
		 * @return
		 */
		default public boolean match(Field f, Object targetProperty, Object sourceProperty) {
			return true;
		}
	}

	/**
	 * Enhanced callback interface invoked on each field in the hierarchy.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月11日
	 * @since
	 */
	public static interface FieldCopyer {

		/**
		 * Use the given field copy operation.
		 * 
		 * @param target
		 * @param tf
		 * @param sf
		 * @param targetPropertyValue
		 * @param sourcePropertyValue
		 * @throws IllegalAccessException
		 * @throws IllegalArgumentException
		 */
		default public void doCopy(Object target, Field tf, Field sf, Object targetPropertyValue, Object sourcePropertyValue)
				throws IllegalArgumentException, IllegalAccessException {
			tf.setAccessible(true);
			tf.set(target, sourcePropertyValue);
		}
	}

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		B b1 = new B();
		b1.bb = "22";
		A a1 = new A();
		a1.cc = "33";
		// a1.b = b1;

		B b2 = new B();
		b2.bb = "222";
		A a2 = new A();
		a2.cc = "333";
		a2.b = b2;

		System.out.println(a1);
		copyFullProperties(a1, a2, new FieldFilter() {
		}, new FieldCopyer() {
		});
		System.out.println(a1);

		System.out.println("=================");

		B b3 = new B();
		b3.bb = "22";
		A a3 = new A();
		a3.cc = "33";
		a3.b = b3;

		C c3 = new C();
		c3.cc = "c33";

		System.out.println(a3);
		copyFullProperties(a3, c3, new FieldFilter() {
		}, new FieldCopyer() {
		});
		System.out.println(a3);

		System.out.println("=================");
	}

	public static class A extends C {
		B b;

		@Override
		public String toString() {
			return "A [b=" + b + ", cc=" + cc + "]";
		}

	}

	public static class B {
		String bb;

		@Override
		public String toString() {
			return "B [bb=" + bb + "]";
		}

	}

	public static class C {
		String cc;

		@Override
		public String toString() {
			return "C [cc=" + cc + "]";
		}

	}

}
