package com.wl4g.devops.common.utils.bean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Bean and Map Convert.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月28日
 * @since
 */
public final class BeanMapConvert {
	final private static Collection<Class<?>> nativeClasss = new ArrayList<>();
	final private static Collection<String> nativePackage = new ArrayList<>();

	static {
		nativeClasss.add(int.class);
		nativeClasss.add(long.class);
		nativeClasss.add(double.class);
		nativeClasss.add(float.class);
		nativeClasss.add(byte.class);
		nativeClasss.add(String.class);
		nativeClasss.add(Integer.class);
		nativeClasss.add(Long.class);
		nativeClasss.add(Double.class);
		nativeClasss.add(Float.class);
		nativeClasss.add(Byte.class);
		nativeClasss.add(Class.class);
		nativePackage.add("com.sun.");
		nativePackage.add("sun.");
		nativePackage.add("java.");
		nativePackage.add("javax.");
		nativePackage.add("jdk.");
		nativePackage.add("javafx.");
		nativePackage.add("oracle.");
	}

	private Object bean;

	public BeanMapConvert(Object bean) {
		super();
		this.bean = bean;
	}

	/**
	 * This bean map to URI parameters
	 * 
	 * @return
	 */
	public String toUriParmaters() {
		return toUriParmaters(getBeanMap());
	}

	public Map<String, Object> getBeanMap() {
		return Collections.unmodifiableMap(getDeepBeanMap(null, this.getObject(), new LinkedHashMap<String, Object>()));
	}

	private Map<String, Object> getDeepBeanMap(String memberOfParent, Object obj, Map<String, Object> ret) {
		Assert.notNull(obj, "The obj argument must be null");
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String memberName = property.getName();
				// Filter class property
				if (!memberName.equals("class")) {
					Object value = ReflectionUtils.invokeMethod(property.getReadMethod(), obj);
					if (isNativeClass(property.getPropertyType())) {
						ret.put(links(memberOfParent, memberName), value);
					} else {
						this.getDeepBeanMap(memberName, value, ret);
					}
				}
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
		return ret;
	}

	public Object getObject() {
		return bean;
	}

	/**
	 * Map to URI parameters
	 * 
	 * @param param
	 * @return
	 */
	public static String toUriParmaters(Map<String, Object> param) {
		// To URI parameters string
		StringBuffer uri = new StringBuffer();
		for (Iterator<?> it = param.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			uri.append(key);
			uri.append("=");
			// Prevents any occurrence of a value string null
			Object value = param.get(key);
			if (value != null) {
				uri.append(value); // "null"
			}
			if (it.hasNext()) {
				uri.append("&");
			}
		}
		return uri.toString();
	}

	private static String links(String memberOfParent, String memberName) {
		if (memberOfParent != null && !memberOfParent.equalsIgnoreCase("null")) {
			return memberOfParent + "." + memberName;
		}
		return memberName;
	}

	private static boolean isNativeClass(Class<?> clazz) {
		return nativeClasss.contains(clazz) || nativePackage.contains(clazz.getName());
	}

}
