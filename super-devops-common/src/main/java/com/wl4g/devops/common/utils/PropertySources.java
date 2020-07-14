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
package com.wl4g.devops.common.utils;

import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.Assert;

import com.google.common.base.Charsets;

/**
 * YAML/Properties source check utility.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月30日
 * @since
 */
public abstract class PropertySources {

	public static Map<String, Object> resolve(Type type, String content) {
		Assert.notNull(type, "The 'type' argument must be null");
		Assert.notNull(content, "The 'content' argument must be null");
		return type.getHandle().resolve(content);
	}

	public static enum Type {
		YML(new YamlResolverHandle()), YAML(new YamlResolverHandle()), PROPS(new PropertiesResolverHandle());

		private ResolverHandle handle;

		private Type(ResolverHandle handle) {
			this.handle = handle;
		}

		public ResolverHandle getHandle() {
			return handle;
		}

		public void setHandle(ResolverHandle handle) {
			this.handle = handle;
		}

		public static Type of(String name) {
			for (Type t : values()) {
				if (t.name().equalsIgnoreCase(String.valueOf(name))) {
					return t;
				}
			}
			throw new IllegalStateException(String.format(" 'name' : %s", String.valueOf(name)));
		}

	}

	public static interface ResolverHandle {
		Map<String, Object> resolve(String content);
	}

	public static class YamlResolverHandle implements ResolverHandle {

		@Override
		public Map<String, Object> resolve(String content) {
			YamlPropertiesFactoryBean ymlFb = new YamlPropertiesFactoryBean();

			ymlFb.setResources(new ByteArrayResource(content.getBytes(Charsets.UTF_8)));
			ymlFb.afterPropertiesSet();
			// Properties to map
			Map<String, Object> map = new HashMap<>();
			if (nonNull(ymlFb) && nonNull(ymlFb.getObject())) {
				ymlFb.getObject().forEach((k, v) -> map.put(valueOf(k), v));
			}
			return map;
		}

	}

	public static class PropertiesResolverHandle implements ResolverHandle {

		@Override
		public Map<String, Object> resolve(String content) {
			Map<String, Object> result = new HashMap<>();
			try {
				Properties prop = new Properties();
				prop.load(new StringReader(content));
				// Copy and check.
				prop.forEach((k, v) -> {
					result.put(String.valueOf(k), v);
				});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return result;
		}

	}

}