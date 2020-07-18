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
package com.wl4g.devops.components.tools.common.remoting.parse;

import com.fasterxml.jackson.databind.ser.FilterProvider;

/**
 * A simple holder for the POJO to serialize via
 * {@link MappingJackson2HttpMessageParser} along with further serialization
 * instructions to be passed in to the converter.
 *
 * <p>
 * On the server side this wrapper is added with a
 * {@code ResponseBodyInterceptor} after content negotiation selects the
 * converter to use but before the write.
 *
 * <p>
 * On the client side, simply wrap the POJO and pass it in to the
 * {@code RestTemplate}.
 */
public class MappingJacksonValue {

	private Object value;

	private Class<?> serializationView;

	private FilterProvider filters;

	private String jsonpFunction;

	/**
	 * Create a new instance wrapping the given POJO to be serialized.
	 * 
	 * @param value
	 *            the Object to be serialized
	 */
	public MappingJacksonValue(Object value) {
		this.value = value;
	}

	/**
	 * Modify the POJO to serialize.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Return the POJO that needs to be serialized.
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Set the serialization view to serialize the POJO with.
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
	 * @see com.fasterxml.jackson.annotation.JsonView
	 */
	public void setSerializationView(Class<?> serializationView) {
		this.serializationView = serializationView;
	}

	/**
	 * Return the serialization view to use.
	 * 
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
	 * @see com.fasterxml.jackson.annotation.JsonView
	 */
	public Class<?> getSerializationView() {
		return this.serializationView;
	}

	/**
	 * Set the Jackson filter provider to serialize the POJO with.
	 * 
	 * @since 4.2
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writer(FilterProvider)
	 * @see com.fasterxml.jackson.annotation.JsonFilter
	 * @see Jackson2ObjectMapperBuilder#filters(FilterProvider)
	 */
	public void setFilters(FilterProvider filters) {
		this.filters = filters;
	}

	/**
	 * Return the Jackson filter provider to use.
	 * 
	 * @since 4.2
	 * @see com.fasterxml.jackson.databind.ObjectMapper#writer(FilterProvider)
	 * @see com.fasterxml.jackson.annotation.JsonFilter
	 */
	public FilterProvider getFilters() {
		return this.filters;
	}

	/**
	 * Set the name of the JSONP function name.
	 */
	public void setJsonpFunction(String functionName) {
		this.jsonpFunction = functionName;
	}

	/**
	 * Return the configured JSONP function name.
	 */
	public String getJsonpFunction() {
		return this.jsonpFunction;
	}

}