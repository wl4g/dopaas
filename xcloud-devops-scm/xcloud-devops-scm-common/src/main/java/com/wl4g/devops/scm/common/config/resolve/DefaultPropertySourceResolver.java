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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.scm.common.config.HoconPropertySource;
import com.wl4g.devops.scm.common.config.TomlPropertySource;
import com.wl4g.devops.scm.common.config.JsonPropertySource;
import com.wl4g.devops.scm.common.config.PropertiesPropertySource;
import com.wl4g.devops.scm.common.config.ScmPropertySource;
import com.wl4g.devops.scm.common.config.XmlPropertySource;
import com.wl4g.devops.scm.common.config.YamlMapPropertySource;

/**
 * {@link DefaultPropertySourceResolver}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-08-20
 * @sine v1.0.0
 * @see
 */
public class DefaultPropertySourceResolver implements PropertySourceResolver {

	/**
	 * Repository of {@link ScmPropertySource}
	 */
	public static final Map<String[], Class<? extends ScmPropertySource<?>>> PRPERTY_SOURCE_TYPE = new ConcurrentHashMap<>();

	static {
		PRPERTY_SOURCE_TYPE.put(new String[] { "yaml", "yml" }, YamlMapPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(new String[] { "properties" }, PropertiesPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(new String[] { "json" }, JsonPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(new String[] { "hocon" }, HoconPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(new String[] { "ini", "toml" }, TomlPropertySource.class);
		PRPERTY_SOURCE_TYPE.put(new String[] { "xml" }, XmlPropertySource.class);
	}

}
