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
package com.wl4g.devops.common.utils.bean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import static com.wl4g.devops.common.utils.reflect.Types.*;

/**
 * Bean and Map Convert.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月28日
 * @since
 */
public final class BeanMapConvert {

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
		return Collections.unmodifiableMap(doWithDeepFields(null, this.getObject(), new LinkedHashMap<String, Object>()));
	}

	private Map<String, Object> doWithDeepFields(String memberOfParent, Object obj, Map<String, Object> properties) {
		Assert.notNull(obj, "The obj argument must be null");
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String memberName = property.getName();
				// Filter class property
				if (!memberName.equals("class")) {
					Object value = ReflectionUtils.invokeMethod(property.getReadMethod(), obj);
					if (isBaseType(property.getPropertyType())) {
						properties.put(link(memberOfParent, memberName), value);
					} else {
						this.doWithDeepFields(memberName, value, properties);
					}
				}
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
		return properties;
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

	private static String link(String memberOfParent, String memberName) {
		if (memberOfParent != null && !memberOfParent.equalsIgnoreCase("null")) {
			return memberOfParent + "." + memberName;
		}
		return memberName;
	}

}