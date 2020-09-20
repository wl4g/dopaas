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
package com.wl4g.devops.dts.codegen.utils;

import static java.util.Objects.isNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * {@link RenderingJacksonUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public final class RenderingJacksonUtils {

	/**
	 * Object to JSON strings.
	 * 
	 * @param object
	 * @return
	 */
	public static String toJSONString(Object object) {
		if (isNull(object))
			return null;
		try {
			return defaultObjectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
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
			return defaultObjectMapper.readValue(content, valueTypeRef);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Internal rendering model jackson serialization property filter. </br>
	 * 
	 * <pre>
	 * 
	 * &#64;JsonFilter({@link InnerRenderingModelPropertyFilter#FILTER_ID})
	 * public class MyBean {
	 * 
	 * }
	 * 
	 * </pre>
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-09-20
	 * @sine v1.0.0
	 * @see
	 */
	public static final class InnerRenderingModelPropertyFilter extends SimpleBeanPropertyFilter {

		@Override
		protected boolean include(BeanPropertyWriter writer) {
			return true;
		}

		@Override
		protected boolean include(PropertyWriter writer) {
			return true;
		}

		@Override
		public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
				throws Exception {
			RenderingProperty anno = writer.findAnnotation(RenderingProperty.class);
			if (isNull(anno)) {
				writer.serializeAsOmittedField(pojo, jgen, provider);
			} else {
				super.serializeAsField(pojo, jgen, provider, writer);
			}
		}

	}

	/**
	 * Whether the property fields of the annotation system bean will be
	 * serialized to the rendering model.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-09-20
	 * @sine v1.0.0
	 * @see
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RenderingProperty {
	}

	/** Jackson filter id of {@link InnerRenderingModelPropertyFilter} */
	public static final String FILTER_ID = "renderingModelFilterId";

	/**
	 * Default {@link ObjectMapper} instance.
	 */
	private static final ObjectMapper defaultObjectMapper = new ObjectMapper();

	static {
		defaultObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		defaultObjectMapper
				.setFilterProvider(new SimpleFilterProvider().addFilter(FILTER_ID, new InnerRenderingModelPropertyFilter()));
	}

}
