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
package com.wl4g.devops.scm.common.config.resolve;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.component.common.reflect.ObjectInstantiators;
import com.wl4g.devops.scm.common.config.HoconConfigSource;
import com.wl4g.devops.scm.common.config.TomlConfigSource;
import com.wl4g.devops.scm.common.config.JsonConfigSource;
import com.wl4g.devops.scm.common.config.PropertiesConfigSource;
import com.wl4g.devops.scm.common.config.ScmConfigSource;
import com.wl4g.devops.scm.common.config.XmlConfigSource;
import com.wl4g.devops.scm.common.config.YamlMapPropertySource;
import com.wl4g.devops.scm.common.exception.UnknownPropertySourceException;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

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
	public ScmConfigSource resolve(ConfigProfile profile, String sourceContent) {
		// Gets source type.
		Class<? extends ScmConfigSource> soruceClass = getPropertySourceOfType(profile.getType());
		notNull(soruceClass, UnknownPropertySourceException.class, "Unsupported property source of configuration format: %s",
				profile.getType());

		// New property source.
		ScmConfigSource source = ObjectInstantiators.newInstance(soruceClass);

		// Read & parsing.
		source.read(profile, sourceContent);

		return source;
	}

	/**
	 * Gets {@link ScmConfigSource} class by type.
	 * 
	 * @param type
	 * @return
	 */
	private Class<? extends ScmConfigSource> getPropertySourceOfType(String type) {
		return PRPERTY_SOURCE_TYPE.entrySet().stream()
				.filter(e -> e.getKey().stream().filter(t -> t.equalsIgnoreCase(type)).findFirst().isPresent())
				.map(e -> e.getValue()).findFirst().orElse(null);
	}

	/**
	 * Property source definitions of {@link ScmConfigSource}
	 */
	public static final Map<List<String>, Class<? extends ScmConfigSource>> PRPERTY_SOURCE_TYPE = new ConcurrentHashMap<>();

	static {
		PRPERTY_SOURCE_TYPE.put(asList("yaml", "yml"), YamlMapPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(asList("properties"), PropertiesConfigSource.class);
		PRPERTY_SOURCE_TYPE.put(asList("json"), JsonConfigSource.class);
		PRPERTY_SOURCE_TYPE.put(asList("hocon", "conf"), HoconConfigSource.class);
		PRPERTY_SOURCE_TYPE.put(asList("ini", "toml"), TomlConfigSource.class);
		PRPERTY_SOURCE_TYPE.put(asList("xml"), XmlConfigSource.class);
	}

}