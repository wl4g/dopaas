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
package com.wl4g.devops.components.tools.common.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

/**
 * Object manipulation tool class
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2017年4月13日
 * @since
 */
public abstract class JdkSerializeUtils {

	/**
	 * Annotate to object copying, only copy the methods that match.
	 * 
	 * @param annotation
	 * @param object
	 */
	public static void annotationToObject(Object annotation, Object object) {
		if (annotation != null && object != null) {
			Class<?> annotationClass = annotation.getClass();
			Class<?> objectClass = object.getClass();
			if (objectClass != null) {
				for (Method m : objectClass.getMethods()) {
					if (StringUtils.startsWith(m.getName(), "set")) {
						try {
							String s = StringUtils.uncapitalize(StringUtils.substring(m.getName(), 3));
							Object obj = annotationClass.getMethod(s).invoke(annotation);
							if (obj != null && !"".equals(obj.toString())) {
								if (object == null) {
									object = objectClass.newInstance();
								}
								m.invoke(object, obj);
							}
						} catch (Exception e) {
							// 忽略所有设置失败方法
						}
					}
				}
			}
		}
	}

	/**
	 * Serialized object
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			if (object != null) {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				return baos.toByteArray();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				if (oos != null)
					oos.close();
				oos = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			try {
				if (baos != null)
					baos.close();
				baos = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		return null;
	}

	/**
	 * Deserialized object
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			if (bytes != null && bytes.length > 0) {
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return ois.readObject();
			}
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		} finally {
			try {
				if (ois != null)
					ois.close();
				ois = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			try {
				if (bais != null)
					bais.close();
				bais = null;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		return null;
	}

}