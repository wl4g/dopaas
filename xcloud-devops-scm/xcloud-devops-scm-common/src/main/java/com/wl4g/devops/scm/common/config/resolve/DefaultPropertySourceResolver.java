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
package com.wl4g.devops.scm.common.config.resolve;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.components.common.reflect.ObjectInstantiators;
import com.wl4g.devops.scm.common.config.HoconPropertySource;
import com.wl4g.devops.scm.common.config.TomlPropertySource;
import com.wl4g.devops.scm.common.config.JsonPropertySource;
import com.wl4g.devops.scm.common.config.PropertiesPropertySource;
import com.wl4g.devops.scm.common.config.ScmPropertySource;
import com.wl4g.devops.scm.common.config.XmlPropertySource;
import com.wl4g.devops.scm.common.config.YamlMapPropertySource;
import com.wl4g.devops.scm.common.exception.UnsupportedPropertySourceException;

/**
 * {@link DefaultPropertySourceResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-20
 * @sine v1.0.0
 * @see
 */
public class DefaultPropertySourceResolver implements PropertySourceResolver {

	@Override
	public ScmPropertySource resolve(String sourceType, String sourceContent) {
		// Gets source type.
		Class<? extends ScmPropertySource> cls = getPropertySourceOfType(sourceType);
		notNull(cls, UnsupportedPropertySourceException.class, "Unsupported property source of type: %s", sourceType);

		// New property source.
		ScmPropertySource source = ObjectInstantiators.newInstance(cls);

		// Read and parsing.
		source.read(sourceContent);

		return source;
	}

	/**
	 * Gets {@link ScmPropertySource} class by type.
	 * 
	 * @param type
	 * @return
	 */
	private Class<? extends ScmPropertySource> getPropertySourceOfType(String type) {
		return PRPERTY_SOURCE_TYPE.entrySet().stream()
				.filter(e -> e.getKey().stream().filter(t -> t.equalsIgnoreCase(type)).findFirst().isPresent())
				.map(e -> e.getValue()).findFirst().orElse(null);
	}

	/**
	 * Property source definitions of {@link ScmPropertySource}
	 */
	public static final Map<List<String>, Class<? extends ScmPropertySource>> PRPERTY_SOURCE_TYPE = new ConcurrentHashMap<>();

	static {
		PRPERTY_SOURCE_TYPE.put(asList("yaml", "yml"), YamlMapPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(asList("properties"), PropertiesPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(asList("json"), JsonPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(asList("hocon"), HoconPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(asList("ini", "toml"), TomlPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(asList("xml"), XmlPropertySource.class);
	}

}
