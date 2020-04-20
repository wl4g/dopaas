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
package com.wl4g.devops.common.utils.serialize;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

/**
 * Google Protostuff serialize utils
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月11日
 * @since
 */
public abstract class ProtostuffUtils {

	/**
	 * Class sets requiring serialization/deserialization using wrapper classes
	 */
	final private static List<Class<?>> warpperSet = new ArrayList<>(16);

	/**
	 * Serialized/deserialized wrapper class Schema objects
	 */
	@SuppressWarnings("rawtypes")
	final private static Schema<SerializableWrapper> warpperSchema = RuntimeSchema.createFrom(SerializableWrapper.class);

	/**
	 * Caching object and object schema information set
	 */
	final private static Map<Class<?>, Schema<?>> schemaCache = Maps.newConcurrentMap();

	/**
	 * Spring packaging compatible object creator
	 */
	final private static Objenesis objenesis = new ObjenesisStd(true);

	/**
	 * Improving memory allocation speed by using current thread pool caching
	 */
	final private static ThreadLocal<LinkedBuffer> bufferCache = ThreadLocal.withInitial(() -> LinkedBuffer.allocate());

	static {
		// Predefined objects that Protostuff cannot directly
		// serialize/deserialize
		warpperSet.add(List.class);
		warpperSet.add(ArrayList.class);
		warpperSet.add(CopyOnWriteArrayList.class);
		warpperSet.add(LinkedList.class);
		warpperSet.add(Stack.class);
		warpperSet.add(Vector.class);

		warpperSet.add(Map.class);
		warpperSet.add(HashMap.class);
		warpperSet.add(TreeMap.class);
		warpperSet.add(Hashtable.class);
		warpperSet.add(SortedMap.class);
		warpperSet.add(Map.class);

		warpperSet.add(Object.class);
	}

	/**
	 * serialized objects
	 *
	 * @param bean
	 *            Objects that need serialization
	 * @param <T>
	 *            Types of serialized objects
	 * @return Serialized binary array
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> byte[] serialize(T bean) {
		if (bean == null) {
			return null;
		}

		Class<T> clazz = (Class<T>) bean.getClass();
		// Simple direct turn
		if (isSimpleType(bean.getClass())) {
			return bean.toString().getBytes(Charsets.UTF_8);
		}

		LinkedBuffer buf = bufferCache.get();
		try {
			Object serializeObj = bean;
			Schema schema = warpperSchema;
			if (!warpperSet.contains(clazz) && !clazz.isArray()) {
				schema = getSchema(clazz);
			} else {
				serializeObj = SerializableWrapper.builder(bean);
			}
			return ProtostuffIOUtil.toByteArray(serializeObj, schema, buf);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			buf.clear();
		}
	}

	/**
	 * Deserialized object
	 *
	 * @param data
	 *            Binary arrays requiring deserialization
	 * @param clazz
	 *            Deserialized object class
	 * @param <T>
	 *            Object types after deserialization
	 * @return Deserialized object set
	 */
	public static <T> T deserialize(byte[] data, Class<T> clazz) {
		notNullOf(clazz, "objectClass");
		if (isNull(data))
			return null;

		try {
			// Simple type conversion
			T bean = simpleConversion(data, clazz);
			if (bean == null) {
				if (!warpperSet.contains(clazz) && !clazz.isArray()) {
					bean = objenesis.newInstance(clazz); // java原生实例化必须调用constructor故使用objenesis
					ProtostuffIOUtil.mergeFrom(data, bean, getSchema(clazz));
					return bean;
				} else {
					SerializableWrapper<T> wrapper = new SerializableWrapper<>();
					ProtostuffIOUtil.mergeFrom(data, wrapper, warpperSchema);
					return wrapper.getData();
				}
			}
			return bean;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Getting schema for serialized object types
	 *
	 * @param cls
	 *            Class of serialized objects
	 * @param <T>
	 *            Types of serialized objects
	 * @return Schema of serialized object type
	 */
	@SuppressWarnings({ "unchecked" })
	private static <T> Schema<T> getSchema(Class<T> cls) {
		Schema<T> schema = (Schema<T>) schemaCache.get(cls);
		if (schema == null) {
			schema = RuntimeSchema.createFrom(cls);
			schemaCache.put(cls, schema);
		}
		return schema;
	}

	/**
	 * Java simple type conversion
	 * 
	 * @param bytes
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> T simpleConversion(byte[] bytes, Class<T> clazz) {
		Object object = null;
		if (isSimpleType(clazz)) {
			if (clazz == int.class || clazz == Integer.class) {
				object = Integer.valueOf(new String(bytes));
			} else if (clazz == long.class || clazz == Long.class) {
				object = Long.valueOf(new String(bytes));
			} else if (clazz == double.class || clazz == Double.class) {
				object = Double.valueOf(new String(bytes));
			} else if (clazz == byte.class || clazz == Byte.class) {
				object = Byte.valueOf(new String(bytes));
			} else if (clazz == String.class) {
				object = new String(bytes);
			}
		}
		return (T) object;
	}

	/**
	 * Is simple type
	 * 
	 * @param clazz
	 * @return
	 */
	private static boolean isSimpleType(Class<?> clazz) {
		return (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || CharSequence.class.isAssignableFrom(clazz));
	}

	/**
	 * 序列化/反序列化对象包装类 专为基于 Protostuff 进行序列化/反序列化而定义。 Protostuff
	 * 是基于POJO进行序列化和反序列化操作。 如果需要进行序列化/反序列化的对象不知道其类型，不能进行序列化/反序列化；
	 * 比如Map、List、String、Enum等是不能进行正确的序列化/反序列化。
	 * 因此需要映入一个包装类，把这些需要序列化/反序列化的对象放到这个包装类中。 这样每次 Protostuff
	 * 都是对这个类进行序列化/反序列化,不会出现不能/不正常的操作出现
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月11日
	 * @since
	 * @param <T>
	 */
	private static class SerializableWrapper<T> {

		private T data;

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public static <T> SerializableWrapper<T> builder(T data) {
			SerializableWrapper<T> wrapper = new SerializableWrapper<>();
			wrapper.setData(data);
			return wrapper;
		}

	}

}