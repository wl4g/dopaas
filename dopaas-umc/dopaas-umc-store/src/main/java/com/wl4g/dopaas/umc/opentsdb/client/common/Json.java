/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.umc.opentsdb.client.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * 配置一个单例的jackson objectMapper，同时防止外部修改mapper的配置
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午1:21
 * @Version: 1.0
 */
public class Json {

	private static final ObjectMapper instance;

	static {
		instance = new ObjectMapper();

		// 支持java8
		instance.registerModule(new JavaTimeModule()).registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module());
		instance.findAndRegisterModules();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		instance.setDateFormat(dateFormat);
		// 允许对象忽略json中不存在的属性
		instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 允许出现特殊字符和转义符
		instance.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		// 允许出现单引号
		instance.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		// 忽视为空的属性
		instance.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
	}

	/***
	 * 将对象序列化为json字符串
	 * 
	 * @param value
	 *            具体对象
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String writeValueAsString(Object value) throws JsonProcessingException {
		return instance.writeValueAsString(value);
	}

	/***
	 * 将json字符串反序列化为T类型的对象
	 * 
	 * @param content
	 *            json字符串
	 * @param valueType
	 *            数据类型
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public static <T> T readValue(String content, Class<T> valueType) throws IOException {
		return instance.readValue(content, valueType);
	}

	/***
	 * 将json反序列化为集合，集合类型是collectionClass，泛型是elementClass
	 * 
	 * @param content
	 *            json字符串
	 * @param collectionClass
	 *            集合类型
	 * @param elementClass
	 *            泛型
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T readValue(String content, Class<? extends Collection> collectionClass, Class<?> elementClass)
			throws IOException {
		CollectionType collectionType = instance.getTypeFactory().constructCollectionType(collectionClass, elementClass);
		return instance.readValue(content, collectionType);
	}

}