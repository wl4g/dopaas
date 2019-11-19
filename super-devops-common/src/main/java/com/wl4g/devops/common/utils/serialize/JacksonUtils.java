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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * JACKSON utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月22日
 * @since
 */
public abstract class JacksonUtils {
	final private static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Object to JSON strings.
	 * 
	 * @param object
	 * @return
	 */
	public static String toJSONString(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Parse object from JSON strings.
	 * 
	 * @param content
	 * @param clazz
	 * @return
	 */
	public static <T> T parseJSON(String content, Class<T> clazz) {
		if (content == null) {
			return null;
		}
		try {
			return mapper.readValue(content, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Parse object from JSON strings.
	 * 
	 * @param content
	 * @param valueTypeRef
	 * @return
	 */
	public static <T> T parseJSON(String content, TypeReference<T> valueTypeRef) {
		if (content == null) {
			return null;
		}
		try {
			return mapper.readValue(content, valueTypeRef);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Convert value to target type.</br>
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#convertValue(Object,
	 *      Class)
	 * @param <T>
	 * @param bean
	 * @param toType
	 * @return
	 */
	public static <T> T convertBean(Object bean, Class<T> toType) {
		return mapper.convertValue(bean, toType);
	}

	/**
	 * Convert value to reference type.</br>
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#convertValue(Object,
	 *      TypeReference)
	 * @param <T>
	 * @param bean
	 * @param typeRef
	 * @return
	 */
	public static <T> T convertBean(Object bean, TypeReference<?> typeRef) {
		return mapper.convertValue(bean, typeRef);
	}

	/**
	 * Convert value to Java type.</br>
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#convertValue(Object,
	 *      JavaType)
	 * @param <T>
	 * @param bean
	 * @param toJavaType
	 * @return
	 */
	public static <T> T convertBean(Object bean, JavaType toJavaType) {
		return mapper.convertValue(bean, toJavaType);
	}

}