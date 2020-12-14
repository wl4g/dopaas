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
package com.wl4g.devops.doc.plugin.swagger.util;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.wl4g.devops.doc.plugin.swagger.config.oas3.Oas3ServerVariable;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.servers.ServerVariable;

/**
 * Supported output formats.
 */
public enum OutputFormater {

	JSON(new JSONWriter()), YAML(new YAMLWriter());

	private final SwaggerWriter writer;

	OutputFormater(SwaggerWriter writer) {
		this.writer = writer;
	}

	public void write(Object document, Object out, boolean prettyPrint) throws IOException {
		writer.write(document, out, prettyPrint);
	}

	/**
	 * Interface defining requirements for being able to write out Swagger
	 * instance to file.
	 */
	@FunctionalInterface
	interface SwaggerWriter {
		void write(Object document, Object out, boolean prettyPrint) throws IOException;

		default void writeValue(ObjectMapper mapper, Object document, Object out) throws IOException {
			if (out instanceof File) {
				mapper.writeValue((File) out, document);
			} else if (out instanceof OutputStream) {
				mapper.writeValue((OutputStream) out, document);
			} else if (out instanceof Writer) {
				mapper.writeValue((Writer) out, document);
			} else {
				throw new UnsupportedOperationException(format("No supported output parameter type.", out));
			}
		}
	}

	/**
	 * As the Maven plugin plugin does not support lambdas properly a real
	 * implementation is needed.
	 */
	static class JSONWriter implements SwaggerWriter {

		@Override
		public void write(Object document, Object out, boolean prettyPrint) throws IOException {
			ObjectMapper mapper = Json.mapper();
			mapper.addMixIn(ServerVariable.class, Oas3ServerVariable.ServerVariableMixin.class);
			if (prettyPrint) {
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
			}

			writeValue(mapper, document, out);
		}
	}

	/**
	 * As the Maven plugin plugin does not support lambdas properly a real
	 * implementation is needed.
	 */
	static class YAMLWriter implements SwaggerWriter {

		@Override
		public void write(Object document, Object out, boolean prettyPrint) throws IOException {
			ObjectMapper mapper = Yaml.mapper();
			mapper.addMixIn(ServerVariable.class, Oas3ServerVariable.ServerVariableMixin.class);
			writeValue(mapper, document, out);
		}
	}

}