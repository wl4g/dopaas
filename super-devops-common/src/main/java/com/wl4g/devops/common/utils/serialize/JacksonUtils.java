package com.wl4g.devops.common.utils.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JacksonUtils {
	final private static ObjectMapper mapper = new ObjectMapper();

	public static String toJSONString(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parseJSON(String content, Class<T> clazz) {
		if (content == null) {
			return null;
		}
		try {
			return mapper.readValue(content, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
